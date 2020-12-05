package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class Admin (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "name")
    val name: String? = null,


)