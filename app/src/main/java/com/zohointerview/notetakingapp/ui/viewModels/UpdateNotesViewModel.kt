package com.zohointerview.notetakingapp.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.evernote.edam.type.Note
import com.zohointerview.notetakingapp.database.getDatabase
import com.zohointerview.notetakingapp.domain.DomainNote
import com.zohointerview.notetakingapp.repository.NotesRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import android.content.Intent
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.startActivity
import androidx.work.*
import com.evernote.client.android.asyncclient.EvernoteCallback
import com.zohointerview.notetakingapp.ThirdParty.EdamNote
import com.zohointerview.notetakingapp.ThirdParty.EverNoteProvider
import com.zohointerview.notetakingapp.ThirdParty.asNote
import com.zohointerview.notetakingapp.ThirdParty.asNoteEntity
import com.zohointerview.notetakingapp.core.utils.Constants
import com.zohointerview.notetakingapp.core.utils.workmanagers.FetchNotesWorker
import com.zohointerview.notetakingapp.database.asSingleDomainNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


/****************************************************
 * View model class for fragment_country_detail
 *
 ****************************************************/
class UpdateNotesViewModel(
    note: DomainNote,
    app: Application
) : AndroidViewModel(app) {
    private val notesRepository = NotesRepository(getDatabase(app))

    // The MutableLiveData for the selected country
    // This is being received via safeArgs from the fragment_country_master
    private val _selectedNote = MutableLiveData<DomainNote>()

    // The  MutableLiveData for showing progress
    private val _shouldShowProgress = MutableLiveData<Boolean>()

    // The MutableLiveData for showing error
    private val _shouldShowError = MutableLiveData<Boolean>()


    // The backing field for the selected country
    val selectedNote: LiveData<DomainNote>
        get() = _selectedNote

    // The backing field for showing progress
    val shouldShowProgress: LiveData<Boolean>
        get() = _shouldShowProgress

    // The backing field for showing error
    val shouldShowError: LiveData<Boolean>
        get() = _shouldShowError



    // Initialize the _backing properties
    init {
        _selectedNote.value = note
        _shouldShowProgress.value = false
        _shouldShowError.value = false
    }

    fun updateNote(newNote: Note) {
        _shouldShowProgress.value = true

        viewModelScope.launch(Dispatchers.IO){

            try {
                EverNoteProvider.obtainNoteStoreClient().updateNote(newNote)
            } catch (e: Exception) {
                e.printStackTrace()
                _shouldShowError.postValue(false)
                notesRepository.updateNote(EdamNote(Arrays.asList(newNote)).asNoteEntity()[0])
            } finally {
                _shouldShowProgress.postValue(false)
            }
        }
    }

fun onNoteDeleted() {

            viewModelScope.launch(Dispatchers.IO){
            _shouldShowProgress.postValue(true)
            try {
                EverNoteProvider.obtainNoteStoreClient().deleteNote(_selectedNote.value?.guid)
               _selectedNote.value?.let{notesRepository.deleteNote(it.guid)}
                _shouldShowProgress.postValue(false)

            }
            catch(e: Exception)
            {
                Timber.d("Error : $e")
                _shouldShowError.postValue(true)
            }

            finally {
                _shouldShowProgress.postValue(false)

            }
//                EverNoteProvider.obtainNoteStoreClient()
//                    .deleteNoteAsync(_selectedNote.value?.guid, object :
//                        EvernoteCallback<Int> {
//                        override fun onSuccess(result: Int) {
////                            WorkManager.getInstance().enqueueUniqueWork(
////                                FetchNotesWorker.WORK_NAME,
////                                ExistingWorkPolicy.KEEP,
////                                oneTimeFetchNoteRequest
////                            )
//                            _shouldShowProgress.value = false
//
//                        }
//
//                        override fun onException(exception: Exception) {
//                            Timber.d("Error: " + exception.toString())
//                            _shouldShowProgress.value = false
//                            _shouldShowError.value = true
//
//
//                        }
                   // }
           // )


        }

    }

    fun onNotePinned()
    {

        viewModelScope.launch(Dispatchers.IO) {
            _shouldShowProgress.postValue(true)
            lateinit var tagToBeUpdated : String
            try {
                val currentNote =
                  EverNoteProvider.noteStoreClient.getNote(
                        selectedNote.value?.guid,
                        true,
                        true,
                        true,
                        true
                    )
                currentNote?.run {
                    unsetContent()
                    unsetResources()
                }
                val currentTag = currentNote?.tagGuids?.let {
                   EverNoteProvider.noteStoreClient.getNoteTagNames(
                        it[0]
                    )
                }
              tagToBeUpdated =
                    currentTag?.let { if (it.equals(Constants.TAG_NAME_PINNED)) Constants.TAG_NAME_OTHERS else Constants.TAG_NAME_PINNED }
                        ?: Constants.TAG_NAME_PINNED
                currentNote?.tagNames = Arrays.asList(tagToBeUpdated)
              EverNoteProvider.obtainNoteStoreClient()
                    .updateNote(currentNote)
                _shouldShowProgress.postValue(false)
            }
            catch(e : Exception)
            {
                var tagFromDb = selectedNote?.value?.guid?.let{notesRepository.getSharableNote(it).type} ?: Constants.TAG_NAME_OTHERS
              val newTag =  tagFromDb?.let { if (it.equals(Constants.TAG_NAME_PINNED)) Constants.TAG_NAME_OTHERS else Constants.TAG_NAME_PINNED }
                notesRepository.pinNote(_selectedNote.value!!.guid,newTag)
                notesRepository.fetchNotes()
                _shouldShowError.postValue(true)

            }
            finally{
                _shouldShowProgress.postValue(false)
            }

    }
    }

    /**
     * Setting the value to null post navigation
     * Also shouldShouldError is being cleared here
     */
    fun onNavigationComplete() {
        _selectedNote.value = null
        _shouldShowProgress.value = false
        _shouldShowError.value = false
    }





    }
