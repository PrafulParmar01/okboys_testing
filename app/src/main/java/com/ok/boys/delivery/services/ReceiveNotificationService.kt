package com.ok.boys.delivery.services

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ok.boys.delivery.ui.orders.view.OrderRefreshEvent
import com.ok.boys.delivery.util.ApiConstant.ORDER_COMPLETED
import com.ok.boys.delivery.util.UtilsMethod
import org.greenrobot.eventbus.EventBus
import org.json.JSONException


class ReceiveNotificationService : FirebaseMessagingService() {


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.e("onNewToken", "===> $newToken")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var type = ""
        var refreshed = ""

        remoteMessage.data.isNotEmpty().let {
            Log.e("jsonObject", "Message data payload: " + remoteMessage.data)
            Log.e("jsonObject", "Message data Type: " + remoteMessage.data.get("type"))

            type = remoteMessage.data["type"].toString()
            val userId = remoteMessage.data["userId"].toString()
            val body = remoteMessage.data["body"].toString()

            if (remoteMessage.data["refreshed"] != null) {
                refreshed = remoteMessage.data["refreshed"].toString()
            }

            if (type == "COMMENT") {
                Log.e("Notification: ", "Comment received: " + remoteMessage.data)
                EventBus.getDefault().postSticky(ChatCountRefreshEvent(true))
            } else if (type == "EDIT-ORDER") {

                Log.e("jsonObject: ", "EDIT-ORDER TYPE===> " + type)
                EventBus.getDefault().postSticky(OrderRefreshEvent(true))
            } else {

            }
        }

        remoteMessage.notification?.let {
            Log.e("jsonObject", "Message Notification Body 1: ${it.body}")
            handleDataMessage(type, it.body.toString(), refreshed)
        }
    }


    private fun handleDataMessage(type: String, body: String, refreshed: String) {
        try {
            if (type == "EDIT-ORDER") {
                NotificationUtils(applicationContext).startChatNotificationEditOrder(body)
                Log.e("jsonObject", "NotificationUtils :")
            } else {
                Log.e("jsonObject: ", "1 TYPE===> " + type)
                if (!UtilsMethod.appInForeground(applicationContext)) {
                    Log.e("jsonObject: ", "2 FOREGROUND===> " + "false")
                    NotificationUtils(applicationContext).startChatNotification(body)
                } else {

                    if (refreshed != "" && refreshed == "true") {
                        Log.e(
                            "jsonObject: ",
                            "3 FOREGROUND===> " + "true" + "   refreshed ----" + refreshed
                        )
                        val intent = Intent("ORDER_REFRESH")
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                    } else {
                        Log.e(
                            "jsonObject: ",
                            "4 FOREGROUND===> " + "else" + "   refreshed ----" + refreshed
                        )
                        if (type == "EDIT-ORDER") {
                            NotificationUtils(applicationContext).startChatNotificationEditOrder(body)
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
