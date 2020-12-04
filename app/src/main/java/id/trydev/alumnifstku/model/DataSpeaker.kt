package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class DataSpeaker (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "kelas_alumni_id")
    val kelasId: String? = null,

    @Json(name = "pembicara")
    val pembicara: String? = null,

    @Json(name = "tentang")
    val tentang: String? = null,

    @Json(name = "foto")
    val foto: String? = null,

)