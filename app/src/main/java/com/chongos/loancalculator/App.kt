package com.chongos.loancalculator

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.chongos.loancalculator.data.AppDatabase
import com.chongos.loancalculator.utils.timber.CrashReportTree
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
open class App: Application() {

    companion object {
        private var DB_INSTANCE: AppDatabase? = null
        val database: AppDatabase by lazy { DB_INSTANCE!! }

        private fun initialDatabase(context: Context) {
            DB_INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "load-calculator-db"
            ).build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        initialDatabase(this)
        initialFabric()
        initialTimber()
    }

    protected open fun initialFabric() {
        Fabric.with(this, Crashlytics())
    }

    protected open fun initialTimber() {
        Timber.plant(CrashReportTree())
    }
}