package com.animated_anand.adminamazonclone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.animated_anand.adminamazonclone.activity.AdminMainActivity
import com.animated_anand.adminamazonclone.adapters.AdapterSelectedImages
import com.animated_anand.adminamazonclone.databinding.FragmentAddProductBinding
import com.animated_anand.adminamazonclone.models.Product
import com.animated_anand.adminamazonclone.utils.Utils
import com.animated_anand.adminamazonclone.viewmodels.AdminViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID

class AddProductFragment : Fragment() {
     private lateinit var binding : FragmentAddProductBinding
     private  var selectedImages : ArrayList<Uri> = arrayListOf()
     private  val adminViewModel : AdminViewModel by viewModels()
     val selectImage = registerForActivityResult(ActivityResultContracts.GetMultipleContents())
     { listOfUri->
         val fiveImages = listOfUri.take(5)
         selectedImages.clear()
         selectedImages.addAll(fiveImages)

         binding.rvSelectedImages.adapter = AdapterSelectedImages(selectedImages)
     }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)
        setAutoCompleteTextViews()
        attachImage()
        onUploadButtonClick()
        return binding.root
    }

    private fun onUploadButtonClick() {
        binding.btAddProduct.setOnClickListener {
            val productTitle = binding.etProductTitle.text.toString()
            val productQuantity = binding.etQuantity.text.toString()
            val productUnit = binding.etUnit.text.toString()
            val productPrice = binding.etPrice.text.toString()
            val productStock = binding.etStock.text.toString()
            val productCategory = binding.etProductCategory.text.toString()
            val productType = binding.etProductType.text.toString()

            if(productTitle.isEmpty() || productUnit.isEmpty() || productQuantity.isEmpty() || productPrice.isEmpty()
                || productStock.isEmpty() || productCategory.isEmpty() || productType.isEmpty())
            {
                Utils.showToast(requireContext(),"Empty field is not allowed!")
            }
            else if(selectedImages.isEmpty())
            {
                Utils.showToast(requireContext(),"Please attach some images!")
            }
            else
            {
                var newProduct = Product(
                    id = Utils.generateCleanUUID(),
                    title = productTitle,
                    quantity = productQuantity.toInt(),
                    unit = productUnit,
                    price = productPrice.toInt(),
                    stock = productStock.toInt(),
                    category = productCategory,
                    type = productType,
                    count = 0,
                    adminID = Utils.getCurrentUserID()
                )
                uploadImage(newProduct)
            }
        }
    }

    private fun uploadImage(newProduct: Product) {
        Utils.showProgressDialog(requireContext(),"Uploading Images..")
        adminViewModel.uploadImagesInDB(selectedImages)

        lifecycleScope.launch {
            adminViewModel.allImageUploadStatus.collect{allImageUploadStatus->
                if(allImageUploadStatus)
                {
                    Utils.hideProgressDialog()
                    insertImageURIs(newProduct)
                }
            }
        }
    }

    private fun insertImageURIs(newProduct: Product) {
        Utils.showProgressDialog(requireContext(),"Publishing product..")
        lifecycleScope.launch {
            adminViewModel.downloadURLs.collect{urlList->
                newProduct.imageURIs = urlList
                uploadProduct(newProduct)
            }
        }
    }

    private fun uploadProduct(newProduct: Product) {
        adminViewModel.saveProduct(newProduct)
        lifecycleScope.launch {
            adminViewModel.productUploadStatus.collect{
                if(it)
                {
                    Utils.hideProgressDialog()
                    startActivity(Intent(requireActivity(),AdminMainActivity::class.java))
                    Utils.showToast(requireContext(),"Your product is live!")
                }
            }
        }
    }

    private fun attachImage() {
        binding.ivUploadImage.setOnClickListener {
            selectImage.launch("image/*")
        }
    }

    private fun setAutoCompleteTextViews() {
        val unitAdapter = ArrayAdapter(requireContext(),R.layout.show_list,Constants.listAllUnitsOfProducts)
        val categoryAdapter = ArrayAdapter(requireContext(),R.layout.show_list,Constants.listAllCategoryName)
        val productTypesAdapter = ArrayAdapter(requireContext(),R.layout.show_list,Constants.listAllProductTypeName)

        binding.etProductCategory.setAdapter(categoryAdapter)
        binding.etProductType.setAdapter(productTypesAdapter)
        binding.etUnit.setAdapter(unitAdapter)
    }

}