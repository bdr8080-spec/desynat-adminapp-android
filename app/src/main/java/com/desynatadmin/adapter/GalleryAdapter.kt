package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemGalleryImgBinding
import com.desynatadmin.ui.home.storyManagement.response.GalleryMedia

class GalleryAdapter(var context:Context, private val items: MutableList<GalleryMedia>,private val listener: (GalleryMedia) -> Unit) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemGalleryImgBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClick{
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGalleryImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            when (item) {
                is GalleryMedia.Image -> {
                    holder.binding.ivPlay.isVisible = false
                    holder.binding.ivGalleryImage.load(item.uri)
                }
                is GalleryMedia.Video -> {
                    holder.binding.ivPlay.isVisible = true
                    val bitmap = getVideoThumbnail(item.uri)
                    if (bitmap != null) {
                        ivGalleryImage.setImageBitmap(bitmap)
                    } else {
                        ivGalleryImage.setImageResource(R.color.black)
                    }
                }
            }
        }

        holder.itemView.setOnClickListener { listener(item) }
    }

    private fun getVideoThumbnail(uri: Uri): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun addItems(newItems: List<GalleryMedia>) {
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }
}
