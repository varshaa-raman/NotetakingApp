package com.zohointerview.notetakingapp.core

import android.app.Application
import android.content.Context
import androidx.work.*
import com.evernote.client.android.EvernoteSession
import com.facebook.stetho.Stetho
import com.zohointerview.notetakingapp.core.utils.workmanagers.FetchNotesWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class NotetakingApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)
   companion object
    {
        lateinit var session : EvernoteSession
        lateinit var noteApplicationContext: Context
    }




    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        performSetupOperations()
        noteApplicationContext = this.applicationContext

    }

    private fun performSetupOperations() {
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()

        }
    }


    /**
     * Fetch network data ever 15 minutes
     */
    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicalFetchNoteRequest = PeriodicWorkRequestBuilder<FetchNotesWorker>(15, TimeUnit.MINUTES)
            .addTag(FetchNotesWorker.FETCH_NOTES_WORKER_TAG)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            FetchNotesWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicalFetchNoteRequest
        )




    }



}