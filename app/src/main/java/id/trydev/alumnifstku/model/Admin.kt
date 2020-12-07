package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class Admin (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,


)