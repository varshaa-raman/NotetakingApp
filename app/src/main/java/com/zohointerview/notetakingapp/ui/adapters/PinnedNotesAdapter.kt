package com.zohointerview.notetakingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zohointerview.notetakingapp.databinding.ItemNoteBinding
import com.zohointerview.notetakingapp.domain.DomainNote

class PinnedNotesAdapter(private val onClickListener: OnClickListener,private val onLongClickListener: PinnedNotesAdapter.OnLongClickListener) : ListAdapter<DomainNote,
        PinnedNotesAdapter.PinnedNoteViewHolder>(PinnedNoteDiffUtilCallBack) {

    class PinnedNoteViewHolder(private var binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: DomainNote) {
            binding.note = note
            binding.executePendingBindings()
        }
    }

    companion object PinnedNoteDiffUtilCallBack : DiffUtil.ItemCallback<DomainNote>() {
        override fun areItemsTheSame(oldItem: DomainNote, newItem: DomainNote): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DomainNote, newItem: DomainNote): Boolean {
            return oldItem.guid.equals(newItem.guid, true)
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PinnedNoteViewHolder {
        return PinnedNoteViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context)))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: PinnedNoteViewHolder, position: Int) {
        val noteSelected = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(noteSelected)
        }
        holder.itemView.setOnLongClickListener{ onLongClickListener.onClick(noteSelected)}
        holder.bind(noteSelected)
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.
     */
    class OnClickListener(val clickListener: (selectedNote: DomainNote) -> Unit) {
        fun onClick(selectedNote: DomainNote) = clickListener(selectedNote)
    }

    class OnLongClickListener(val clickListener: (selectedNote: DomainNote) -> Boolean) {
        fun onClick(selectedNote: DomainNote) = clickListener(selectedNote)
    }

    override fun submitList(list: List<DomainNote>?) {
        super.submitList(list?.let { ArrayList(it) })
    }


}