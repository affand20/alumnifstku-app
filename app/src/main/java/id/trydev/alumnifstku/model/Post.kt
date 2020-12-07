package id.trydev.alumnifstku.model

import com.google.gson.annotations.SerializedName

data class Post (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("foto")
    val foto: String? = null,

    @SerializedName("deskripsi")
    val deskripsi: String? = null,

    @SerializedName("alumni")
    val alumni: Alumni? = null,

    @SerializedName("tag")
    val tag: List<Tags>? = null,

    @SerializedName("likes")
    val likes: List<Likes>? = null,

    @SerializedName("comment")
    val comments: List<Comment>? = null,



    )