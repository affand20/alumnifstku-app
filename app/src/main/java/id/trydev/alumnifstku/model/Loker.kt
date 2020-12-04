package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class Loker (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "jabatan")
    val jabatan: String? = null,

    @Json(name = "perusahaan")
    val perusahaan: String? = null,

    @Json(name = "deskripsi")
    val deskripsi: String? = null,

    @Json(name = "poster")
    val poster: String? = null,

    @Json(name = "link")
    val link: String? = null,

    @Json(name = "cluster")
    val cluster: String? = null,

    @Json(name = "jurusan")
    val jurusan: String? = null,

    @Json(name = "uploader")
    val uploader: Admin? = null

)