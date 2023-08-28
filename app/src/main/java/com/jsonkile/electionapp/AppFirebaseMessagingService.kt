package com.jsonkile.electionapp

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppFirebaseMessagingService : FirebaseMessagingService() {
    @SuppressLint("MissingPermission")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.e("Message from FCM", message.notification?.body.orEmpty())

        val builder = NotificationCompat.Builder(this, "fcm_channel_id")
            .setSmallIcon(R.drawable.round_how_to_vote_24)
            .setContentTitle(message.notification?.title.orEmpty())
            .setContentText(message.notification?.body.orEmpty())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify((30000000..60000000).random(), builder.build())
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}