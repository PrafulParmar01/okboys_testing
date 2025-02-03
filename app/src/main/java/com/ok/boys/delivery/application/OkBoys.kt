package com.ok.boys.delivery.application

import android.app.Activity
import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ok.boys.delivery.di.AppModule
import com.ok.boys.delivery.di.DaggerOkBoysAppComponent
import com.ok.boys.delivery.di.OkBoysAppComponent


class OkBoys : OkBoysApplication() {

    companion object {
        operator fun get(app: Application): OkBoys {
            return app as OkBoys
        }

        operator fun get(activity: Activity): OkBoys {
            return activity.application as OkBoys
        }

        lateinit var componentLegacy: OkBoysAppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        try {
            componentLegacy = DaggerOkBoysAppComponent.builder()
                .appModule(AppModule(this))
                .build()
            componentLegacy.inject(this)
            super.setAppComponent(componentLegacy)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}