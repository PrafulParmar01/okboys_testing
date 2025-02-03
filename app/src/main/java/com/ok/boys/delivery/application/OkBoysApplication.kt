package com.ok.boys.delivery.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDex
import com.ok.boys.delivery.BuildConfig
import com.ok.boys.delivery.base.api.orders.model.CategoryItem
import com.ok.boys.delivery.base.api.orders.model.JobAddressItem
import com.ok.boys.delivery.base.api.orders.model.UserAddressItem
import com.ok.boys.delivery.base.network.FileLoggingTree
import com.ok.boys.delivery.di.BaseAppComponent
import com.ok.boys.delivery.di.BaseUiApplication
import timber.log.Timber


open class OkBoysApplication : BaseUiApplication() {

    override fun onCreate() {
        super.onCreate()
        context = this
        MultiDex.install(this)
        setupLog()
    }


    private fun setupLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(FileLoggingTree(this))
        }
    }

    override fun getAppComponent(): BaseAppComponent {
        return component
    }

    override fun setAppComponent(baseAppComponent: BaseAppComponent) {
        component = baseAppComponent
    }

    companion object {
        lateinit var component: BaseAppComponent

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        var categoryList: List<CategoryItem> = listOf()
        var jobAddressItem: JobAddressItem? = null
        var jobAddressList: List<JobAddressItem> = listOf()
        var userAddressItem: UserAddressItem? = null
        //var orderResponse: OrderResponse? = null
        var isOrderRefreshed: Boolean = false
        var chatCounter  = 0
        var isBroadcastRegistered  = false

        fun getAppComponent(): BaseAppComponent {
            return component
        }
    }
}