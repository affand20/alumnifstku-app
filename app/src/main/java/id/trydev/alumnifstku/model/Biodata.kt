package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class Biodata (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "alumni_id")
    val alumniId: String? = null,

    @Json(name = "nama")
    val nama: String? = null,

    @Json(name = "alamat")
    val alamat: String? = null,

    @Json(name = "umur")
    val umur: Int? = null,

    @Json(name = "ttl")
    val ttl: String? = null,

    @Json(name = "jenis_kelamin")
    val jenisKelamin: String? = null,

    @Json(name = "angkatan")
    val angkatan: String? = null,

    @Json(name = "jurusan")
    val jurusan: String? = null,

    @Json(name = "linkedin")
    val linkedin: String? = null,

    @Json(name = "foto")
    val foto: String? = null


)