package com.baghdadhomes.Models

data class NotificationsModel(
    var notificationData:ArrayList<NotificationData>? = null

)
data class NotificationData(
    var title: String? = null,
    var body: String? = null,
)
