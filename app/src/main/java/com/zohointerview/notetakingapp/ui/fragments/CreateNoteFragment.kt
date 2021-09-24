package com.zohointerview.notetakingapp.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.zohointerview.notetakingapp.R
import com.zohointerview.notetakingapp.core.utils.NoteAppUtils
import com.zohointerview.notetakingapp.databinding.FragmentCreateNoteBinding
import com.zohointerview.notetakingapp.domain.DomainNote
import com.zohointerview.notetakingapp.domain.NoteCategory
import com.zohointerview.notetakingapp.ui.factories.NotesViewModelFactory
import com.zohointerview.notetakingapp.ui.viewModels.NotesViewModel
import timber.log.Timber


private lateinit var viewModel: NotesViewModel
private lateinit var binding: FragmentCreateNoteBinding

class CreateNoteFragment : Fragment() {


    var attachmentUri : Uri? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = getView()?.findViewById<Toolbar>(R.id.toolbar_crt_nt)

        toolbar?.menu?.clear()
        //toolbar?.inflateMenu(R.menu.menu_nt_crt_updt)
        super.onViewCreated(view, savedInstanceState)
        val saveNoteImageView = getView()?.findViewById<ImageView>(R.id.iv_crt_nt_save)
        val titleEditText = getView()?.findViewById<TextInputEditText>(R.id.tiet_crt_nte_title)
        val contentEditText = getView()?.findViewById<TextInputEditText>(R.id.tiet_crt_nte_content)
        saveNoteImageView?.setOnClickListener { view: View ->
            val title = titleEditText?.text.toString()
            val content = contentEditText?.text.toString()
            val resource = if(binding.ivCrtNteAttchment.visibility == View.VISIBLE)
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
            viewModel.onNoteCreated(newNote)
            Timber.d("Clicked!!!!!")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(activity).application
        binding = FragmentCreateNoteBinding.inflate(inflater)
        val viewModelFactory = NotesViewModelFactory(application)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(NotesViewModel::class.java)
        binding.notesViewModel = viewModel

        binding.lifecycleOwner = this
//        viewModel.shouldShowProgress.observe(this.viewLifecycleOwner, {
//            onProgressChanged(binding, it)
//
//        })
        viewModel.selectedNote.observe(this.viewLifecycleOwner, {
            it?.let {
                this.findNavController()
                    .navigate(
                       NoteListFragmentDirections.actionShowCreatedNote(
                            it
                        )
                    )
                viewModel.onNavigationComplete()
            }
        })
        binding.ivCrtNtAddRsrc.setOnClickListener{ view: View ->
            val appPerms = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            activityResultLauncher.launch(appPerms)


        }
        binding.tietCrtNteTitle.addTextChangedListener(textWatcher)
        binding.tietCrtNteContent.addTextChangedListener(textWatcher)
        binding.ivCrtNtSettings.setOnClickListener { view: View -> this.findNavController().navigate(
            CreateNoteFragmentDirections.actionCreateSettings())}

        return binding.root



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
            if (it.resultCode == RESULT_OK) {
                val uri = it.data?.data
                uri?.let{
                    binding.ivCrtNteAttchment.visibility = View.VISIBLE
                    Glide.with(this.requireContext())
                        .load(uri) // Uri of the picture
                        .into(binding.ivCrtNteAttchment);
                }
                attachmentUri = uri
            }

        }

    private fun pickImage(context: Context) {
        val pickFileIntent = Intent(Intent.ACTION_PICK)
        pickFileIntent.type = "image/*"
        pickImageRequest.launch(pickFileIntent)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.menu_nt_crt_updt, menu)
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.ivCrtNtSave.setEnabled(!binding.tietCrtNteTitle.text.toString().isNullOrEmpty() && !binding.tietCrtNteContent.text.toString().isNullOrEmpty())
        }

        override fun afterTextChanged(s: Editable) {}
    }


}