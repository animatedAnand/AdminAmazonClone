package com.animated_anand.adminamazonclone

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.animated_anand.adminamazonclone.adapters.AdapterCategory
import com.animated_anand.adminamazonclone.adapters.AdapterProduct
import com.animated_anand.adminamazonclone.databinding.EditProductBinding
import com.animated_anand.adminamazonclone.databinding.FragmentHomeBinding
import com.animated_anand.adminamazonclone.models.Category
import com.animated_anand.adminamazonclone.models.Product
import com.animated_anand.adminamazonclone.utils.Utils
import com.animated_anand.adminamazonclone.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private  val adminViewModel : AdminViewModel by viewModels()
    private  lateinit var adapter : AdapterProduct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setAllProductCategory()
        setAllProducts("All")
        searchProducts()
        return binding.root
    }

    private fun searchProducts() {
        binding.etSearch.addTextChangedListener( object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                adapter.filter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun setAllProducts(categoryName: String) {
        binding.shimmerAllProducts.visibility = View.VISIBLE
        lifecycleScope.launch{
            adminViewModel.fetchAllTheProducts(categoryName).collect{productList->
                if(productList.isEmpty())
                {
                    binding.rvAllProducts.visibility = View.GONE
                    binding.tvNoProduct.visibility = View.VISIBLE
                }
                else
                {
                    binding.rvAllProducts.visibility = View.VISIBLE
                    binding.tvNoProduct.visibility = View.GONE
                }
                adapter =  AdapterProduct(::onEditProductClicked)
                binding.rvAllProducts.adapter = adapter
                adapter.differ.submitList(productList)
                adapter.original_product_list = productList as ArrayList<Product>
                binding.shimmerAllProducts.visibility = View.GONE
            }
        }
    }

    private fun onCategoryClicked(categoryName :String)
    {
        setAllProducts(categoryName)
    }

    private fun onEditProductClicked(product : Product)
    {
        val editProductView = EditProductBinding.inflate(LayoutInflater.from(requireContext()))
        editProductView.apply {
            etProductTitle.setText(product.title)
            etQuantity.setText(product.quantity.toString())
            etUnit.setText(product.unit.toString())
            etPrice.setText(product.price.toString())
            etStock.setText(product.stock.toString())
            etProductCategory.setText(product.category)
            etProductType.setText(product.type)
        }
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProductView.root)
            .create()
        alertDialog.show()

        editProductView.btEdit.setOnClickListener {
            editProductView.etProductTitle.isEnabled = true
            editProductView.etProductCategory.isEnabled = true
            editProductView.etProductType.isEnabled = true
            editProductView.etStock.isEnabled = true
            editProductView.etPrice.isEnabled = true
            editProductView.etUnit.isEnabled = true
            editProductView.etQuantity.isEnabled = true

            setAutoCompleteTextViews(editProductView)
        }

        editProductView.btSave.setOnClickListener {
            lifecycleScope.launch {
                product.title = editProductView.etProductTitle.text.toString()
                product.category = editProductView.etProductCategory.text.toString()
                product.type = editProductView.etProductType.text.toString()
                product.stock = editProductView.etStock.text.toString().toInt()
                product.price = editProductView.etPrice.text.toString().toInt()
                product.quantity = editProductView.etQuantity.text.toString().toInt()
                product.unit = editProductView.etUnit.text.toString()

                adminViewModel.saveUpdatedProduct(product)
            }
            Utils.showToast(requireContext(),"Product details updated!")
            alertDialog.dismiss()
        }
    }
    private fun setAllProductCategory() {
        val categoryList = ArrayList<Category>()
        for(i in 0 until Constants.listAllCategoryName.size)
        {
            categoryList.add(Category(Constants.listAllCategoryName[i],Constants.listAllCategoryIcon[i]))
        }
        binding.rvCategory.adapter = AdapterCategory(categoryList,::onCategoryClicked)
    }

    private fun setAutoCompleteTextViews(editProductView: EditProductBinding) {
        val unitAdapter = ArrayAdapter(requireContext(),R.layout.show_list,Constants.listAllUnitsOfProducts)
        val categoryAdapter = ArrayAdapter(requireContext(),R.layout.show_list,Constants.listAllCategoryName)
        val productTypesAdapter = ArrayAdapter(requireContext(),R.layout.show_list,Constants.listAllProductTypeName)

        editProductView.etProductCategory.setAdapter(categoryAdapter)
        editProductView.etProductType.setAdapter(productTypesAdapter)
        editProductView.etUnit.setAdapter(unitAdapter)
    }

}