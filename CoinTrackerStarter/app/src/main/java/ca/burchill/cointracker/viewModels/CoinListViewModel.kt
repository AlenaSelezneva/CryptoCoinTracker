package ca.burchill.cointracker.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.burchill.cointracker.database.getDatabase
import ca.burchill.cointracker.domain.Coin
import ca.burchill.cointracker.network.CoinApi
import ca.burchill.cointracker.network.CoinApiResponse
import ca.burchill.cointracker.network.NetworkCoin
import ca.burchill.cointracker.network.asDomainModel
import ca.burchill.cointracker.repository.CoinsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await


enum class CoinApiStatus { LOADING, ERROR, DONE }


class CoinListViewModel(application: Application) : AndroidViewModel(application) {

//    private var _eventNetworkError = MutableLiveData<Boolean>(false)
//    val eventNetworkError: LiveData<Boolean>
//        get() = _eventNetworkError
//
//
//    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
//    val isNetworkErrorShown: LiveData<Boolean>
//        get() = _isNetworkErrorShown


    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<CoinApiStatus>()
    val status: LiveData<CoinApiStatus>
        get() = _status


//    private val _coins = MutableLiveData<List<Coin>>()
//    val coins: LiveData<List<Coin>>
//        get() = _coins

    private val coinsRepository = CoinsRepository(getDatabase(application ))

    val coins = coinsRepository.coins

    // or use viewModelScope
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        getCoins()
    }

    private fun getCoins() {

       coroutineScope.launch {
            try {
//                var coinResult = CoinApi.retrofitService.getCoins()
//                if (coinResult.coins.size > 0) {
//                    _coins.value = asDomainModel( coinResult.coins)
//                }
                coinsRepository.refreshCoins()
//                _eventNetworkError.value = false
//                _isNetworkErrorShown.value = false
            } catch (t: Throwable) {
               _status.value = CoinApiStatus.ERROR
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}