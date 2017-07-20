package com.chongos.loancalculator.base

import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val activityDisposables by lazy { CompositeDisposable() }
    protected val foregroundDisposables by lazy { CompositeDisposable() }

    protected open fun onStartSubscribe(disposable: CompositeDisposable) {

    }

    override fun onStart() {
        super.onStart()
        onStartSubscribe(foregroundDisposables)
    }

    override fun onStop() {
        super.onStop()
        foregroundDisposables.clear()
    }

    override fun onDestroy() {
        activityDisposables.clear()
        super.onDestroy()
    }
}