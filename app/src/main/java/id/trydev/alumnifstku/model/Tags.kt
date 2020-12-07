package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class Tags (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("alumni_id")
    val alumniId: String? = null,

    @SerializedName("sharing_alumni_id")
    val postId: String? = null

)