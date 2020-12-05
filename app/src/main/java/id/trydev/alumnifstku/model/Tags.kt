package id.trydev.alumnifstku.model

import com.squareup.moshi.Json

data class Tags (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "alumni_id")
    val alumniId: String? = null,

    @Json(name = "sharing_alumni_id")
    val postId: String? = null

)