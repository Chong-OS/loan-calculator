package com.chongos.loancalculator.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.chongos.loancalculator.App
import com.chongos.loancalculator.R
import com.chongos.loancalculator.data.entity.Account
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import kotlin.system.measureTimeMillis


/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
class MainActivity : AppCompatActivity() {

    companion object {

        fun getCallingIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.title = getString(R.string.app_name)
        buttonAdd.setOnClickListener {
            Single.fromCallable {
                App.database.account().insert(Account().apply {
                    name = "TMB"
                    amount = 2000000.0
                    startTime = measureTimeMillis { }
                    years = 30
                })
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
                    }, {
                        Timber.e(it)
                    })
        }
    }
}
