package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class Kelas (

    @Json(name="id")
    val id: String? = null,

    @Json(name="judul")
    val judul: String? = null,

    @Json(name="kuota")
    val kuota: String? = null,

    @Json(name="tanggal")
    val tanggal: String? = null,

    @Json(name="poster")
    val poster: String? = null,

    @Json(name="deskripsi")
    val deskripsi: String? = null,

    @Json(name="uploader")
    val uploader: Admin? = null,

    @Json(name="data_speaker")
    val speaker: List<DataSpeaker>? = null,

    @Json(name="participants")
    val participants: List<BookingKelas>? = null,

)