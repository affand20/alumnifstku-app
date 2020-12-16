package id.trydev.alumnifstku.ui.pengaturan.biodata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import id.trydev.alumnifstku.model.Biodata
import id.trydev.alumnifstku.model.DefaultResponse
import id.trydev.alumnifstku.network.ApiFactory
import id.trydev.alumnifstku.network.ApiService
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

class PengaturanBiodataViewModel: ViewModel() {

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

    private val _submitresponse = MutableLiveData<DefaultResponse<Biodata>>()
    val submitresponse: LiveData<DefaultResponse<Biodata>>
        get() = _submitresponse

    /* Variabel untuk menampung error yg bukan berasal dari server */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    /* Untuk asynchronous execution */
    private var job = Job()
    private val uiScope = CoroutineScope(job+ Dispatchers.Main)

    private var biodataAttribute =  HashMap<String, Any>()
    private var filePath: String? = null


    fun addBioAttr(mapAttr: HashMap<String, String>) {
        biodataAttribute.putAll(mapAttr)
    }

    fun getBiodata(apiToken: String){

        _state.postValue(RequestState.REQUEST_START)
        uiScope.launch {

            try {
                when(val response = ApiFactory.myBio(apiToken)){
                    is Result.Success -> {
                        _state.postValue(RequestState.REQUEST_END)
                        _response.postValue(response.data)
                    }

                    is Result.Error -> {
                        _state.postValue(RequestState.REQUEST_ERROR)
//                        _error.postValue(response.exception)
                        _response.postValue(Gson().fromJson(response.exception, DefaultResponse::class.java) as DefaultResponse<Biodata>)
                    }

                }
            } catch (t: Throwable) {
                _state.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }

    fun postBiodata(apiToken: String) {

        _state.postValue(RequestState.REQUEST_START)
        var foto: MultipartBody.Part? = null
        if (filePath != null) {
            val file = File(filePath)
            foto = MultipartBody.Part.createFormData(
                    "foto",
                    file.name,
                    RequestBody.create(MediaType.parse("image/*"), file))
        }

        var linkedin: RequestBody? = null
        if (biodataAttribute["linkedin"] != null) {
            linkedin = RequestBody.create(MultipartBody.FORM, biodataAttribute["linkedin"].toString())
        }

        uiScope.launch {

            try {
                when(val response = ApiFactory.updateBio(
                        apiToken,
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["nama"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["domisili"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["alamat"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["umur"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["ttl"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["jenis kelamin"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["angkatan"].toString()),
                        RequestBody.create(MultipartBody.FORM, biodataAttribute["jurusan"].toString()),
                        linkedin,
                        foto
                )){
                    is Result.Success -> {
                        _state.postValue(RequestState.REQUEST_END)
                        _submitresponse.postValue(response.data)
                    }

                    is Result.Error -> {
                        _state.postValue(RequestState.REQUEST_ERROR)
                        _submitresponse.postValue(Gson().fromJson(response.exception, DefaultResponse::class.java) as DefaultResponse<Biodata>)
                    }
                }
            } catch (t: Throwable) {
                _state.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}