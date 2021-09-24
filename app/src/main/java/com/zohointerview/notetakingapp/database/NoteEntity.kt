package com.zohointerview.notetakingapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zohointerview.notetakingapp.core.utils.Constants
import com.zohointerview.notetakingapp.domain.DomainNote
import com.zohointerview.notetakingapp.domain.NoteCategory


@Entity(tableName = "note_list_table")
data class NoteEntity(
    @PrimaryKey
    @ColumnInfo(name = "note_guid")
    val guid: String,
    val title: String,
    var content: String,
    var resources: String,
    @ColumnInfo(name = "tag_name")
    var tagName: String,
    var created: Long,
    var updated: Long,
    var active: Boolean
)

/**
 * Convert db entities to domain objects
 */
fun List<NoteEntity>.asNote(): List<DomainNote> {
    return map {
        DomainNote(
            guid = it.guid,
            title = it.title,
            content = it.content,
            type = if (it.tagName?.equals(
                    Constants.TAG_NAME_PINNED,
                    true
                )
            ) NoteCategory.PINNED else NoteCategory.OTHERS,
            resourceUri = it.resources?.let { it },
            createdTime = it.created,
            updatedTime = it.updated,
            active = it.active
        )
    }
}

    fun NoteEntity.asSingleDomainNote() : DomainNote {
        return DomainNote(this.guid,this.title,this.content,if (this.tagName?.equals(
                Constants.TAG_NAME_PINNED,
                true
            )
        ) NoteCategory.PINNED else NoteCategory.OTHERS,this.resources?.let { it },this.created,this.updated,this.active)
    }

