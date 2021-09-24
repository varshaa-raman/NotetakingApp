package com.zohointerview.notetakingapp.core.utils

import android.content.ContentResolver
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.evernote.client.android.EvernoteUtil
import com.evernote.edam.type.Data
import com.evernote.edam.type.Note
import com.evernote.edam.type.Resource
import com.evernote.edam.type.ResourceAttributes
import com.zohointerview.notetakingapp.core.NotetakingApplication
import com.zohointerview.notetakingapp.domain.DomainNote
import com.zohointerview.notetakingapp.domain.NoteCategory
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.*
import android.content.DialogInterface

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.preference.PreferenceManager
import com.zohointerview.notetakingapp.ThirdParty.EdamNote
import java.text.ParseException
import java.text.SimpleDateFormat


object NoteAppUtils {

    fun domainNoteToEdamConverter(domainNote: DomainNote, resource: Resource? = null): Note {

        val newNote = Note()
        val contentBody =
            resource?.let { domainNote.content.trim() + EvernoteUtil.createEnMediaTag(it) }
                ?: domainNote.content.trim()
        domainNote.let {
            newNote.apply {
                title = it.title
                content = EvernoteUtil.NOTE_PREFIX + contentBody + EvernoteUtil.NOTE_SUFFIX
                tagNames =
                    if (it.type == NoteCategory.PINNED) Arrays.asList(Constants.TAG_NAME_PINNED) else Arrays.asList(
                        Constants.TAG_NAME_OTHERS
                    )
            }
            return newNote
        }

    }

    fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.getScheme()) {
            val contentResolver: ContentResolver =
                NotetakingApplication.noteApplicationContext.contentResolver
            contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri
                    .toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase()
            )
        }
        Timber.d("Mime type of the file is %s", mimeType)
        return mimeType
    }

    fun generateFileName(extension: String): String =
        "NoteApp_${Constants.currentTimestamp}.$extension"

    fun getExtensionFromUri(uri: Uri): String? = uri.lastPathSegment

    fun createFileFromUri(attachmentUri: Uri): ByteArray? {
        var inputStream =
            NotetakingApplication.noteApplicationContext.contentResolver.openInputStream(
                attachmentUri
            )
        return inputStream?.readBytes()
    }

    fun getFileData(file: ByteArray): Data {
        val data = Data()
        data.setSize(file.size)
        data.setBodyHash(MessageDigest.getInstance("MD5").digest(file))
        data.setBody(file)

        return data
    }

    fun extractNoteContent(rawContent: String): String =
        rawContent.substringAfter(EvernoteUtil.NOTE_PREFIX)
            .substringBefore(EvernoteUtil.NOTE_SUFFIX).substringBefore(Constants.MEDIA_TAG)

    fun createEdamResource(attachmentUri: Uri): Resource {
        var filename = getExtensionFromUri(attachmentUri)?.let { generateFileName(it) }
        var fileData = createFileFromUri(attachmentUri)
        var mimeType = getMimeType(attachmentUri)
        var attachment = Resource()
        var fileAttributes = ResourceAttributes()
        fileAttributes.fileName = filename
        if (fileData != null) {
            attachment.apply {
                data = getFileData(fileData)
                mime = mimeType
                attributes = fileAttributes

            }
        }


        return attachment


    }

    fun getSharedPrefManager(c: Context) : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)


    fun extractResourceFromNote(attachment: Resource): Uri {
        val fileOutputStream: FileOutputStream
        val fileBody = attachment.data.body
        val fileName = attachment.attributes.fileName
        val outputFile = File(Environment.getDataDirectory(), "/NoteApp/$fileName}")
        if (outputFile.exists()) {
            outputFile.delete()
        }
        try {
            fileOutputStream = FileOutputStream(outputFile.getPath())
            fileOutputStream.write(fileBody)
            fileOutputStream.close()

        } catch (e: IOException) {
            Timber.d("Error $e")
        }
        return outputFile.toUri()
    }


    fun getTimeString(timeStamp: Long) : String
    {

        try {

            val ago = DateUtils.getRelativeTimeSpanString(timeStamp, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS)
            return ago.toString()
        } catch (e: ParseException) {
          Timber.d("Parse Error ${e.message}")
            return  ""
        }

    }

}



