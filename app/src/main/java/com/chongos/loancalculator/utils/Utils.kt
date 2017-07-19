package com.chongos.loancalculator.utils

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
internal fun <T> nameExtra(clazz: Class<T>, name: String): Lazy<String> =
        lazy { "${clazz.simpleName}_EXTRA_${name.toUpperCase()}" }

internal fun <T> nameState(clazz: Class<T>, name: String): Lazy<String> =
        lazy { "${clazz.simpleName}_STATE_${name.toUpperCase()}" }

internal fun <T> nameArg(clazz: Class<T>, name: String): Lazy<String> =
        lazy { "${clazz.simpleName}_ARG_${name.toUpperCase()}" }

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)