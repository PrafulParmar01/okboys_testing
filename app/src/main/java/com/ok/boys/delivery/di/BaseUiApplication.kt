package com.ok.boys.delivery.di

import android.app.Application
import com.ok.boys.delivery.services.LocationsService
import com.ok.boys.delivery.ui.chat.view.ChatActivity
import com.ok.boys.delivery.ui.chat.view.PreviewActivity
import com.ok.boys.delivery.ui.dashboard.HomeActivity
import com.ok.boys.delivery.ui.dashboard.view.HomeFragment
import com.ok.boys.delivery.ui.history.view.HistoryFragment
import com.ok.boys.delivery.ui.login.OTPActivity
import com.ok.boys.delivery.ui.login.WelcomeActivity
import com.ok.boys.delivery.ui.orders.view.OrdersFragment
import com.ok.boys.delivery.ui.orders.view.payment.view.PaymentRequestActivity
import com.ok.boys.delivery.ui.payment.PaymentActivity
import com.ok.boys.delivery.ui.profile.ProfileActivity
import com.ok.boys.delivery.ui.tracking.DeliverTrackingActivity
import com.ok.boys.delivery.ui.tracking.TrackingActivity

/**
 * This base app class will be extended by either Main or Demo project.
 * It then will provide library project app component accordingly.
 */
abstract class BaseUiApplication : Application() {
    abstract fun getAppComponent(): BaseAppComponent
    abstract fun setAppComponent(baseAppComponent: BaseAppComponent)
}

/**
 * Base app component
 * This class should have all the inject targets classes
 */
interface BaseAppComponent {
    fun inject(app: Application)
    fun inject(homeActivity: HomeActivity)
    fun inject(welcomeActivity: WelcomeActivity)
    fun inject(otpActivity: OTPActivity)
    fun inject(trackingActivity: TrackingActivity)
    fun inject(chatActivity: ChatActivity)
    fun inject(previewActivity: PreviewActivity)
    fun inject(profileActivity: ProfileActivity) {
    }

    fun inject(homeFragment: HomeFragment)
    fun inject(ordersFragment: OrdersFragment)
    fun inject(deliverTrackingActivity: DeliverTrackingActivity)
    fun inject(paymentRequestActivity: PaymentRequestActivity)
    fun inject(historyFragment: HistoryFragment)
    fun inject(paymentActivity: PaymentActivity)
}

/**
 * Extension for getting component more easily
 */
fun BaseUiApplication.getComponent(): BaseAppComponent {
    return this.getAppComponent()
}
