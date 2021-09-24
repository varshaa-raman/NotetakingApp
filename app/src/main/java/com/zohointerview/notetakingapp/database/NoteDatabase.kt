package com.zohointerview.notetakingapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract val noteDatabaseDao: NoteDatabaseDao
}

private lateinit var INSTANCE: NoteDatabase

fun getDatabase(context: Context): NoteDatabase {
    synchronized(NoteDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java,
                "notes"
            ).build()
        }
    }
    return INSTANCE

}