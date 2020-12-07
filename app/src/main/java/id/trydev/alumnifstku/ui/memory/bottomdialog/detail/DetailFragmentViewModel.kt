package id.trydev.alumnifstku.ui.memory.bottomdialog.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.trydev.alumnifstku.model.Comment
import id.trydev.alumnifstku.model.DefaultResponse
import id.trydev.alumnifstku.model.Post
import id.trydev.alumnifstku.network.ApiFactory
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.network.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailFragmentViewModel: ViewModel() {

    /* State untuk memeriksa status request, apakah START, SUCCES, atau FAILED. */
    private val _state = MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state

    private val _stateComment = MutableLiveData<RequestState>()
    val stateComment: LiveData<RequestState>
        get() = _stateComment

    /*
    * Variabel untuk mengambil base response dari server
    * */
    private val _response = MutableLiveData<DefaultResponse<Post>>()
    val response: LiveData<DefaultResponse<Post>>
        get() = _response

    private val _responseComment = MutableLiveData<DefaultResponse<List<Comment>>>()
    val responseComment: LiveData<DefaultResponse<List<Comment>>>
        get() = _responseComment

    /* Variabel untuk menampung error yg bukan berasal dari server */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    /* Untuk asynchronous execution */
    private var job = Job()
    private val uiScope = CoroutineScope(job+ Dispatchers.Main)

    /*
    * Loker function yang dipanggil dari UI
    * */
    fun getPosts(apiToken: String, postId: Int) {
        _state.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {
                when(val response = ApiFactory.getDetailPost(apiToken, postId)) {
                    is Result.Success -> {
                        _state.postValue(RequestState.REQUEST_END)
                        _response.postValue(response.data)
                    }

                    is Result.Error -> {
                        _state.postValue(RequestState.REQUEST_ERROR)
//                        _response.postValue(Gson().fromJson(response.exception, DefaultResponse::class.java) as DefaultResponse<List<Loker>>?)
                        _error.postValue(response.exception)
                    }
                }
            } catch (t: Throwable) {
                _state.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }

    fun postComment(apiToken: String, postId: Int, comment: String) {
        _stateComment.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {
                when(val response = ApiFactory.postComment(apiToken, postId, comment)) {
                    is Result.Success -> {
                        _stateComment.postValue(RequestState.REQUEST_END)
                        _responseComment.postValue(response.data)
                    }

                    is Result.Error -> {
                        _stateComment.postValue(RequestState.REQUEST_ERROR)
//                        _responseComment.postValue(Gson().fromJson(response.exception, DefaultResponse::class.java) as DefaultResponse<List<Loker>>?)
                        _error.postValue(response.exception)
                    }
                }
            } catch (t: Throwable) {
                _stateComment.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }

    fun getComments(apiToken: String, postId: Int) {
        _stateComment.postValue(RequestState.REQUEST_START)
        uiScope.launch {
            try {
                when(val response = ApiFactory.getAllCommment(apiToken, postId)) {
                    is Result.Success -> {
                        _stateComment.postValue(RequestState.REQUEST_END)
                        _responseComment.postValue(response.data)
                    }

                    is Result.Error -> {
                        _stateComment.postValue(RequestState.REQUEST_ERROR)
//                        _responseComment.postValue(Gson().fromJson(response.exception, DefaultResponse::class.java) as DefaultResponse<List<Loker>>?)
                        _error.postValue(response.exception)
                    }
                }
            } catch (t: Throwable) {
                _stateComment.postValue(RequestState.REQUEST_ERROR)
                _error.postValue(t.localizedMessage)
            }
        }
    }


}