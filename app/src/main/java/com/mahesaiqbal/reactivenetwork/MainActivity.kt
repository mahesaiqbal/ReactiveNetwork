package com.mahesaiqbal.reactivenetwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var connectivityDisposable: Disposable? = null
    private var internetDisposable: Disposable? = null

    companion object {
        private val TAG = "ReactiveNetwork"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        connectivityDisposable = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connectivity ->
                Log.d(TAG, connectivity.toString())
                val state = connectivity.state()
                val name = connectivity.typeName()
                connectivity_status.text = String.format("State: %s, Typename: %s", state, name)
            }

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                internet_status.text = isConnectedToInternet.toString()
            }
    }

    override fun onPause() {
        super.onPause()
        safelyDispose(connectivityDisposable)
        safelyDispose(internetDisposable)
    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }
}
