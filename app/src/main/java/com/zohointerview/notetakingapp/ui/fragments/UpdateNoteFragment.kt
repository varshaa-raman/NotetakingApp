package com.zohointerview.notetakingapp.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.zohointerview.notetakingapp.R
import com.zohointerview.notetakingapp.core.utils.NoteAppUtils
import com.zohointerview.notetakingapp.databinding.FragmentUpdateNoteBinding
import com.zohointerview.notetakingapp.domain.DomainNote
import com.zohointerview.notetakingapp.domain.NoteCategory
import com.zohointerview.notetakingapp.ui.viewModels.UpdateNotesViewModel
import com.zohointerview.notetakingapp.ui.factories.UpdateNotesViewModelFactory
import timber.log.Timber
import java.lang.Exception

/****************************************************
 * View model class for fragment_update_note
 *
 ****************************************************/
class UpdateNoteFragment : Fragment() {
    private lateinit var viewModel: UpdateNotesViewModel

    // his is being received via safeArgs
    lateinit var selectedNote: DomainNote
    lateinit var binding: FragmentUpdateNoteBinding
    var isSharingEnabled : Boolean = true
    var attachmentUri : Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(activity).application
       binding = FragmentUpdateNoteBinding.inflate(inflater)
        binding.lifecycleOwner = this
        selectedNote =
            UpdateNoteFragmentArgs.fromBundle(
                requireArguments()
            ).selectedNote
        viewModel = ViewModelProvider(this, UpdateNotesViewModelFactory(selectedNote, application))
            .get(UpdateNotesViewModel::class.java)
        binding.notesViewModel = viewModel
        binding.ivUpdtNtSave.setOnClickListener { view: View ->
            shareNote()
        }
        binding.ivUpdtSettings.setOnClickListener { view: View ->
            this.findNavController()
                .navigate(com.zohointerview.notetakingapp.ui.fragments.UpdateNoteFragmentDirections.actionUpdateSettings())
        }
        binding.ivUpdtNtMnu.setOnClickListener { view: View -> popMenu(view) }
        binding.ivUpdtNtSave.setOnClickListener {  view: View ->
            val title = binding.tietUpdtNteTitle?.text.toString()
            val content = binding.tietUpdtNteContent?.text.toString()
            val resource = if(binding.ivUpdtNteAttchment.visibility == View.VISIBLE)
            {
                attachmentUri?.let{NoteAppUtils.createEdamResource(it)} ?: null
            } else null
            val newNote = NoteAppUtils.domainNoteToEdamConverter(
                DomainNote(
                    title = title,
                    content = content,
                    type = NoteCategory.OTHERS,
                    resourceUri = attachmentUri?.path
                ),resource
            )
            viewModel.updateNote(newNote)
            Timber.d("Clicked!!!!!") }

        viewModel.shouldShowProgress.observe(this.viewLifecycleOwner, {
            onProgressChanged(binding, it)


        })
        viewModel.shouldShowError.observe(this.viewLifecycleOwner, {
            onError(it)

        })
        binding.ivUpdtNteAttchment.setOnClickListener{ view: View ->
            val appPerms = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            activityResultLauncher.launch(appPerms)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NoteAppUtils.getSharedPrefManager(view.context).getBoolean(getString(R.string.pref_key_sharing),true)
    }
    private var activityResultLauncher: ActivityResultLauncher<Array<String>>
    init{
        this.activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { result ->
            var allAreGranted = true
            for(result in result.values) {
                allAreGranted = allAreGranted && result
            }

            if(allAreGranted) {
                pickImage(this.requireContext())
            }
        }
    }

    val pickImageRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data
                uri?.let{
                    binding.ivUpdtNteAttchment.visibility = View.VISIBLE
                    Glide.with(this.requireContext())
                        .load(uri) // Uri of the picture
                        .into(binding.ivUpdtNteAttchment);
                }
                attachmentUri = uri
            }

        }

    private fun pickImage(context: Context) {
        val pickFileIntent = Intent(Intent.ACTION_PICK)
        pickFileIntent.type = "image/*"
        pickImageRequest.launch(pickFileIntent)
    }


    fun popMenu(v: View) {
        val popup = PopupMenu(this.requireContext(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_nt_crt_updt, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_img -> {
                    val appPerms = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    )
                    activityResultLauncher.launch(appPerms)

                }
                R.id.action_share ->
                {
                    if(isSharingEnabled) {shareNote()} else {Toast.makeText(v.context,"Sharing has been disabled in settings",Toast.LENGTH_LONG).show()}
                }
                R.id.action_pin_nt -> {
                    viewModel.onNotePinned()
                }
                R.id.action_dlt_nt -> {
                    showDeleteAlert(v)

                }
            }
     true
        }
        popup.show()
    }


    fun shareNote() {
        try{
            val noteSharingIntent = Intent()

            val text = "Title: ${selectedNote.title} \n Content:${selectedNote.content}"
            noteSharingIntent.apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                noteSharingIntent.setType("text/plain")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

//
                if (!selectedNote.resourceUri.isNullOrEmpty()) {
                    val pictureUri: Uri = Uri.parse(selectedNote.resourceUri)
                    putExtra(Intent.EXTRA_STREAM, pictureUri)
                    type = "*/*"
                }
            }

            startActivity(Intent.createChooser(noteSharingIntent, "Share notes..."))

        }
        catch (ex: Exception)
        {
            Timber.d("sharing failed with exception $ex")
        }


    }


    fun showDeleteAlert(view: View) {
        //Instantiate builder variable
        val builder = AlertDialog.Builder(view.context)

        // set title
        builder.setTitle("Delete Note Confirmation")

        //set content area
        builder.setMessage("Delete note titled ${selectedNote.title}?")

        //set negative button
        builder.setPositiveButton(
            "Delete Now"
        ) { dialog, id ->
            // User clicked Update Now button
            viewModel.onNoteDeleted()
        }

        //set positive button
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, id ->
            Toast.makeText(
                this.requireContext(),
                "Note not deleted as requested",
                Toast.LENGTH_SHORT
            ).show()
        }

        builder.show()
    }


    /**
     * Displaying progress bar
     * @param binding data binding reference
     * @param shouldShowProgress boolean to decide whether progress should be shown or not
     */
    private fun onProgressChanged(
        binding: FragmentUpdateNoteBinding,
        shouldShowProgress: Boolean
    ) {

        binding.flUpdtNt.visibility = if (shouldShowProgress) View.VISIBLE else View.GONE
        binding.clUpdtNote.visibility = if (shouldShowProgress) View.GONE else View.VISIBLE
    }

    /**
     * Displaying error banner
     * @param binding data binding reference
     * @param shouldShowError boolean to decide whether error banner should be shown or not
     */
    private fun onError(shouldShowError: Boolean) {
        if (shouldShowError) {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show()
        }

    }
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.ivUpdtNtSave.setEnabled(!binding.tietUpdtNteTitle.text.toString().isNullOrEmpty() && !binding.tietUpdtNteContent.text.toString().isNullOrEmpty())
        }

        override fun afterTextChanged(s: Editable) {}
    }


}