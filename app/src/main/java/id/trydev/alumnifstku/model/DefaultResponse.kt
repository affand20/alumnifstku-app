package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class DefaultResponse<out T: Any?> (

    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: T? = null

)