package com.chongos.loancalculator.utils.view

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.Observable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * @author ChongOS
 * @since 20-Jul-2017
 */
fun EditText.textWatcher(): Observable<String> {
    return Observable.create<String> { e ->
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                e.onNext(s.toString())
            }
        }
        e.setCancellable { removeTextChangedListener(textWatcher) }
        addTextChangedListener(textWatcher)
    }
}

fun EditText.numberWatcher(): Observable<String> {
    return Observable.create<String> { e ->
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                removeTextChangedListener(this)
                val text = s.toString().let {
                    if (it.contains(",")) it.replace(",".toRegex(), "") else it
                }
                e.onNext(text)

                try {
                    val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                    formatter.applyPattern("#,###,###,###")
                    val formattedString = formatter.format(text.toLong())

                    setText(formattedString)
                    setSelection(this@numberWatcher.text.length)
                } catch (_: NumberFormatException) {

                } finally {
                    addTextChangedListener(this)
                }
            }
        }
        e.setCancellable { removeTextChangedListener(textWatcher) }
        addTextChangedListener(textWatcher)
    }
}