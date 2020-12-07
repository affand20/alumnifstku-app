package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class Alumni (

    @SerializedName("id")
    val id:String? = null,

    @SerializedName("email")
    val email:String? = null,

    @SerializedName("username")
    val username:String? = null,

    @SerializedName("password")
    val password:String? = null,

    @SerializedName("token_registration")
    val tokenRegist:String? = null,

    @SerializedName("api_token")
    val apiToken:String? = null,

    @SerializedName("created_at")
    val createdAt:String? = null,

    var isVerified: Boolean? = null,

    @SerializedName( "biodata")
    val biodata: Biodata? = null,

    @SerializedName( "tracing")
    val tracing: List<Tracing>? = null

//    @SerializedName("updated_at")
//    val updatedAt:String? = null,
//
//    @SerializedName("verified_at")
//    val verifiedAt:String? = null

)