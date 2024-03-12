package com.animated_anand.adminamazonclone.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animated_anand.adminamazonclone.models.Product
import com.animated_anand.adminamazonclone.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AdminViewModel : ViewModel() {

    private val _allImageUploadStatus = MutableStateFlow(false)
    val allImageUploadStatus = _allImageUploadStatus

    private val _productUploadStatus = MutableStateFlow(false)
    val productUploadStatus = _productUploadStatus

    private val _downloadURLs = MutableStateFlow<ArrayList<String>>(arrayListOf())
    val downloadURLs = _downloadURLs
    fun uploadImagesInDB(selectedImages: ArrayList<Uri>) {
        val imageDownloadURLs = ArrayList<String>()

        viewModelScope.launch {
            val uploadTasks = selectedImages.map { uri ->
                val imageRef = FirebaseStorage.getInstance().reference
                    .child(Utils.getCurrentUserID())
                    .child("images")
                    .child(UUID.randomUUID().toString())

                async {
                    val task = imageRef.putFile(uri).await()
                    val url = task.storage.downloadUrl.await()
                    imageDownloadURLs.add(url.toString())
                }
            }

            uploadTasks.awaitAll()

            _allImageUploadStatus.value = true
            _downloadURLs.value = imageDownloadURLs
        }
    }


    fun saveProduct(newProduct: Product) {

        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${newProduct.id}").setValue(newProduct)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins").child("Productcategory/${newProduct.category}/${newProduct.id}").setValue(newProduct)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins").child("ProductsType/${newProduct.type}/${newProduct.id}").setValue(newProduct)
                            .addOnSuccessListener {
                                _productUploadStatus.value = true
                            }
                    }
            }
    }

    fun fetchAllTheProducts(categoryName: String): Flow<List<Product>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val allProducts = ArrayList<Product>()
                for(product in snapshot.children)
                {
                    val currentProduct =product.getValue(Product::class.java)
                    if(categoryName == "All" || categoryName == currentProduct?.category)
                    {
                        allProducts.add(currentProduct!!)
                    }
                }
                trySend(allProducts)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(valueEventListener)
        awaitClose {
            db.removeEventListener(valueEventListener)
        }
    }

    fun saveUpdatedProduct(product : Product)
    {
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.id}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductsType/${product.type}/${product.id}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("Productcategory/${product.category}/${product.id}").setValue(product)
    }
}