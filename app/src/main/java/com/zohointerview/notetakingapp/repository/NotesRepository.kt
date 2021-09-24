package com.zohointerview.notetakingapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.evernote.client.android.asyncclient.EvernoteCallback
import com.evernote.edam.notestore.NoteFilter
import com.evernote.edam.type.Note
import com.evernote.edam.type.NoteSortOrder
import com.zohointerview.notetakingapp.R
import com.zohointerview.notetakingapp.ThirdParty.EdamNote
import com.zohointerview.notetakingapp.ThirdParty.EverNoteProvider
import com.zohointerview.notetakingapp.ThirdParty.asNoteEntity
import com.zohointerview.notetakingapp.core.NotetakingApplication
import com.zohointerview.notetakingapp.core.utils.Constants
import com.zohointerview.notetakingapp.core.utils.NoteAppUtils
import com.zohointerview.notetakingapp.core.utils.workmanagers.FetchNotesWorker
import com.zohointerview.notetakingapp.database.NoteDatabase
import com.zohointerview.notetakingapp.database.NoteEntity
import com.zohointerview.notetakingapp.database.asNote
import com.zohointerview.notetakingapp.database.asSingleDomainNote
import com.zohointerview.notetakingapp.domain.DomainNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * Repository class which serves as the single source of notes data
 */
class NotesRepository(private val notesDatabase: NoteDatabase) {
    val isUpdtAsc = NoteAppUtils.getSharedPrefManager(NotetakingApplication.noteApplicationContext).getBoolean(NotetakingApplication.noteApplicationContext.getString(
        R.string.key_srt_new_btm),false)

    val pinnedNotes: LiveData<List<DomainNote>> =
        Transformations.map(notesDatabase.noteDatabaseDao.getPinnedNotes(Constants.TAG_NAME_PINNED)) {
            it.asNote()
        }
    val otherNotesDesc: LiveData<List<DomainNote>> =
        Transformations.map(notesDatabase.noteDatabaseDao.getOtherNotesByUpdtDesc(Constants.TAG_NAME_PINNED)) {
            it.asNote()
        }
    val otherNotesAsc: LiveData<List<DomainNote>> =
        Transformations.map(notesDatabase.noteDatabaseDao.getOtherNotesByUpdtAsc(Constants.TAG_NAME_PINNED)) {
            it.asNote()
        }
    val otherNotes = if(isUpdtAsc){otherNotesAsc} else{otherNotesDesc}
    val pinnedNotesCount: LiveData<Integer> = notesDatabase.noteDatabaseDao.getPinnedNotesCount(Constants.TAG_NAME_PINNED)

    fun getSharableNote(guid :String) = (notesDatabase.noteDatabaseDao.getSingleNote(guid)).asSingleDomainNote()
    fun pinNote(guid: String, tagname: String) {
        (notesDatabase.noteDatabaseDao.updatePin(guid,tagname))
    }

  fun deleteNote(note : String) = (notesDatabase.noteDatabaseDao.deleteNote(note))
    fun updateNote(note: NoteEntity ) = notesDatabase.noteDatabaseDao.updateNote(note)

    /**
     * Fetch countries by hitting the end point and populate the db
     *
     */
    suspend fun fetchNotes() {
        withContext(Dispatchers.IO) {
            Timber.d("refresh notes is being called")

            val noteList: MutableList<Note> = ArrayList()
            val filter = NoteFilter()
            filter.order = NoteSortOrder.UPDATED.value
            val guidList: MutableList<String> = ArrayList()
            val result = EverNoteProvider.noteStoreClient.findNotes(filter, 0, 200)
            for (note in result!!.notes) {
                guidList.add(note.guid)
            }
            for (guid in guidList) {
                val currentNote =
                    EverNoteProvider.noteStoreClient.getNote(guid, true, true, true, true)
                currentNote?.let { noteList.add(it) }
            }
            val notes = EdamNote(noteList)
            val pk = notesDatabase.noteDatabaseDao.insertAll(notes.asNoteEntity())


        }


        }



        }


