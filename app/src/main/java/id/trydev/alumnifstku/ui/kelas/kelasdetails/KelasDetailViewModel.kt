package id.trydev.alumnifstku.ui.kelas.kelasdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import id.trydev.alumnifstku.model.DefaultResponse
import id.trydev.alumnifstku.model.Kelas
import id.trydev.alumnifstku.network.ApiFactory
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.network.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class KelasDetailViewModel: ViewModel() {

    /* State untuk memeriksa status request, apakah START, SUCCES, atau FAILED. */
    private val _state = MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state

    private val _stateUnbook = MutableLiveData<RequestState>()
    val stateUnbook: LiveData<RequestState>
        get() = _stateUnbook

    private val _stateResend = MutableLiveData<RequestState>()
    val stateResend: LiveData<RequestState>
        get() = _stateResend

    /*
    * Variabel untuk mengambil base response dari server
    * */
    private val _response = MutableLiveData<DefaultResponse<Kelas>>()
    val response: LiveData<DefaultResponse<Kelas>>
        get() = _response

    private val _responseNothing = MutableLiveData<DefaultResponse<*>>()
    val responseNothing: LiveData<DefaultResponse<*>>
        get() = _responseNothing

    /* Variabel untuk menampung error yg bukan berasal dari server */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    /* Untuk asynchronous execution */
    private var job = Job()
    private val uiScope = CoroutineScope(job+ Dispatchers.Main)

    /*
    * Get Kelas function yang dipanggil dari UI
    * */
    fun getKelas(apiToken: String, kelasId: Int) {
        _state.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {
                when(val response = ApiFactory.detailKelas(apiToken, kelasId)) {
                    is Result.Success -> {
                        _state.postValue(RequestState.REQUEST_END)
                        _response.postValue(response.data)
                    }

                    is Result.Error -> {
                        _state.postValue(RequestState.REQUEST_ERROR)
                        _response.postValue(Gson().fromJson(response.exception, DefaultResponse::class.java) as DefaultResponse<Kelas>)
                    }
                }
            } catch (t: Throwable) {
                _state.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }

    fun resendTicket(apiToken: String, kelasId: Int) {
        _stateResend.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {
                when(val response = ApiFactory.resendTicketKelas(apiToken, kelasId)) {
                    is Result.Success -> {
                        _stateResend.postValue(RequestState.REQUEST_END)
                        _responseNothing.postValue(response.data)
                    }

                    is Result.Error -> {
                        _stateResend.postValue(RequestState.REQUEST_ERROR)
                        _responseNothing.postValue(Gson().fromJson(response.exception, DefaultResponse::class.java) as DefaultResponse<*>)
                    }
                }
            } catch (t: Throwable) {
                _stateResend.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }

    fun unbookKelas(apiToken: String, kelasId: Int) {
        _stateUnbook.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {
                when(val response = ApiFactory.unbookKelas(apiToken, kelasId)) {
                    is Result.Success -> {
                        _stateUnbook.postValue(RequestState.REQUEST_END)
                        _responseNothing.postValue(response.data)
                    }

                    is Result.Error -> {
                        _stateUnbook.postValue(RequestState.REQUEST_ERROR)
                        _error.postValue(response.exception)
                    }
                }
            } catch (t: Throwable) {
                _stateUnbook.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}