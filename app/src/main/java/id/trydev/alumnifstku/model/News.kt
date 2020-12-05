package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class News (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "judul")
    val judul: String? = null,

    @Json(name = "link")
    val link: String? = null,

    @Json(name = "gambar")
    val gambar: String? = null,

    @Json(name = "created_at")
    val createdAt: String? = null,

    @Json(name = "uploader")
    val uploader: Alumni? = null,

)