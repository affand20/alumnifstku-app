package id.trydev.alumnifstku.ui.kelas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.trydev.alumnifstku.model.DefaultResponse
import id.trydev.alumnifstku.model.Kelas
import id.trydev.alumnifstku.model.News
import id.trydev.alumnifstku.network.ApiFactory
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.network.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class KelasViewModel: ViewModel() {

    /* State untuk memeriksa status request, apakah START, SUCCES, atau FAILED. */
    private val _state = MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state

    /*
    * Variabel untuk mengambil base response dari server
    * */
    private val _response = MutableLiveData<DefaultResponse<List<Kelas>>>()
    val response: LiveData<DefaultResponse<List<Kelas>>>
        get() = _response

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
    fun getKelas(apiToken: String, query: Map<String, String>) {
        _state.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {
                when(val response = ApiFactory.listKelas(apiToken, query)) {
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