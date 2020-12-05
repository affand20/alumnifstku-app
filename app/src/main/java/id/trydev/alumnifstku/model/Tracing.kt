package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class Tracing (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "alumni_id")
    val alumniId: String? = null,

    @Json(name = "perusahaan")
    val perusahaan: String? = null,

    @Json(name = "cluster")
    val cluster: String? = null,

    @Json(name = "tahun_masuk")
    val tahunMasuk: String? = null,

    @Json(name = "jabatan")
    val jabatan: String? = null,


)
