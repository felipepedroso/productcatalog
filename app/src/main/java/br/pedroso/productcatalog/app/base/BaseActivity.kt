package br.pedroso.productcatalog.app.base

import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {
    private val subscriptions by lazy { CompositeDisposable() }

    protected fun registerSubscription(subscription: Disposable) {
        subscriptions.add(subscription)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseSubscriptions()
    }

    private fun releaseSubscriptions() {
        subscriptions.clear()
    }

}