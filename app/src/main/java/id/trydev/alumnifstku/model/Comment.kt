package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class Comment (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("alumni_id")
    val commentatorId: String? = null,

    @SerializedName("sharing_alumni_id")
    val postId: String? = null,

    @SerializedName("text")
    val comment: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("alumni")
    val alumni: Alumni? = null

)