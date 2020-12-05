package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class BookingKelas (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "kelas_alumni_id")
    val kelasId: String? = null,

    @Json(name = "alumni_id")
    val alumniId: String? = null,

    @Json(name = "email")
    val email: String? = null,

    @Json(name = "nama_lengkap")
    val name: String? = null,

//    @Json(name = "whatsapp")
//    val whatsapp: String? = null,
//
)