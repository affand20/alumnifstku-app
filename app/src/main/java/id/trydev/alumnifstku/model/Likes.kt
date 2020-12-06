package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class Likes (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("sharing_alumni_id")
    val postId: String? = null,

    /*
    * User ID orang yang ngelike postingannya
    * */
    @SerializedName("alumni_id")
    val alumniId: String? = null,

)