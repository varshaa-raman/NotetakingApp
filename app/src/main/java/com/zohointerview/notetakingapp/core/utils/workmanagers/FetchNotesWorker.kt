package com.zohointerview.notetakingapp.core.utils.workmanagers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zohointerview.notetakingapp.database.getDatabase
import com.zohointerview.notetakingapp.repository.NotesRepository
import retrofit2.HttpException
import timber.log.Timber

class FetchNotesWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "com.zohointerview.notetakingapp.core.utils.workmanagers.FetchNotesWorker"
        const val FETCH_NOTES_WORKER_TAG = "fetch_notes_worker_tag"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = NotesRepository(database)

        try {
            repository.fetchNotes()
            Timber.d("Fetching notes data from worker ...")
        } catch (e: HttpException) {
            return Result.retry()
        }

        return Result.success()
    }
}