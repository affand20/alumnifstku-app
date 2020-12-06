package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Loker (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("jabatan")
    val jabatan: String? = null,

    @SerializedName("perusahaan")
    val perusahaan: String? = null,

    @SerializedName("deskripsi")
    val deskripsi: String? = null,

    @SerializedName("poster")
    val poster: String? = null,

    @SerializedName("link")
    val link: String? = null,

    @SerializedName("cluster")
    val cluster: String? = null,

    @SerializedName("jurusan")
    val jurusan: String? = null,

    @SerializedName("uploader")
    val uploader: Admin? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null

)