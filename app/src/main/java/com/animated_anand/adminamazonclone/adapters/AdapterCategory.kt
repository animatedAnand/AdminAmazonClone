package com.animated_anand.adminamazonclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.animated_anand.adminamazonclone.databinding.ItemViewProductCategoryBinding
import com.animated_anand.adminamazonclone.models.Category

class AdapterCategory(val categories: ArrayList<Category>, val onCategoryClicked: (String) -> Unit) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>()
{
    class CategoryViewHolder(val binding: ItemViewProductCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ItemViewProductCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.apply {
            ivCategory.setImageResource(category.image)
            tvCategory.text = category.name
        }
        holder.itemView.setOnClickListener {
            onCategoryClicked(category.name)
        }
    }
}