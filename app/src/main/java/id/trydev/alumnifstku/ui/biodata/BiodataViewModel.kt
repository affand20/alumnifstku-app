package id.trydev.alumnifstku.ui.biodata

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.trydev.alumnifstku.model.Alumni
import id.trydev.alumnifstku.model.Biodata
import id.trydev.alumnifstku.model.DefaultResponse
import id.trydev.alumnifstku.network.ApiFactory
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.network.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class BiodataViewModel: ViewModel() {

    /* State untuk memeriksa status request, apakah START, SUCCES, atau FAILED. */
    private val _state = MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state

    /*
    * Variabel untuk mengambil base response dari server
    * */
    private val _response = MutableLiveData<DefaultResponse<Biodata>>()
    val response: LiveData<DefaultResponse<Biodata>>
        get() = _response

    /* Variabel untuk menampung error yg bukan berasal dari server */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    /* Untuk asynchronous execution */
    private var job = Job()
    private val uiScope = CoroutineScope(job+ Dispatchers.Main)

    private var biodataAttribute =  HashMap<String, Any>()
    private var biodataAttribute2 =  HashMap<String, Any>()
    private var filePath: String? = null

    /* Save temporarily data from each fragment */
    fun addBioAttr(mapAttr: HashMap<String, String>) {
        biodataAttribute.putAll(mapAttr)
        Log.d("FOTO PATH", "${biodataAttribute["foto"]}")
    }

    /* Upload Biodata function
    * ========================
    * Params:
    * 1. apiToken
    * Another params fetched from biodataAttribute variable
    *  */
    fun uploadBio(apiToken: String,) {
        _state.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {
                val file = File(filePath)

                when(val response = ApiFactory.uploadBio(
                    apiToken,
                    RequestBody.create(MultipartBody.FORM, biodataAttribute["nama"] as String),
                    RequestBody.create(MultipartBody.FORM, biodataAttribute["alamat"] as String),
                    RequestBody.create(MultipartBody.FORM, biodataAttribute["umumr"] as String),
                    RequestBody.create(MultipartBody.FORM, biodataAttribute["ttl"] as String),
                    RequestBody.create(MultipartBody.FORM, biodataAttribute["jenis_kelamin"] as String),
                    RequestBody.create(MultipartBody.FORM, biodataAttribute["angkatan"] as String),
                    RequestBody.create(MultipartBody.FORM, biodataAttribute["jurusan"] as String),
                    RequestBody.create(MultipartBody.FORM, biodataAttribute2["linkedin"] as String?),
                    MultipartBody.Part.createFormData(
                        "foto",
                        file.name,
                        RequestBody.create(MediaType.parse("image/*"), file)
                    )
                )) {
                    is Result.Success -> {
                        _state.postValue(RequestState.REQUEST_END)
                        _response.postValue(response.data)
                    }

                    is Result.Error -> {
                        _state.postValue(RequestState.REQUEST_ERROR)
                        _error.postValue(response.exception)
                    }
                }
            } catch (t: Throwable) {
                _state.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }

    fun uploadBioWithTracing(apiToken: String) {
        Log.d("VIEWMODEL" , "LAUNCH!")

        var foto: MultipartBody.Part? = null
        if (biodataAttribute["foto"] != null) {
            val file = File(biodataAttribute["foto"].toString())
            foto = MultipartBody.Part.createFormData(
                    "foto",
                    file.name,
                    RequestBody.create(MediaType.parse("image/*"), file))
        }

        var linkedin: RequestBody? = null
        if (biodataAttribute["linkedin"] != null) {
            linkedin = RequestBody.create(MultipartBody.FORM, biodataAttribute["linkedin"].toString())
        }

        _state.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {
                when(val response = ApiFactory.uploadBioAndTracing(
                        apiToken,
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["nama"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["domisili"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["alamat"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["umur"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["ttl"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["jenis_kelamin"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["angkatan"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["jurusan"].toString()),
                        linkedin,
                        foto,
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["company"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["year_joined"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["cluster"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["position"].toString()),
                )) {
                    is Result.Success -> {
                        _state.postValue(RequestState.REQUEST_END)
                        _response.postValue(response.data)
                    }

                    is Result.Error -> {
                        _state.postValue(RequestState.REQUEST_ERROR)
                        _error.postValue(response.exception)
                    }
                }
            } catch (t: Throwable) {
                _state.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }

}