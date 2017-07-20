package com.chongos.loancalculator.account.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.chongos.loancalculator.App
import com.chongos.loancalculator.R
import com.chongos.loancalculator.base.BaseActivity
import com.chongos.loancalculator.data.entity.Account
import com.chongos.loancalculator.data.entity.Rate
import com.chongos.loancalculator.utils.view.numberWatcher
import com.chongos.loancalculator.utils.view.textWatcher
import com.chongos.loancalculator.utils.view.toViewVisible
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account_create.*
import timber.log.Timber

/**
 * @author ChongOS
 * @since 20-Jul-2017
 */
class CreateAccountActivity : BaseActivity() {

    companion object {

        fun getCallingIntent(context: Context) = Intent(context, CreateAccountActivity::class.java)
    }

    val tierRateAdapter by lazy { TierRateAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_create)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerViewRate.layoutManager = LinearLayoutManager(this)
        recyclerViewRate.adapter = tierRateAdapter

        buttonCreate.setOnClickListener {
            activityDisposables.add(Single
                    .fromCallable {
                        App.database.account().insert(Account().apply {
                            name = inputName.text.toString()
                            amount = inputAmount.text.toString().let { if (it.contains(",")) it.replace(",".toRegex(), "") else it }.toDouble()
                            years = inputTime.text.toString().toInt()
                        })
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
                        finish()
                    }, {
                        Timber.e(it)
                    })
            )
        }
        buttonSwitchRate.setOnClickListener {
            val isMortgageMode = inputRate.visibility != View.VISIBLE
            val singleRateVisibility = isMortgageMode.toViewVisible()
            val mortgageRateVisibility = isMortgageMode.not().toViewVisible()

            buttonSwitchRate.text = getString(if (isMortgageMode) R.string.button_switch_to_mortgage else R.string.button_switch_to_single)
            inputRate.visibility = singleRateVisibility
            inputRateHint.visibility = singleRateVisibility
            inputRateUnit.visibility = singleRateVisibility
            recyclerViewRate.visibility = mortgageRateVisibility
            recyclerViewRateHint.visibility = mortgageRateVisibility
            buttonAddRate.visibility = mortgageRateVisibility
        }
        buttonAddRate.setOnClickListener {
            tierRateAdapter.add(Rate().apply {
                order = tierRateAdapter.itemCount + 1
                rate = 3.0
            })
        }
    }

    override fun onStartSubscribe(disposable: CompositeDisposable) {
        super.onStartSubscribe(disposable)
        disposable.add(Observable
                .combineLatest(
                        inputName.textWatcher().mapToNotBlank(),
                        inputAmount.numberWatcher().mapToNotBlank(),
                        inputTime.textWatcher().mapToNotBlank(),
                        inputRate.textWatcher().mapToNotBlank(),
                        Function4<Boolean, Boolean, Boolean, Boolean, Boolean> { t1, t2, t3, t4 -> t1 && t2 && t3 && t4 }
                )
                .filter { buttonCreate.isEnabled != it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    buttonCreate.isEnabled = it
                }, {
                    Timber.e(it)
                })

        )
    }

    private fun Observable<String>.mapToNotBlank() = this.map { !it.isNullOrBlank() }
}