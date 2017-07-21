package com.chongos.loancalculator.calculate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import com.chongos.loancalculator.App
import com.chongos.loancalculator.R
import com.chongos.loancalculator.base.BaseActivity
import com.chongos.loancalculator.data.entity.Account
import com.chongos.loancalculator.data.entity.Rate
import com.chongos.loancalculator.utils.calculateInterest
import com.chongos.loancalculator.utils.toStringFormat
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_calculate.*
import timber.log.Timber

/**
 * @author ChongOS
 * @since 21-Jul-2017
 */
class CalculateActivity : BaseActivity() {

    companion object {

        private val EXTRA_ACCOUNT_ID = "EXTRA_ACCOUNT_ID"
        private val STATE_ACCOUNT_ID = "STATE_ACCOUNT_ID"

        fun getCallingIntent(context: Context, account: Account) = Intent(context, CalculateActivity::class.java)
                .apply {
                    putExtra(EXTRA_ACCOUNT_ID, account.id)
                }
    }

    var accountId: Long = -1
    var accountWithRates: AccountWithRates? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate)
        accountId = savedInstanceState?.getLong(STATE_ACCOUNT_ID) ?: intent.getLongExtra(EXTRA_ACCOUNT_ID, -1)

        activityDisposables.add(
                Flowable
                        .combineLatest(
                                App.database.account().load(accountId),
                                App.database.rate().loadAll(accountId),
                                BiFunction<Account, List<Rate>, AccountWithRates> { account, rates ->
                                    AccountWithRates(account, rates)
                                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            accountWithRates = it
                            textAmount.text = it.account.amount.toStringFormat()
                            textInterest.text = it.account.interest.toStringFormat()
                            textTotal.text = it.account.amount.plus(it.account.interest).toStringFormat()
                        }, {
                            Timber.e(it)
                        })
        )

        seekBarTenor.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val amount = accountWithRates?.account?.amount
                val interest = accountWithRates?.account?.calculateInterest(accountWithRates?.rates!!)
                    textAmount.text = amount?.toStringFormat()
                    textInterest.text = interest?.toStringFormat()
                    textTotal.text = amount?.plus(interest!!)?.toStringFormat()
            }
        })
    }

    class AccountWithRates(val account: Account, val rates: List<Rate>)

}