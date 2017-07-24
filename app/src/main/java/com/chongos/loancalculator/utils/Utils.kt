package com.chongos.loancalculator.utils

import com.chongos.loancalculator.data.entity.Account
import com.chongos.loancalculator.data.entity.Rate
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
private val amountFormatter by lazy {
    (NumberFormat.getInstance(Locale.US) as DecimalFormat).apply {
        applyPattern("#,###,###,###")
    }
}

fun Number.toStringFormat() = amountFormatter.format(this) ?: ""

internal fun <T> nameExtra(clazz: Class<T>, name: String): Lazy<String> =
        lazy { "${clazz.simpleName}_EXTRA_${name.toUpperCase()}" }

internal fun <T> nameState(clazz: Class<T>, name: String): Lazy<String> =
        lazy { "${clazz.simpleName}_STATE_${name.toUpperCase()}" }

internal fun <T> nameArg(clazz: Class<T>, name: String): Lazy<String> =
        lazy { "${clazz.simpleName}_ARG_${name.toUpperCase()}" }

fun Account.calculateInterest(rates: List<Rate>): Double {
    var tier = 0
    var monthRateChanged = rates[0].months
    var totalInterest = 0.0
    var lastAmount = amount

    val payment = let {
        var sumMonth = 0
        var weight = 0.0
        for (r in rates) {
            weight += r.rate * r.months
            sumMonth += r.months
        }
        pmt(weight / sumMonth / 12, sumMonth, amount)
    }

    for (month in 0..years * 12) {
        if (lastAmount <= 0) {
            break
        }

        if (month > monthRateChanged) {
            tier++
            monthRateChanged += rates[tier].months
        }
        val interest = lastAmount * rates[tier].rate / 12 / 100
        lastAmount -= (payment - interest)
        totalInterest += interest
    }

    return totalInterest
}

// 11,199
// 2,011,490
fun pmt(rate: Double, timePeriod: Int, presentValue: Double) =
        (presentValue * rate / 100) / (1 - Math.pow(1 + (rate / 100), -timePeriod.toDouble()))