package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class Biodata (

        @SerializedName("id")
    val id: String? = null,

        @SerializedName("alumni_id")
    val alumniId: String? = null,

        @SerializedName("nama")
    var nama: String? = null,

        @SerializedName("kota_domisili")
    var domisili: String? = null,

        @SerializedName("alamat")
    val alamat: String? = null,

        @SerializedName("umur")
    val umur: Int? = null,

        @SerializedName("ttl")
    val ttl: String? = null,

        @SerializedName("jenis_kelamin")
    val jenisKelamin: String? = null,

        @SerializedName("angkatan")
    val angkatan: String? = null,

        @SerializedName("jurusan")
    val jurusan: String? = null,

        @SerializedName("linkedin")
    val linkedin: String? = null,

        @SerializedName("foto")
    val foto: String? = null


)