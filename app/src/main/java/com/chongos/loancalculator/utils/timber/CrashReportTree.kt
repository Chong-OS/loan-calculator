package com.chongos.loancalculator.utils.timber

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
class CrashReportTree : Timber.Tree() {

    companion object {
        private val CRASHLYTICS_KEY_PRIORITY by lazy { "priority" }
        private val CRASHLYTICS_KEY_TAG by lazy { "tag" }
        private val CRASHLYTICS_KEY_MESSAGE by lazy { "message" }
    }

    override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)
        Crashlytics.logException(t ?: Exception(message))
    }
}