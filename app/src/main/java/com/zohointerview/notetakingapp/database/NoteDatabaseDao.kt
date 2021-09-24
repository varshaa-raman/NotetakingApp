package com.zohointerview.notetakingapp.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface NoteDatabaseDao {
    @Query("select * from note_list_table")
    fun getNotes(): LiveData<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<NoteEntity>): List<Long>

    @Query("select distinct * from note_list_table where tag_name LIKE :tagName AND active ORDER BY updated ASC")
    fun getPinnedNotes(tagName: String): LiveData<List<NoteEntity>>

    @Query("select distinct * from note_list_table where note_guid LIKE :guid AND active")
    fun getSingleNote(guid: String): NoteEntity


    @Query("select distinct * from note_list_table where tag_name NOT LIKE :tagName AND active ORDER BY updated ASC")
    fun getOtherNotesByUpdtAsc(tagName: String): LiveData<List<NoteEntity>>

    @Query("select distinct * from note_list_table where tag_name NOT LIKE :tagName AND active ORDER BY updated Desc")
    fun getOtherNotesByUpdtDesc(tagName: String): LiveData<List<NoteEntity>>

    @Query("Delete from note_list_table")
    fun deleteAll()


    @Query("Delete from note_list_table where note_guid LIKE :guid AND active")
    fun deleteNote(guid: String)

    @Query("Update note_list_table set tag_name = :tagName where note_guid LIKE :guid AND active")
    fun updatePin(guid: String, tagName: String)

    @Query("Select distinct COUNT(note_guid) from note_list_table where tag_name LIKE :tagName AND active ")
    fun getPinnedNotesCount(tagName: String): LiveData<Integer>

    @Update
    fun updateNote(note: NoteEntity)



}