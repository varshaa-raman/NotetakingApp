@file:Suppress("unused")

package com.zohointerview.notetakingapp.core.utils

import com.evernote.client.android.EvernoteSession

class Constants {
    companion object {
        val EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX
        const val TAG_NAME_PINNED = "pinned"
        const val TAG_NAME_OTHERS = "others"
        const val MEDIA_TAG = "<en-media"

        val currentTimestamp = System.currentTimeMillis()
    }
}