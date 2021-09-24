package com.zohointerview.notetakingapp.ui.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zohointerview.notetakingapp.ui.viewModels.NotesViewModel

class NotesViewModelFactory(val app: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(app) as T
        }
        throw IllegalArgumentException("ViewModel class undefined")
    }

}