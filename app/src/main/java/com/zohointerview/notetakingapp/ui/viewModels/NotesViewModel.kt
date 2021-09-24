package com.zohointerview.notetakingapp.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.evernote.client.android.asyncclient.EvernoteCallback
import com.evernote.edam.type.Note
import com.zohointerview.notetakingapp.ThirdParty.EdamNote
import com.zohointerview.notetakingapp.ThirdParty.EverNoteProvider
import com.zohointerview.notetakingapp.ThirdParty.asNote
import com.zohointerview.notetakingapp.core.utils.Constants
import com.zohointerview.notetakingapp.core.utils.workmanagers.FetchNotesWorker
import com.zohointerview.notetakingapp.database.getDatabase
import com.zohointerview.notetakingapp.domain.DomainNote
import com.zohointerview.notetakingapp.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class NotesViewModel constructor(application: Application) : AndroidViewModel(application) {
    private val notesRepository = NotesRepository(getDatabase(application))
    val database = getDatabase(application)

    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    val oneTimeFetchNoteRequest = OneTimeWorkRequestBuilder<FetchNotesWorker>().setConstraints(constraints).build()


    val pinnedNotesList = notesRepository.pinnedNotes
    val otherNotesList = notesRepository.otherNotes
    val pinnedNotesCount = notesRepository.pinnedNotesCount

    private val _selectedNote = MutableLiveData<DomainNote>()
    val selectedNote: LiveData<DomainNote>
        get() = _selectedNote

    // The  MutableLiveData for showing progress
    private val _shouldShowProgress = MutableLiveData<Boolean>()

    // The MutableLiveData for showing error
    private val _shouldShowError = MutableLiveData<Boolean>()

    private val _selectedNoteLongClick = MutableLiveData<DomainNote>()
    val selectedNoteLongClick: LiveData<DomainNote>
        get() = _selectedNoteLongClick
    // The backing field for showing progress
    val shouldShowProgress: LiveData<Boolean>
        get() = _shouldShowProgress

    // The backing field for showing error
    val shouldShowError: LiveData<Boolean>
        get() = _shouldShowError

    private val _switchToolbar = MutableLiveData<Boolean>()
    val switchToolbar: LiveData<Boolean>
        get() = _switchToolbar

    fun onNoteClicked(selectedNote: DomainNote) {
        _selectedNote.value = selectedNote
    }

    fun onNoteLongClicked(selectedNote: DomainNote): Boolean {
        _selectedNoteLongClick.value = selectedNote
        _switchToolbar.value = true
        return true
    }

    /**
     * Setting the value to null post navigation
     */
    fun onNavigationComplete() {
        _selectedNote.value = null
        _selectedNoteLongClick.value
        _switchToolbar.value = false
        _shouldShowError.value = false
        _shouldShowError.value = false
    }

    fun onNoteCreated(newNote: Note) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeFetchNoteRequest = OneTimeWorkRequestBuilder<FetchNotesWorker>()
            .setConstraints(constraints)
            .build()
        EverNoteProvider.obtainNoteStoreClient().createNoteAsync(newNote, object :
            EvernoteCallback<Note> {
            override fun onSuccess(result: Note) {
                WorkManager.getInstance().enqueueUniqueWork(
                    FetchNotesWorker.WORK_NAME,
                    ExistingWorkPolicy.KEEP,
                    oneTimeFetchNoteRequest
                )
                _selectedNote.value = EdamNote(Arrays.asList(result)).asNote().get(0)
            }

            override fun onException(exception: Exception) {
                Timber.d("Error: " + exception.toString())
            }
        })
    }

    fun onNoteClosed() {
        _switchToolbar.value = false
    }



    fun onNoteDeleted() {
        EverNoteProvider.obtainNoteStoreClient()
            .deleteNoteAsync(_selectedNoteLongClick.value?.guid, object :
                EvernoteCallback<Int> {
                override fun onSuccess(result: Int) {
                    WorkManager.getInstance().enqueueUniqueWork(
                        FetchNotesWorker.WORK_NAME,
                        ExistingWorkPolicy.KEEP,
                        oneTimeFetchNoteRequest
                    )

                }

                override fun onException(exception: Exception) {
                    Timber.d("Error: " + exception.toString())
                }
            })
        _switchToolbar.value = false
    }

    fun onNotePinned() {
        _switchToolbar.value = false
        onNotePinnedUnpinned()
    }
    fun onNotePinnedUnpinned()
    {

        viewModelScope.launch(Dispatchers.IO) {
            _shouldShowProgress.postValue(true)
            lateinit var tagToBeUpdated : String
            try {
                val currentNote =
                    EverNoteProvider.noteStoreClient.getNote(
                        selectedNoteLongClick.value?.guid,
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
                var tagFromDb = selectedNoteLongClick?.value?.guid?.let{notesRepository.getSharableNote(it).type} ?: Constants.TAG_NAME_OTHERS
                val newTag =  tagFromDb?.let { if (it.equals(Constants.TAG_NAME_PINNED)) Constants.TAG_NAME_OTHERS else Constants.TAG_NAME_PINNED }
                notesRepository.pinNote(selectedNoteLongClick.value!!.guid,newTag)
                notesRepository.fetchNotes()
                _shouldShowError.postValue(true)

            }
            finally{
                _shouldShowProgress.postValue(false)
            }

        }
    }


}


