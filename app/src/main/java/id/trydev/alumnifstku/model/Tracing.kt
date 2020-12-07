package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class Tracing (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("alumni_id")
    val alumniId: String? = null,

    @SerializedName("perusahaan")
    val perusahaan: String? = null,

    @SerializedName("cluster")
    val cluster: String? = null,

    @SerializedName("tahun_masuk")
    val tahunMasuk: String? = null,

    @SerializedName("jabatan")
    val jabatan: String? = null,


)
