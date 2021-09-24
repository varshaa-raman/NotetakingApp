package com.zohointerview.notetakingapp.ThirdParty

import com.evernote.client.android.EvernoteUtil
import com.evernote.edam.type.Note
import com.zohointerview.notetakingapp.core.utils.Constants.Companion.TAG_NAME_PINNED
import com.zohointerview.notetakingapp.core.utils.NoteAppUtils
import com.zohointerview.notetakingapp.database.NoteEntity
import com.zohointerview.notetakingapp.domain.DomainNote
import com.zohointerview.notetakingapp.domain.NoteCategory


data class EdamNote(val noteList : List<Note>)



/**
* Convert response to domain object
*/
fun EdamNote.asNote(): List<DomainNote> {
    return noteList.map {
        DomainNote(
            guid = it.guid ?: "",
            title = it.title ?: "",
            content = it.content ?.let { NoteAppUtils.extractNoteContent(it)  } ?: "",
            type = if (it.tagGuids.get(0).equals(TAG_NAME_PINNED,true) ) NoteCategory.PINNED else NoteCategory.OTHERS,
            resourceUri = it.resources?.let{it.toString()} ?: "",
            createdTime = it.created,
        updatedTime = it.updated,
            active = it.isActive
        )
    }
}

/**
 * Convert response to database object
 */
fun EdamNote.asNoteEntity(): List<NoteEntity> {
    return noteList.map {
        NoteEntity(
            guid = it.guid,
           title = it.title ?: "",
            content = it.content ?.let{NoteAppUtils.extractNoteContent(it)} ?: "",
            resources = it.resources?.let { NoteAppUtils.extractResourceFromNote(it[0]).toString() } ?: "",
            tagName = it.tagNames?.let {it[0]} ?: com.zohointerview.notetakingapp.core.utils.Constants.TAG_NAME_OTHERS,
            created = it.created,
            updated = it.updated,
            active = it.isActive
        )
    }
}
