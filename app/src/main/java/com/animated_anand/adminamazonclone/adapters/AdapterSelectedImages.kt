package com.animated_anand.adminamazonclone.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.animated_anand.adminamazonclone.databinding.ItemViewImageSelectionBinding
import com.animated_anand.adminamazonclone.databinding.ShowListBinding

class AdapterSelectedImages(val selectedImages : ArrayList<Uri>) : RecyclerView.Adapter<AdapterSelectedImages.ImageViewHolder>()
{
    class ImageViewHolder (val binding : ItemViewImageSelectionBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemViewImageSelectionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return selectedImages.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = selectedImages[position]
        holder.binding.ivImage.setImageURI(image)

        holder.binding.ivRemove.setOnClickListener {
            if(position<selectedImages.size)
            {
                selectedImages.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }
}