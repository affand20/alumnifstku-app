package id.trydev.alumnifstku.ui.pengaturan.pekerjaan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.trydev.alumnifstku.model.Alumni
import id.trydev.alumnifstku.model.DefaultResponse
import id.trydev.alumnifstku.model.Tracing
import id.trydev.alumnifstku.network.ApiFactory
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.network.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PengaturanPekerjaanViewModel: ViewModel() {

    /* State untuk memeriksa status request, apakah START, SUCCES, atau FAILED. */
    private val _state = MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state

    /*
    * Variabel untuk mengambil base response dari server
    * */
    private val _response = MutableLiveData<DefaultResponse<Tracing>>()
    val response: LiveData<DefaultResponse<Tracing>>
        get() = _response

    /*
   * Variabel untuk mengambil base response UPDATE dari server
   * */
    private val _responseupdate = MutableLiveData<DefaultResponse<Tracing>>()
    val responseupdate: LiveData<DefaultResponse<Tracing>>
        get() = _responseupdate

    /* Variabel untuk menampung error yg bukan berasal dari server */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    /* Untuk asynchronous execution */
    private var job = Job()
    private val uiScope = CoroutineScope(job+ Dispatchers.Main)

    private var traceAttribute =  HashMap<String, Any>()
    private lateinit var uid: String

    fun addTraceAttr(mapAttr: HashMap<String, Any>) {
        traceAttribute.putAll(mapAttr)
    }

    fun createTrace(apiToken: String){
        _state.postValue(RequestState.REQUEST_START)
        uiScope.launch {

            try {
                when(val response = ApiFactory.createTrace(
                        apiToken,
                        RequestBody.create(MultipartBody.FORM, traceAttribute["company"].toString()),
                        RequestBody.create(MultipartBody.FORM, traceAttribute["cluster"].toString()),
                        RequestBody.create(MultipartBody.FORM, traceAttribute["tahun_masuk"].toString()),
                        RequestBody.create(MultipartBody.FORM, traceAttribute["jabatan"].toString()),
                )){
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

    fun updateTrace(apiToken: String){

        uid = traceAttribute["id"].toString()

        _state.postValue(RequestState.REQUEST_START)
        uiScope.launch {

            try {
                when(val response = ApiFactory.updateTrace(
                        apiToken,
                        uid.toInt(),
                        RequestBody.create(MultipartBody.FORM, traceAttribute["company"].toString()),
                        RequestBody.create(MultipartBody.FORM, traceAttribute["cluster"].toString()),
                        RequestBody.create(MultipartBody.FORM, traceAttribute["tahun_masuk"].toString()),
                        RequestBody.create(MultipartBody.FORM, traceAttribute["jabatan"].toString()),
                )){
                    is Result.Success -> {
                        _state.postValue(RequestState.REQUEST_END)
                        _responseupdate.postValue(response.data)
                    }

                    is Result.Error -> {
                        _state.postValue(RequestState.REQUEST_ERROR)
                        _error.postValue(response.exception)
                    }
                }
            }catch (t: Throwable) {
                _state.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }
}