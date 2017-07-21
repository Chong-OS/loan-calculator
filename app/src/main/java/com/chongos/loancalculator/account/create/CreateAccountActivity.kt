package com.chongos.loancalculator.account.create

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.chongos.loancalculator.App
import com.chongos.loancalculator.R
import com.chongos.loancalculator.R.id.*
import com.chongos.loancalculator.base.BaseActivity
import com.chongos.loancalculator.data.entity.Account
import com.chongos.loancalculator.data.entity.Rate
import com.chongos.loancalculator.utils.calculateInterest
import com.chongos.loancalculator.utils.view.numberWatcher
import com.chongos.loancalculator.utils.view.textWatcher
import com.chongos.loancalculator.utils.view.toViewVisible
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account_create.*
import kotlinx.android.synthetic.main.dialog_add_tier_rate.*
import kotlinx.android.synthetic.main.dialog_add_tier_rate.view.*
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
    var dialog: AlertDialog? = null
        set(value) {
            field?.cancel()
            field = value
        }
    var isSingleRate: Boolean = true
        private set(value) {
            field = value

            val singleRateVisibility = value.toViewVisible()
            val mortgageRateVisibility = value.not().toViewVisible()

            buttonSwitchRate.text = getString(if (value) R.string.button_switch_to_mortgage else R.string.button_switch_to_single)
            inputRate.visibility = singleRateVisibility
            inputRateHint.visibility = singleRateVisibility
            inputRateUnit.visibility = singleRateVisibility
            recyclerViewRate.visibility = mortgageRateVisibility
            recyclerViewRateHint.visibility = mortgageRateVisibility
            buttonAddRate.visibility = mortgageRateVisibility
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_create)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerViewRate.layoutManager = LinearLayoutManager(this)
        recyclerViewRate.adapter = tierRateAdapter

        // Create
        buttonCreate.setOnClickListener {
            activityDisposables.add(Single
                    .fromCallable {
                        val account = Account().apply {
                            name = inputName.text.toString()
                            amount = inputAmount.text.toString().let { if (it.contains(",")) it.replace(",".toRegex(), "") else it }.toDouble()
                            years = inputTime.text.toString().toInt()
                        }
                        account.id = App.database.account().insert(account)
                        account
                    }
                    .map {
                        val rates = mutableListOf<Rate>()
                        if (isSingleRate) {
                            rates.add(Rate().apply {
                                accountId = it.id
                                rate = inputRate.text.toString().toDouble()
                            })
                        } else {
                            rates.addAll(tierRateAdapter.list.mapIndexed { index, rate ->
                                rate.apply {
                                    accountId = it.id
                                    order = index
                                }
                            })
                        }
                        it.interest = it.calculateInterest(rates)
                        App.database.rate().inserts(rates)
                        App.database.account().update(it)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        finish()
                    }, {
                        Timber.e(it)
                    })
            )
        }

        // Switch rate mode
        buttonSwitchRate.setOnClickListener {
            if (!isSingleRate && tierRateAdapter.itemCount > 0) {
                AlertDialog.Builder(this)
                        .setMessage(R.string.alert_clear_adjust_rate_mortgage)
                        .setNegativeButton(R.string.button_cancel, { dialog, _ -> dialog.cancel() })
                        .setPositiveButton(R.string.button_clear, { dialog, _ ->
                            tierRateAdapter.clear()
                            isSingleRate = isSingleRate.not()
                            dialog.cancel()
                        })
                        .show()
                return@setOnClickListener
            }

            isSingleRate = isSingleRate.not()
        }


        // Add tier rate
        buttonAddRate.setOnClickListener {
            dialog = buildDialog(onSubmit = { tierRateAdapter.add(it) })
        }
        tierRateAdapter.onItemSelectListener = object : TierRateAdapter.OnItemSelectListener {
            override fun onSelect(rate: Rate, pos: Int) {
                rate.apply { order = pos }
                dialog = buildDialog(rate, { tierRateAdapter.update(pos, it) }, { tierRateAdapter.remove(pos) })
            }
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
        disposable.add(Observable
                .create<Int> {
                    val observer = object : RecyclerView.AdapterDataObserver() {
                        override fun onChanged() {
                            super.onChanged()
                            it.onNext(tierRateAdapter.itemCount)
                        }
                    }
                    tierRateAdapter.registerAdapterDataObserver(observer)
                    it.setCancellable { tierRateAdapter.unregisterAdapterDataObserver(observer) }
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    inputRate.setText(if (it > 0) tierRateAdapter.list[0].rate.toString() else null)
                })
        )
    }

    private fun Observable<String>.mapToNotBlank() = this.map { !it.isNullOrBlank() }

    private fun buildDialog(rate: Rate? = null, onSubmit: (rate: Rate) -> Unit = {}, onRemove: (rate: Rate) -> Unit = {}): AlertDialog {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tier_rate, null)
        return AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .apply {
                    if (rate != null) {
                        setNeutralButton(R.string.button_remove, { _, _ -> onRemove(rate) })
                        setPositiveButton(R.string.button_edit, { _, _ ->
                            onSubmit(rate.updateWithInput(
                                    dialogView.dialogAddTierRate_inputRate,
                                    dialogView.dialogAddTierRate_inputTime)
                            )
                        })
                    } else {
                        setPositiveButton(R.string.button_add, { _, _ ->
                            onSubmit(Rate()
                                    .apply { order = tierRateAdapter.itemCount }
                                    .updateWithInput(
                                            dialogView.dialogAddTierRate_inputRate,
                                            dialogView.dialogAddTierRate_inputTime)
                            )
                        })
                    }
                    setNegativeButton(R.string.button_cancel, { dialog, _ -> dialog.cancel() })
                }
                .show()
                .apply {
                    val buttonPositive = getButton(DialogInterface.BUTTON_POSITIVE)
                    buttonPositive.isEnabled = false
                    val disposable = Observable
                            .combineLatest(
                                    dialogView.dialogAddTierRate_inputRate.textWatcher().mapToNotBlank(),
                                    dialogView.dialogAddTierRate_inputTime.textWatcher().mapToNotBlank(),
                                    BiFunction<Boolean, Boolean, Boolean> { t1, t2 -> t1 && t2 })
                            .filter { buttonPositive.isEnabled != it }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ buttonPositive.isEnabled = it }, { Timber.e(it) })

                    dialogAddTierRate_textTierOrder?.apply {
                        text = getString(R.string.text_fmt_tier, (rate?.order ?: tierRateAdapter.itemCount).plus(1))
                    }
                    if (rate != null) {
                        dialogView.dialogAddTierRate_inputRate.setText("${rate.rate}")
                        dialogView.dialogAddTierRate_inputTime.setText("${rate.months / 12}")
                    }
                    setOnCancelListener { disposable.dispose() }
                    setOnDismissListener { disposable.dispose() }
                }
    }

    private fun Rate.updateWithInput(inputRate: EditText, inputTime: EditText) = apply {
        rate = inputRate.text.toString().toDouble()
        months = inputTime.text.toString().toInt() * 12
    }

}