package com.zohointerview.notetakingapp

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zohointerview.notetakingapp.domain.DomainNote
import com.zohointerview.notetakingapp.ui.adapters.NotesAdapter
import com.zohointerview.notetakingapp.ui.adapters.PinnedNotesAdapter

/**
 * Class for defining custom binding functions
 */

/**
 *Attribute submit list data to the adapter
 */
@BindingAdapter("listDataPinned")
fun bindRecyclerViewPinned(recyclerView: RecyclerView, data: List<DomainNote>?) {
    val adapter = recyclerView.adapter as PinnedNotesAdapter
    adapter.submitList(data)
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<DomainNote>?) {
    val adapter = recyclerView.adapter as NotesAdapter
    adapter.submitList(data)
}


/**
 * Used for fetching image using glide or GlideToVectorYou
 * For SVG -> use GlideToVectorYou(Extension of glide for vector images)
 * others -> use Glide
 */
@BindingAdapter("imageUri")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    Glide.with(imgView.context)
        .load(Uri.parse(imgUrl)) // Uri of the picture
        .into(imgView);
}

