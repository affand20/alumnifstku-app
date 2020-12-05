package id.trydev.alumnifstku.network

import android.util.Log
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import id.trydev.alumnifstku.model.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiFactory {

    // prod
    const val BASE_URL = "http://alumnifstku.trydev.my.id/api/"

    private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    private val client = OkHttpClient().newBuilder()
        .addInterceptor(interceptor)
        .build()

    lateinit var apiService: ApiService

    init {
        makeService()
    }

    private fun makeService() {
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            ))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        this.apiService = retrofit.create(ApiService::class.java)
    }

    private suspend fun <T: Any> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val myResp = call.invoke()
            if (myResp.isSuccessful) {
                Result.Success(myResp.body()!!)
            } else {
                if (myResp.code() == 403) {
                    Log.i("RESPONSE_CODE", "FORBIDDEN")
                }
                Result.Error(myResp.errorBody()?.string() ?: "Something goes wrong")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Internet error runs")
        }
    }

    ////////////// AUTH ENDPOINT //////////////
    /* Login function */
    suspend fun login(username: String? = null, email: String? = null, password: String): Result<DefaultResponse<Alumni>> {
        return safeApiCall { apiService.login(username, email, password) }
    }

    /* Register function */
    suspend fun register(username: String, email: String, password: String): Result<DefaultResponse<Alumni>> {
        return safeApiCall { apiService.register(username, email, password) }
    }

    /*
    * Forgot Password function
    * ======================
    * Params:
    * 1. username   => required kalau email null
    * 2. email      => required kalau username null
    * */
    suspend fun forgotPassword(username: String? = null, email: String? = null): Result<DefaultResponse<Nothing>> {
        Log.d("API FACTORY", "EXECUTED")
        return safeApiCall { apiService.forgotPassword(username, email) }
    }

    /*
    * Resend Email Verif. function
    * ======================
    * Params:
    * 1. username   => required kalau email null
    * 2. email      => required kalau username null
    * */
    suspend fun resendEmailVerif(username: String? = null, email: String? = null): Result<DefaultResponse<Nothing>> {
        return safeApiCall { apiService.resendVerification(username, email) }
    }

    /* Is Alumni Has Verified checking function*/
    suspend fun hasVerified(apiToken: String): Result<Boolean> {
        return safeApiCall { apiService.hasVerified(apiToken) }
    }

    /* Upload Biodata function */
    suspend fun uploadBio(
        apiToken: String,
        nama: RequestBody,
        alamat: RequestBody,
        umur: RequestBody,
        ttl: RequestBody,
        jenis_kelamin: RequestBody,
        angkatan: RequestBody,
        jurusan: RequestBody,
        linkedin: RequestBody? = null,
        foto: MultipartBody.Part? = null): Result<DefaultResponse<Biodata>> {
        return safeApiCall { apiService.uploadBio(
            "Bearer $apiToken",
            nama,
            alamat,
            umur,
            ttl,
            jenis_kelamin,
            angkatan,
            jurusan,
            linkedin,
            foto
        ) }
    }

    /* Upload Biodata function */
    suspend fun uploadBioAndTracing(
            apiToken: String,
            nama: RequestBody,
            alamat: RequestBody,
            umur: RequestBody,
            ttl: RequestBody,
            jenis_kelamin: RequestBody,
            angkatan: RequestBody,
            jurusan: RequestBody,
            linkedin: RequestBody? = null,
            foto: MultipartBody.Part? = null,
            company: RequestBody,
            yearJoined: RequestBody,
            cluster: RequestBody,
            position: RequestBody,
    ): Result<DefaultResponse<Biodata>> {
        Log.d("API FACTORY", "EXECUTED!")
        return safeApiCall { apiService.uploadBioAndTracing(
                "Bearer $apiToken",
                nama,
                alamat,
                umur,
                ttl,
                jenis_kelamin,
                angkatan,
                jurusan,
                linkedin,
                foto,
                company,
                yearJoined,
                cluster,
                position
        ) }
    }

    /* Update Biodata function */
    suspend fun updateBio(
            apiToken: String,
            nama: RequestBody,
            alamat: RequestBody,
            umur: RequestBody,
            ttl: RequestBody,
            jenis_kelamin: RequestBody,
            angkatan: RequestBody,
            jurusan: RequestBody,
            linkedin: RequestBody? = null,
        foto: MultipartBody.Part? = null): Result<DefaultResponse<Biodata>> {
        return safeApiCall { apiService.updateBio(
            "Bearer $apiToken",
                nama,
                alamat,
                umur,
                ttl,
                jenis_kelamin,
                angkatan,
                jurusan,
                linkedin,
                foto
        ) }
    }

    /* Get Self Biodata function */
    suspend fun myBio(apiToken: String): Result<DefaultResponse<Biodata>> {
        return safeApiCall { apiService.myBio(apiToken) }
    }

    /* Get List Alumni function */
    suspend fun listAlumni(apiToken: String, query: Map<String, String>): Result<DefaultResponse<List<Alumni>>> {
        return safeApiCall { apiService.listAlumni("Bearer $apiToken", query) }
    }

    /* Get Detail Alumni function */
    suspend fun detailAlumni(apiToken: String, id: Int): Result<DefaultResponse<Alumni>> {
        return safeApiCall { apiService.detailAlumni("Bearer $apiToken", id) }
    }

    /* Get List Loker function */
    suspend fun listLoker(apiToken: String, query: Map<String, String>): Result<DefaultResponse<List<Loker>>> {
        return safeApiCall { apiService.listLoker("Bearer $apiToken", query) }
    }

    /* Get Detail Loker function */
    suspend fun detailLoker(apiToken: String, id: Int): Result<DefaultResponse<Loker>> {
        return safeApiCall { apiService.detailLoker("Bearer $apiToken", id) }
    }

    /* Get List News function */
    suspend fun listNews(apiToken: String, query: Map<String, String>): Result<DefaultResponse<List<News>>> {
        return safeApiCall { apiService.listNews("Bearer $apiToken", query) }
    }

    /* Get Detail News function */
    suspend fun detailNews(apiToken: String, id:Int): Result<DefaultResponse<News>> {
        return safeApiCall { apiService.detailNews("Bearer $apiToken", id) }
    }

    /* Get List Kelas function */
    suspend fun listKelas(apiToken: String, query: Map<String, String>): Result<DefaultResponse<List<Kelas>>> {
        return safeApiCall { apiService.listKelas("Bearer $apiToken", query) }
    }

    /* Get Detail Kelas function */
    suspend fun detailKelas(apiToken: String, id: Int): Result<DefaultResponse<Kelas>> {
        return safeApiCall { apiService.detailKelas("Bearer $apiToken", id) }
    }

    /* Get Kelas Participants function */
    suspend fun kelasParticipant(apiToken: String, id: Int): Result<DefaultResponse<List<BookingKelas>>> {
        return safeApiCall { apiService.kelasParticipants("Bearer $apiToken", id) }
    }

    /* Book Kelas Alumni Ticket function */
    suspend fun bookKelas(
        apiToken: String,
        id: Int,
        email: String,
        namaLengkap: String,
        whatsapp: String
    ): Result<DefaultResponse<BookingKelas>> {
        return safeApiCall { apiService.bookKelas("Bearer $apiToken", id, email, namaLengkap, whatsapp) }
    }

    /* Un-book Kelas Alumni Ticket function */
    suspend fun unbookKelas(apiToken: String, id: Int): Result<DefaultResponse<Nothing>> {
        return safeApiCall { apiService.unbookKelas("Bearer $apiToken", id) }
    }

    /* Resend Ticket Booking Kelas Alumni function */
    suspend fun resendTicketKelas(apiToken: String, id: Int): Result<DefaultResponse<Nothing>> {
        return safeApiCall { apiService.resendTicketKelas("Bearer $apiToken", id) }
    }

    /* Get Timeline Post function */
    suspend fun getTimelinePost(apiToken:String): Result<DefaultResponse<List<Post>>> {
        return safeApiCall { apiService.timelinePost(apiToken) }
    }

    /* Get Detail Post function */
    suspend fun getDetailPost(apiToken: String, id: Int): Result<DefaultResponse<Post>> {
        return safeApiCall { apiService.detailPost("Bearer $apiToken", id) }
    }

    /* Get All My Post function */
    suspend fun getAllMyPost(apiToken: String): Result<DefaultResponse<List<Post>>> {
        return safeApiCall { apiService.myPost(apiToken) }
    }

    /* Upload Post function */
    suspend fun uploadPost(
        apiToken: String,
        deskripsi: RequestBody,
        foto: MultipartBody.Part): Result<DefaultResponse<Post>> {
        return safeApiCall { apiService.uploadPost("Bearer $apiToken", deskripsi, foto) }
    }

    /* Update Post function */
    suspend fun updatePost(
        apiToken: String,
        id: Int,
        deskripsi: RequestBody,
        foto: MultipartBody.Part): Result<DefaultResponse<Post>> {
        return safeApiCall { apiService.updatePost("Bearer $apiToken", id, deskripsi, foto) }
    }

    /* Like Post function */
    suspend fun likePost(apiToken: String, id: Int): Result<DefaultResponse<Nothing>> {
        return safeApiCall { apiService.likePost("Bearer $apiToken", id) }
    }

    /* Unlike Post function */
    suspend fun unlikePost(apiToken: String, id: Int): Result<DefaultResponse<Nothing>> {
        return safeApiCall { apiService.unlikePost("Bearer $apiToken", id) }
    }

    /* Post Comment function */
    suspend fun postComment(apiToken: String, id: Int, teks: String): Result<DefaultResponse<Comment>> {
        return safeApiCall { apiService.commentPost("Bearer $apiToken", id, teks) }
    }

    /* Remove Comment function */
    suspend fun removeComment(apiToken: String, id: Int, commentId: Int): Result<DefaultResponse<Nothing>> {
        return safeApiCall { apiService.removeCommentPost("Bearer $apiToken", id, commentId) }
    }

    /* Get All Comment */
    suspend fun getAllCommment(apiToken: String, id: Int): Result<DefaultResponse<List<Comment>>> {
        return safeApiCall { apiService.getComments("Bearer $apiToken", id) }
    }

}