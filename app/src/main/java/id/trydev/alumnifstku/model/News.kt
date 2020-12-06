package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName


data class News (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("judul")
    val judul: String? = null,

    @SerializedName("link")
    val link: String? = null,

    @SerializedName("gambar")
    val gambar: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("uploader")
    val uploader: Alumni? = null,

)