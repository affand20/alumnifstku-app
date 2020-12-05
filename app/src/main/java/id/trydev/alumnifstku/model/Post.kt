package id.trydev.alumnifstku.model

import android.nfc.Tag
import com.squareup.moshi.Json

data class Post (

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "foto")
    val foto: String? = null,

    @Json(name = "deskripsi")
    val deskripsi: String? = null,

    @Json(name = "alumni")
    val alumni: Alumni? = null,

    @Json(name = "tag")
    val tag: List<Tag>? = null,

    @Json(name = "likes")
    val likes: List<Likes>? = null,

    @Json(name = "comment")
    val comments: List<Comment>? = null,



    )