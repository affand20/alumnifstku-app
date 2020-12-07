package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class BookingKelas (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("kelas_alumni_id")
    val kelasId: String? = null,

    @SerializedName("alumni_id")
    val alumniId: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("nama_lengkap")
    val name: String? = null,

//    @SerializedName("whatsapp")
//    val whatsapp: String? = null,
//
)