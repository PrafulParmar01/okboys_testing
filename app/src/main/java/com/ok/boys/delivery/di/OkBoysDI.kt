package com.ok.boys.delivery.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.ok.boys.delivery.application.OkBoys
import com.ok.boys.delivery.base.api.chat.ChatModule
import com.ok.boys.delivery.base.api.history.HistoryModule
import com.ok.boys.delivery.base.api.local.LocalModule
import com.ok.boys.delivery.base.api.login.LoginModule
import com.ok.boys.delivery.base.api.network.NetworkModule
import com.ok.boys.delivery.base.api.home.HomeModule
import com.ok.boys.delivery.base.api.image.ImageModule
import com.ok.boys.delivery.base.api.orders.OrdersModule
import com.ok.boys.delivery.base.viewmodelmodule.OkBoysViewModelProvider
import com.ok.boys.delivery.db.LoginInfoDao
import com.ok.boys.delivery.db.OkBoysDatabase
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: Application) {
    @Provides
    @Singleton
    fun provideApplication(): Application {
        return app
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return app
    }

    @Provides
    @Singleton
    fun provideOkBoysDatabase(context: Context): OkBoysDatabase {
        return Room.databaseBuilder(
            context,
            OkBoysDatabase::class.java, "okboys_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLoginInfoDao(db: OkBoysDatabase): LoginInfoDao {
        return db.loginInfoDao()
    }
}

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        HomeModule::class,
        LoginModule::class,
        OkBoysViewModelProvider::class,
        LocalModule::class,
        OrdersModule::class,
        ChatModule::class,
        HistoryModule::class,
        ImageModule::class,
    ]
)

public interface OkBoysAppComponent : BaseAppComponent {
    fun inject(app: OkBoys)
}