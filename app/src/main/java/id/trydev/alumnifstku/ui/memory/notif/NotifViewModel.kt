package id.trydev.alumnifstku.ui.memory.notif

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import id.trydev.alumnifstku.model.DefaultResponse
import id.trydev.alumnifstku.model.Notif
import id.trydev.alumnifstku.network.ApiFactory
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.network.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotifViewModel: ViewModel() {
    
    private val _state =  MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state
    
    private val _response = MutableLiveData<DefaultResponse<List<Notif>>>()
    val response: LiveData<DefaultResponse<List<Notif>>>
        get() = _response

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var job = Job()
    private val uiScope = CoroutineScope(job+ Dispatchers.Main)

    fun getMyNotif(apiToken: String) {
        _state.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {

                when(val response = ApiFactory.getNotif(apiToken)) {
                    is Result.Success -> {
                        _state.postValue(RequestState.REQUEST_END)
                        _response.postValue(response.data)
                    }

                    is Result.Error -> {
                        _state.postValue(RequestState.REQUEST_ERROR)
                        _response.postValue(Gson().fromJson(response.exception, DefaultResponse::class.java) as DefaultResponse<List<Notif>>?)
                    }
                }

            } catch (t: Throwable) {
                _state.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }
}