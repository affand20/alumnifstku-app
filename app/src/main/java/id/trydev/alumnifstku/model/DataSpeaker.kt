package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class DataSpeaker (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("kelas_alumni_id")
    val kelasId: String? = null,

    @SerializedName("pembicara")
    val pembicara: String? = null,

    @SerializedName("tentang")
    val tentang: String? = null,

    @SerializedName("foto")
    val foto: String? = null,

)