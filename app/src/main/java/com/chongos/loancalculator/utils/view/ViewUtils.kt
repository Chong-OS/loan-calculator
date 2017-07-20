package com.chongos.loancalculator.utils.view

import android.view.View

/**
 * @author ChongOS
 * @since 20-Jul-2017
 */
fun Boolean.toViewVisible() = if (this) View.VISIBLE else View.GONE