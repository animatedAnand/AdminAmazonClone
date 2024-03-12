package com.animated_anand.adminamazonclone.models

import java.util.UUID

data class Product(
    var id :String? = null,
    var title :String? = null,
    var quantity :Int? = null,
    var unit :String? = null,
    var price :Int? = null,
    var stock :Int? = null,
    var category :String? = null,
    var type :String? = null,
    var count :Int? = null,
    var adminID :String? = null,
    var imageURIs :ArrayList<String>? = null
)
