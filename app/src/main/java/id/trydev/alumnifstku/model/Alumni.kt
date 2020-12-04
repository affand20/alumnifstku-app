package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class Alumni (

    @Json(name="id")
    val id:String? = null,

    @Json(name="email")
    val email:String? = null,

    @Json(name="username")
    val username:String? = null,

    @Json(name="password")
    val password:String? = null,

    @Json(name="token_registration")
    val tokenRegist:String? = null,

    @Json(name="api_token")
    val apiToken:String? = null,

    @Json(name="created_at")
    val createdAt:String? = null,

    var isVerified: Boolean? = null,

    @Json(name = "biodata")
    val biodata: Biodata? = null,

    @Json(name = "tracing")
    val tracing: List<Tracing>? = null

//    @Json(name="updated_at")
//    val updatedAt:String? = null,
//
//    @Json(name="verified_at")
//    val verifiedAt:String? = null

)