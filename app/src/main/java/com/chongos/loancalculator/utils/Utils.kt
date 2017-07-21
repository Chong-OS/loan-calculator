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

fun Number.toStringFormat() = amountFormatter.format(this)

internal fun <T> nameExtra(clazz: Class<T>, name: String): Lazy<String> =
        lazy { "${clazz.simpleName}_EXTRA_${name.toUpperCase()}" }

internal fun <T> nameState(clazz: Class<T>, name: String): Lazy<String> =
        lazy { "${clazz.simpleName}_STATE_${name.toUpperCase()}" }

internal fun <T> nameArg(clazz: Class<T>, name: String): Lazy<String> =
        lazy { "${clazz.simpleName}_ARG_${name.toUpperCase()}" }

fun Account.calculateInterest(rates: List<Rate>): Double {
    var tier = 1
    var rate = rates[0]
    var triggerYear = rate.months.div(12)
    var interest = 0.0
    var lastAmount = amount
    for(year in 0..years) {
        if(year > triggerYear) {
            rate = rates[tier]
            triggerYear += rate.months.div(12)
        }
        interest += lastAmount * rate.rate / 100
        lastAmount -= interest
    }

   return interest
}