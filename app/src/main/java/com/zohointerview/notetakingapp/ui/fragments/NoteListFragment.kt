package com.zohointerview.notetakingapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zohointerview.notetakingapp.R
import com.zohointerview.notetakingapp.databinding.FragmentNoteListBinding
import com.zohointerview.notetakingapp.databinding.FragmentUpdateNoteBinding
import com.zohointerview.notetakingapp.ui.adapters.NotesAdapter
import com.zohointerview.notetakingapp.ui.adapters.PinnedNotesAdapter
import com.zohointerview.notetakingapp.ui.factories.NotesViewModelFactory
import com.zohointerview.notetakingapp.ui.viewModels.NotesViewModel

class NoteListFragment : Fragment() {

    private lateinit var viewModel: NotesViewModel
    private lateinit var pinnedNotesAdapter: PinnedNotesAdapter
    private lateinit var otherNotesAdapter: NotesAdapter
    private var pinnable = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        viewModel =
            ViewModelProvider(this, NotesViewModelFactory(application))
                .get(NotesViewModel::class.java)

        val binding = FragmentNoteListBinding.inflate(inflater)


        binding.lifecycleOwner = this
        binding.notesViewModel = viewModel
        pinnedNotesAdapter = PinnedNotesAdapter(PinnedNotesAdapter.OnClickListener {
            viewModel.onNoteClicked(it)

        }, PinnedNotesAdapter.OnLongClickListener { viewModel.onNoteLongClicked(it) })
        otherNotesAdapter = NotesAdapter(NotesAdapter.OnClickListener {
            viewModel.onNoteClicked(it)
        }, NotesAdapter.OnLongClickListener { viewModel.onNoteLongClicked(it) })

        binding.ivNtLstSettings.setOnClickListener { view: View ->
            this.findNavController().navigate(
                NoteListFragmentDirections.actionListSettings()
            )

            viewModel.onNavigationComplete()
        }
        binding.rvNotesListOthers.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.rvNotesListPinned.setHasFixedSize(true)
        binding.rvNotesListOthers.setHasFixedSize(true)
        binding.rvNotesListPinned.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.rvNotesListPinned.adapter = pinnedNotesAdapter
        binding.rvNotesListOthers.adapter = otherNotesAdapter
        viewModel.selectedNote.observe(this.viewLifecycleOwner, {
            it?.let {
                this.findNavController()
                    .navigate(
                        NoteListFragmentDirections.actionShowSelectedNote(
                            it
                        )
                    )
                viewModel.onNavigationComplete()
            }
        })
        viewModel.pinnedNotesCount.observe(this.viewLifecycleOwner, {
            pinnable = (it < 4)
        })

        viewModel.switchToolbar.observe(this.viewLifecycleOwner,
            {
                setupToolbar(binding, it)


            }
        )
        viewModel.shouldShowError.observe(this.viewLifecycleOwner, {
            onError(it)

        })
        binding.ivNtLstSettings.setOnClickListener{view: View ->  this.findNavController()
            .navigate(NoteListFragmentDirections.actionListSettings())}
        binding.ivNtLstClose.setOnClickListener { view: View -> viewModel.onNoteClosed() }
        binding.ivNtLstDltNt.setOnClickListener { view: View -> viewModel.onNoteDeleted() }
        binding.ivNtLstPinNt.setOnClickListener { view: View ->
            if(pinnable) {viewModel.onNotePinned() } else { Toast.makeText(view.context,"Maximum pins reached",Toast.LENGTH_LONG).show()}}
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val createNoteImageView = getView()?.findViewById<ImageView>(R.id.iv_nt_lst_crt_nt)
        createNoteImageView?.setOnClickListener { view: View ->
            this.findNavController()
                .navigate(NoteListFragmentDirections.actionCreateNote())

        }
    }

    fun setupToolbar(binding: FragmentNoteListBinding, isLongPressed: Boolean) {
        binding.ivNtLstSettings.visibility = if (isLongPressed) View.INVISIBLE else View.VISIBLE
        binding.ivNtLstClose.visibility = if (isLongPressed) View.VISIBLE else View.INVISIBLE
        binding.tvMainTitle.visibility = if (isLongPressed) View.INVISIBLE else View.VISIBLE
        binding.ivNtLstCrtNt.visibility = if (isLongPressed) View.INVISIBLE else View.VISIBLE
        binding.ivNtLstPinNt.visibility = if (isLongPressed) View.VISIBLE else View.INVISIBLE
        binding.ivNtLstDltNt.visibility = if (isLongPressed) View.VISIBLE else View.INVISIBLE

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
}

