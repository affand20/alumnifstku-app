package id.trydev.alumnifstku.network

import id.trydev.alumnifstku.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    ////////////// AUTH ENDPOINT //////////////
    /*
    * Login
    * =======================
    * user bisa login dengan username atau email
    * */
    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String?=null,
        @Field("email") email: String?=null,
        @Field("password") password: String
    ): Response<DefaultResponse<Alumni>>

    /*
    * Register
    * =======================
    * */
    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<DefaultResponse<Alumni>>

    /*
    * Forgot Password
    * =======================
    * user bisa mengirimkan username/email yang sesuai dengan akun
    * untuk request token reset password
    * */
    @POST("forgot")
    @FormUrlEncoded
    suspend fun forgotPassword(
        @Field("username") username: String? = null,
        @Field("email") email: String? = null
    ): Response<DefaultResponse<Nothing>>

    /*
    * Resend Email Verification
    * =======================
    * user bisa request kirim ulang email verifikasi dengan mengirimkan
    * username/email yang sesuai dengan akunnya
    * */
    @POST("resend-verification")
    @FormUrlEncoded
    suspend fun resendVerification(
        @Field("username") username: String? = null,
        @Field("email") email: String? = null
    ): Response<DefaultResponse<Nothing>>

    /*
    * Check Self-Account Has Verified
    * =======================
    * untuk ngecek apakah user sudah verifikasi akun atau belum
    * */
    @GET("hasVerified")
    suspend fun hasVerified(
        @Header("Authorization") apiToken: String,
        @Header("Accept") accept: String = "application/json"
    ): Response<Boolean>

    /*
    * Change Password
    * =======================
    * */
    @POST("change-password")
    @FormUrlEncoded
    suspend fun changePassword(
            @Header("Authorization") apiToken: String,
            @Field("old_password") oldPassword: String,
            @Field("new_password") newPassword: String,
            @Header("Accept") accept: String = "application/json"
    ): Response<DefaultResponse<Alumni>>

    ////////////// PROFILE ENDPOINT //////////////
    /*
    * Upload Biodata
    * =======================
    * */
    @POST("biodata/create")
    @Multipart
    suspend fun uploadBio(
        @Header("Authorization") apiToken: String,
        @Part("nama") name: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("umur") umur: RequestBody,
        @Part("ttl") ttl: RequestBody,
        @Part("jenis_kelamin") jenisKelamin: RequestBody,
        @Part("angkatan") angkatan: RequestBody,
        @Part("jurusan") major: RequestBody,
        @Part("linkedin") linkedin: RequestBody? = null,
        @Part foto: MultipartBody.Part? = null,
        @Header("Accept") accept: String = "application/json"
    ): Response<DefaultResponse<Biodata>>

    /*
    * Upload Biodata and Tracing in one
    * =======================
    * */
    @POST("biodata/create-with-tracing")
    @Multipart
    suspend fun uploadBioAndTracing(
            @Header("Authorization") apiToken: String,
            @Part("nama") name: RequestBody,
            @Part("alamat") alamat: RequestBody,
            @Part("umur") umur: RequestBody,
            @Part("ttl") ttl: RequestBody,
            @Part("jenis_kelamin") jenisKelamin: RequestBody,
            @Part("angkatan") angkatan: RequestBody,
            @Part("jurusan") major: RequestBody,
            @Part("linkedin") linkedin: RequestBody? = null,
            @Part foto: MultipartBody.Part? = null,
            @Part("perusahaan") company: RequestBody,
            @Part("tahun_masuk") yearJoined: RequestBody,
            @Part("cluster") cluster: RequestBody,
            @Part("jabatan") position: RequestBody,
            @Header("Accept") accept: String = "application/json"
    ): Response<DefaultResponse<Biodata>>

    /*
    * Update Biodata
    * =======================
    * */
    @POST("biodata/update")
    @Multipart
    suspend fun updateBio(
        @Header("Authorization") apiToken: String,
        @Part("nama") name: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("umur") umur: RequestBody,
        @Part("ttl") ttl: RequestBody,
        @Part("jenis_kelamin") jenisKelamin: RequestBody,
        @Part("angkatan") angkatan: RequestBody,
        @Part("jurusan") major: RequestBody,
        @Part("linkedin") linkedin: RequestBody? = null,
        @Part foto: MultipartBody.Part? = null,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Biodata>>

    /*
    * Get Self Biodata
    * =======================
    * */
    @GET("biodata/my")
    suspend fun myBio(
        @Header("Authorization") apiToken: String,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Biodata>>

    /*
    * Update Pekerjaan baru (CREATE)
    * =======================
     */
    @POST("alumni/tracing/create")
    @Multipart
    suspend fun createTrace(
        @Header("Authorization") apiToken: String,
        @Part("perusahaan") perusahaan: RequestBody,
        @Part("cluster") cluster: RequestBody,
        @Part("tahun_masuk") tahunMasuk: RequestBody,
        @Part("jabatan") jabatan: RequestBody,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Tracing>>

    /*
    * Edit Pekerjaan
    * =======================
     */
    @POST("alumni/tracing/{id}/update")
    @Multipart
    suspend fun updateTrace(
            @Header("Authorization") apiToken: String,
            @Path("id") id: Int,
            @Part("perusahaan") perusahaan: RequestBody,
            @Part("cluster") cluster: RequestBody,
            @Part("tahun_masuk") tahunMasuk: RequestBody,
            @Part("jabatan") jabatan: RequestBody,
            @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Tracing>>

    /*
   * Hapus Pekerjaan
   * =======================
    */
    @POST("alumni/tracing/{id}/remove")
    suspend fun removeTrace(
            @Header("Authorization") apiToken: String,
            @Path("id") id: Int,
            @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Tracing>>

    ////////////// TRACING ALUMNI ENDPOINT //////////////
    /*
    * Get List Alumni
    * =======================
    * Available Query options:
    * 1. angkatan
    * 2. perusahaan
    * 3. filter => (true, false)
    * 4. nama
    * 5. cluster
    * */
    @GET("alumni")
    suspend fun listAlumni(
        @Header("Authorization") apiToken: String,
        @QueryMap query: Map<String, String?>,
        @Header("Accept") accept: String = "application/json"
    ): Response<DefaultResponse<List<Alumni>>>

    /*
    * Get Detail Alumni
    * =======================
    * Required parameter:
    * 1. alumni ID
    * */
    @GET("alumni/{id}")
    suspend fun detailAlumni(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int?,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Alumni>>

    ////////////// LOKER ENDPOINT //////////////
    /*
    * Get List Loker
    * =======================
    * Available query options:
    * 1. order => (asc, desc)
    * 2. filter => (true, false)
    * 3. jabatan
    * 4. perusahaan
    * 5. cluster
    * */
    @GET("loker")
    suspend fun listLoker(
        @Header("Authorization") apiToken: String,
        @QueryMap query: Map<String, String?>,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<List<Loker>>>

    /*
    * Get Detail Loker
    * =======================
    * Required parameter:
    * 1. Loker ID
    * */
    @GET("loker/{id}")
    suspend fun detailLoker(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Loker>>

    ////////////// FST NEWS ENDPOINT //////////////
    /*
    * Get List News
    * =======================
    * Available query options:
    * 1. order => (asc, desc)
    * 2. filter => (true, false)
    * 3. judul
    * */
    @GET("news")
    suspend fun listNews(
        @Header("Authorization") apiToken: String,
        @QueryMap query: Map<String, String>,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<List<News>>>

    /*
    * Get Detail News
    * =======================
    * Required parameter:
    * 1. News ID
    * */
    @GET("news/{id}")
    suspend fun detailNews(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<News>>

    ////////////// KELAS ALUMNI ENDPOINT //////////////
    /*
    * Get List Kelas
    * =======================
    * Available query options:
    * 1. filter => (true, false)
    * 2. judul
    * 3. tanggal
    * */
    @GET("kelas")
    suspend fun listKelas(
        @Header("Authorization") apiToken: String,
        @QueryMap query: Map<String, String>,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<List<Kelas>>>

    /*
    * Get Detail Kelas
    * =======================
    * Required parameter:
    * 1. Kelas ID
    * */
    @GET("kelas/{id}")
    suspend fun detailKelas(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Kelas>>

    /*
    * Get Kelas Participant
    * =======================
    * Required parameter:
    * 1. Kelas ID
    * */
    @GET("kelas/{id}/participants")
    suspend fun kelasParticipants(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<List<BookingKelas>>>

    /*
    * Booking Kelas
    * =======================
    * Required parameter:
    * 1. Kelas ID
    * */
    @POST("kelas/{id}/book")
    @FormUrlEncoded
    suspend fun bookKelas(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Field("email") email:String,
        @Field("nama_lengkap") namaLengkap:String,
        @Field("whatsapp") whatsapp:String,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<BookingKelas>>

    /*
    * Unbooking Kelas
    * =======================
    * Required parameter:
    * 1. Kelas ID
    * */
    @POST("kelas/{id}/unbook")
    suspend fun unbookKelas(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Nothing>>

    /*
    * Resend Ticket Booking Kelas
    * =======================
    * Required parameter:
    * 1. Kelas ID
    * */
    @POST("kelas/{id}/resend-ticket")
    suspend fun resendTicketKelas(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Nothing>>


    ////////////// SHARING ALUMNI ENDPOINT //////////////
    /*
    * Get Timeline post
    * =======================
    * */
    @GET("sharing")
    suspend fun timelinePost(
        @Header("Authorization") apiToken: String,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<List<Post>>>

    /*
    * Get Detail post
    * =======================
    * */
    @GET("sharing/{id}")
    suspend fun detailPost(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Post>>

    /*
    * Get All my post
    * =======================
    * */
    @GET("sharing/my")
    suspend fun myPost(
        @Header("Authorization") apiToken: String,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<List<Post>>>

    /*
    * Upload post
    * =======================
    * */
    @POST("sharing")
    @Multipart
    suspend fun uploadPost(
        @Header("Authorization") apiToken: String,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part foto: MultipartBody.Part,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Post>>

    /*
    * Update post
    * =======================
    * */
    @POST("sharing/{id}/update")
    @Multipart
    suspend fun updatePost(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part foto: MultipartBody.Part,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Post>>

    /*
    * Remove Post
    * ======================
    * */
    @POST("sharing/{id}/remove")
    suspend fun removePost(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json"
    ): Response<DefaultResponse<Nothing>>

    /*
    * Like post
    * =======================
    * */
    @POST("sharing/{id}/like")
    suspend fun likePost(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Nothing>>

    /*
    * Unlike post
    * =======================
    * */
    @POST("sharing/{id}/unlike")
    suspend fun unlikePost(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<Nothing>>

    /*
    * Comment post
    * =======================
    * */
    @POST("sharing/{id}/comment")
    @FormUrlEncoded
    suspend fun commentPost(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Field("text") comment: String,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<List<Comment>>>

    /*
    * Remove Comment post
    * =======================
    * */
    @POST("sharing/{id}/comment/{commentId}/remove")
    suspend fun removeCommentPost(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Path("commentId") commentId: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<List<Comment>>>

    /*
    * Get All comment
    * =======================
    * */
    @GET("sharing/{id}/comment")
    suspend fun getComments(
        @Header("Authorization") apiToken: String,
        @Path("id") id: Int,
        @Header("Accept") accept: String = "application/json",
    ): Response<DefaultResponse<List<Comment>>>

}