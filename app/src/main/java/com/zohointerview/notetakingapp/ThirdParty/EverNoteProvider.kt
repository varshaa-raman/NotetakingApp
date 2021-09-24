package com.zohointerview.notetakingapp.ThirdParty

import com.evernote.client.android.EvernoteSession
import com.evernote.client.android.asyncclient.EvernoteCallback
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient
import com.evernote.edam.notestore.NoteFilter
import com.evernote.edam.notestore.NoteList
import com.evernote.edam.notestore.NotesMetadataResultSpec
import com.evernote.edam.type.Note
import com.evernote.edam.type.NoteSortOrder
import com.zohointerview.notetakingapp.core.NotetakingApplication
import com.zohointerview.notetakingapp.core.utils.Constants
import timber.log.Timber
import java.lang.Exception

object EverNoteProvider {
    val session =  EvernoteSession.Builder(NotetakingApplication.noteApplicationContext).setEvernoteService(
        Constants.EVERNOTE_SERVICE)
        .buildForSingleUser("S=s1:U=96788:E=183205a632b:C=17bc8a936d0:P=1cd:A=en-devtoken:V=2:H=884043a4345c956ff666dcae3694951f","https://sandbox.evernote.com/shard/s1/notestore").asSingleton()
    val noteStoreClient = session.evernoteClientFactory.noteStoreClient

    fun obtainNoteStoreClient() : EvernoteNoteStoreClient = noteStoreClient
    fun fetchNotes() : EdamNote {
        val noteList : MutableList<Note> = ArrayList()
        val filter = NoteFilter()
        filter.order = NoteSortOrder.UPDATED.value

        noteStoreClient.findNotesAsync(filter,0,100,object: EvernoteCallback<NoteList> {
            val guidList : MutableList<String> = ArrayList()

            override fun onSuccess(result: NoteList?) {
                for(note in  result!!.notes )
                {
                    guidList.add(note.guid)

                }
                guidList.forEach { noteStoreClient.getNoteAsync(it,true,true,true,true,object :
                    EvernoteCallback<Note>
                {
                    override fun onSuccess(result: Note?) {
                        result?.let { noteList.add(it) }
                        Timber.d(result!!.title + "-" + result!!.guid + "-" + result!!.content)
                    }

                    override fun onException(exception: Exception?) {
                        Timber.d("ERror" + exception.toString())
                    }
                }) }


            }

            override fun onException(exception: Exception?) {
                TODO("Not yet implemented")
            }
        })
        return  EdamNote(noteList)
    }
}