package com.animated_anand.adminamazonclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.animated_anand.adminamazonclone.ProductFilter
import com.animated_anand.adminamazonclone.databinding.ItemViewProductBinding
import com.animated_anand.adminamazonclone.models.Product
import com.denzcoskun.imageslider.models.SlideModel

class AdapterProduct(val onEditProductClicked: (Product) -> Unit) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() , Filterable{

    class ProductViewHolder (val binding : ItemViewProductBinding) : ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemViewProductBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        val productImages = ArrayList<SlideModel>()
        for(i in 0 until product.imageURIs!!.size)
        {
            productImages.add(SlideModel(product.imageURIs!![i].toString()))
        }
        holder.binding.tvProductName.text = product.title.toString()
        holder.binding.isProductImages.setImageList(productImages)
        holder.binding.tvProductQuantity.text = product.quantity.toString() +" "+ product.unit
        holder.binding.tvProductPrice.text = "$" + product.price.toString()

        holder.itemView.setOnClickListener {
            onEditProductClicked(product)
        }
    }

    var original_product_list = ArrayList<Product>()
    private val filter : ProductFilter? = null
    override fun getFilter(): Filter {
        if(filter == null)
        {
            return ProductFilter(this,original_product_list)
        }
        return filter
    }
}