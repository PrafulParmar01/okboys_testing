package com.ok.boys.delivery.base.viewmodelmodule

import com.ok.boys.delivery.base.api.chat.ChatRepository
import com.ok.boys.delivery.base.api.history.HistoryRepository
import com.ok.boys.delivery.base.api.home.HomeRepository
import com.ok.boys.delivery.base.api.image.ImageRepository
import com.ok.boys.delivery.base.api.local.LocalRepository
import com.ok.boys.delivery.base.api.login.LoginRepository
import com.ok.boys.delivery.base.api.orders.OrdersRepository
import com.ok.boys.delivery.chunking.viewmodel.ImageViewModel
import com.ok.boys.delivery.ui.chat.viewmodel.ChatViewModel
import com.ok.boys.delivery.ui.dashboard.viewmodel.HomeViewModel
import com.ok.boys.delivery.ui.history.viewmodel.HistoryViewModel
import com.ok.boys.delivery.ui.login.viewmodel.LocalViewModel
import com.ok.boys.delivery.ui.login.viewmodel.LoginViewModel
import com.ok.boys.delivery.ui.orders.viewmodel.OrderViewModel
import dagger.Module
import dagger.Provides

@Module
class OkBoysViewModelProvider {

    @Provides
    fun provideHomeViewModel(
        homeRepository: HomeRepository
    ): HomeViewModel {
        return HomeViewModel(homeRepository)
    }


    @Provides
    fun provideOrderViewModel(
        ordersRepository: OrdersRepository
    ): OrderViewModel {
        return OrderViewModel(ordersRepository)
    }

    @Provides
    fun provideHistoryViewModel(
        historyRepository: HistoryRepository
    ): HistoryViewModel {
        return HistoryViewModel(historyRepository)
    }

    @Provides
    fun provideLoginViewModel(
        loginRepository: LoginRepository
    ): LoginViewModel {
        return LoginViewModel(loginRepository)
    }

    @Provides
    fun provideLocalViewModel(
        localRepository: LocalRepository
    ): LocalViewModel {
        return LocalViewModel(localRepository)
    }
    @Provides
    fun provideChatViewModel(
        chatRepository: ChatRepository
    ): ChatViewModel {
        return ChatViewModel(chatRepository)
    }

    @Provides
    fun provideImageViewModel(
        imageRepository: ImageRepository
    ): ImageViewModel {
        return ImageViewModel(imageRepository)
    }
}