package com.zohointerview.notetakingapp.domain

import android.os.Parcelable
import com.evernote.edam.type.Note
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainNote constructor(
    val guid: String = "",
    val title: String = "",
    val content: String ="",
    val type:NoteCategory = NoteCategory.OTHERS,
    val resourceUri: String ?,
    val createdTime: Long = 0L,
    val updatedTime: Long = 0L,
    val active: Boolean = true
): Parcelable

enum class NoteCategory() {
    PINNED,
    OTHERS;
}



