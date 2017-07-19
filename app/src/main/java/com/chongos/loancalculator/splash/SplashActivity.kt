package com.chongos.loancalculator.splash

import com.chongos.loancalculator.base.BaseActivity
import com.chongos.loancalculator.main.MainActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
class SplashActivity : BaseActivity() {

    override fun onStartSubscribe(disposable: CompositeDisposable) {
        super.onStartSubscribe(disposable)
        disposable.add(Single.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _, _ ->
                    startActivity(MainActivity.getCallingIntent(this))
                    finish()
                })
    }

    override fun onBackPressed() {

    }
}