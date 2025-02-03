package com.ok.boys.delivery.ui.chat.view

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ChatNotificationData {
    @SerializedName("notification_type")
    @Expose
    var notificationType: String? = null

    @SerializedName("notification_message")
    @Expose
    var notificationMessage: String? = null

    @SerializedName("message_id")
    @Expose
    var messageId: String? = null

    @SerializedName("sender_id")
    @Expose
    var sender_id: String? = null

    @SerializedName("messageType")
    @Expose
    var messageType: String? = null

    @SerializedName("receiver_id")
    @Expose
    var receiverId: String? = null

    @SerializedName("otherUserFirstName")
    @Expose
    var otherUserFirstName: String? = null

    @SerializedName("otherUserProfilePic")
    @Expose
    var otherUserProfilePic: String? = null

    @SerializedName("conversionId")
    @Expose
    var conversionId: String? = null

    @SerializedName("createdDate")
    @Expose
    var createdDate: String? = null
}