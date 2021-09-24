package com.zohointerview.notetakingapp.ui.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zohointerview.notetakingapp.domain.DomainNote
import com.zohointerview.notetakingapp.ui.viewModels.UpdateNotesViewModel

/****************************************************
 * View model factory class for fragment_country_detail
 *
 ****************************************************/
class UpdateNotesViewModelFactory(
    private val note: DomainNote,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateNotesViewModel::class.java)) {
            return UpdateNotesViewModel(note, application) as T
        }
        throw IllegalArgumentException("ViewModel class undefined")
    }
}