package com.chongos.loancalculator.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
abstract class BaseFragment : Fragment() {

    protected val fragmentDisposables by lazy { CompositeDisposable() }
    protected val foregroundDisposables by lazy { CompositeDisposable() }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(getLayoutResource(), container, false)

    protected open fun onStartSubscribe(disposable: CompositeDisposable) {

    }

    override fun onStart() {
        super.onStart()
        onStartSubscribe(foregroundDisposables)
    }

    override fun onStop() {
        super.onStop()
        foregroundDisposables.dispose()
    }

    override fun onDestroyView() {
        fragmentDisposables.dispose()
        super.onDestroyView()
    }

    @LayoutRes
    protected abstract fun getLayoutResource(): Int
}