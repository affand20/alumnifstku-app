package id.trydev.alumnifstku.model

data class DefaultResponse<out T: Any?> (

    val success: Boolean? = null,
    val message: String? = null,
    val data: T? = null

)