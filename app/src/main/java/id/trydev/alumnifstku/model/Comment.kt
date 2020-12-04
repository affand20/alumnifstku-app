package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class Comment (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "alumni_id")
    val commentatorId: String? = null,

    @Json(name = "sharing_alumni_id")
    val postId: String? = null,

    @Json(name = "text")
    val comment: String? = null,

    @Json(name = "created_at")
    val createdAt: String? = null,

)