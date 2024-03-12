package com.animated_anand.adminamazonclone

import android.widget.Filter
import com.animated_anand.adminamazonclone.adapters.AdapterProduct
import com.animated_anand.adminamazonclone.models.Product
import java.util.Locale


class ProductFilter(
    val adapter : AdapterProduct,
    val originalProductList : ArrayList<Product>
) : Filter(){
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val result = FilterResults()
        val filteredProducts = ArrayList<Product>()
        val query =constraint.toString().trim().toUpperCase(Locale.getDefault()).split(" ")
        if(!constraint.isNullOrEmpty())
        {
            for(product in originalProductList)
            {
                if(query.any {
                        product.title?.toUpperCase(Locale.getDefault())?.contains(it) == true
                                || product.category?.toUpperCase(Locale.getDefault())?.contains(it) == true
                                || product.price?.toString()?.toUpperCase(Locale.getDefault())?.contains(it) == true
                                || product.type?.toUpperCase(Locale.getDefault())?.contains(it) == true
                    })
                {
                    filteredProducts.add(product)
                }
            }
            result.values = filteredProducts
            result.count = filteredProducts.size
        }
        else
        {
            result.values = originalProductList
            result.count = originalProductList.size
        }
        return result
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        if (results != null) {
            adapter.differ.submitList(results.values as ArrayList<Product>)
        }
    }

}