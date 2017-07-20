package com.chongos.loancalculator.account.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.chongos.loancalculator.App
import com.chongos.loancalculator.R
import com.chongos.loancalculator.account.create.CreateAccountActivity
import com.chongos.loancalculator.base.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account_list.*
import timber.log.Timber


/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
class AccountListActivity : BaseActivity() {

    companion object {

        fun getCallingIntent(context: Context) = Intent(context, AccountListActivity::class.java)
    }

    val adapter by lazy { AccountListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_list)

        toolbar.title = getString(R.string.app_name)
        buttonAdd.setOnClickListener {
            startActivity(CreateAccountActivity.getCallingIntent(this))
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onStartSubscribe(disposable: CompositeDisposable) {
        super.onStartSubscribe(disposable)
        disposable.add(App.database
                .account()
                .loadAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter.list = it
                }, {
                    Timber.e(it)
                })
        )
    }

}
