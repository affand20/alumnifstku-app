package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class Kelas (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("judul")
    val judul: String? = null,

    @SerializedName("kuota")
    val kuota: String? = null,

    @SerializedName("tanggal")
    val tanggal: String? = null,

    @SerializedName("poster")
    val poster: String? = null,

    @SerializedName("deskripsi")
    val deskripsi: String? = null,

    @SerializedName("kategori")
    val kategori: String? = null,

    @SerializedName("uploader")
    val uploader: Admin? = null,

    @SerializedName("data_speaker")
    val speaker: List<DataSpeaker>? = null,

    @SerializedName("participants")
    val participants: List<BookingKelas>? = null,

)