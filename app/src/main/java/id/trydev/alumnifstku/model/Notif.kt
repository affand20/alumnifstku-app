package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class Notif (

    @SerializedName("id")
    val id:String? = null,

    @SerializedName("alumni_id")
    val alumni_id:String? = null,

    @SerializedName("sharing_id")
    val sharing_id:String? = null,

    @SerializedName("text")
    val text:String? = null,

    @SerializedName("is_read")
    var is_read: String? = null,

    @SerializedName("post")
    val post:Post? = null,

    @SerializedName("readed_at")
    val readed_at:String? = null,

    @SerializedName("created_at")
    val createdAt:String? = null,





//    @SerializedName("updated_at")
//    val updatedAt:String? = null,
//
//    @SerializedName("verified_at")
//    val verifiedAt:String? = null

)
