package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class DefaultResponse<out T: Any?> (

    @Json(name = "success")
    val success: Boolean? = null,
    @Json(name = "message")
    val message: String? = null,
    @Json(name = "data")
    val data: T? = null

)