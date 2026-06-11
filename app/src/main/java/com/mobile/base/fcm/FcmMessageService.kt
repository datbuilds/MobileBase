package com.mobile.base.fcm

//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.media.AudioAttributes
//import android.net.Uri
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import com.google.firebase.messaging.FirebaseMessaging
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import org.koin.android.ext.android.inject
//import timber.log.Timber
//import vn.salon.manager.R
//import vn.salon.manager.SalonApplication
//import vn.salon.manager.activity.login.LoginActivity
//import vn.salon.manager.core.security.encrypt.LocalStorage
//import vn.salon.manager.utils.extensions.logD
//import kotlin.coroutines.CoroutineContext
//
//class FcmMessageService : FirebaseMessagingService(), CoroutineScope {
//
//    private var coroutineJob: Job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.IO + coroutineJob
//
//    companion object {
//        const val NOTIFICATION_ID = 1601
//
//        private const val NOTIFICATION_ACTION = "action"
//    }
//
//    private val storage: LocalStorage by inject()
//
//    init {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                task.result?.let { token ->
//                    Timber.d("FcmMessageService", "FirebaseToken: $token")
//                    if (!token.contentEquals(storage.getFcmToken())) {
//                        storage.saveFcmToken(token)
//
//                        logD("FirebaseMessaging ==> $token")
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onNewToken(token: String) {
//        Timber.d("FcmMessageService", "NewToken -->", token)
//        sendRegistrationToServer(token)
//    }
//
//    private fun sendRegistrationToServer(token: String) {
//        storage.saveFcmToken(token)
//
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//        Timber.d("FcmMessageService", "MessageId= ${remoteMessage.messageId}")
//        Timber.d("FcmMessageService", "Message= ${remoteMessage.notification}")
//        if (remoteMessage.data.isNotEmpty()) {
//            Timber.d("Message data payload: ${remoteMessage.notification}")
//        }
//
//        val isForeground = (this.applicationContext as? SalonApplication)?.isForeground() ?: false
//        if (isForeground) {
//            sendNotification(remoteMessage.notification)
//        } else {
//            // scheduled room calling
//            val action = remoteMessage.data[NOTIFICATION_ACTION]
//            Timber.d("action: $action")
//            // sendNotification(remoteMessage.data)
//        }
//    }
//
//    private fun sendNotification(messageBody: RemoteMessage.Notification?) {
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val titleNotification = messageBody?.title ?: getString(R.string.app_name)
//        val subtitleNotification = messageBody?.body
//
//        val pathAudio = "android.resource://" + applicationContext.packageName + "/raw/notification"
//        val defaultSoundUri = Uri.parse(pathAudio)
//        val pattern = longArrayOf(0, 500, 1000)
//
//        val channelId = getString(R.string.notification_channel_id)
//        val channelName = getString(R.string.notification_channel_name)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_HIGH
//            )
//
//            val audioAttributes = AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .build()
//
//            channel.setShowBadge(true)
//            channel.enableLights(true)
//            channel.enableVibration(true)
//            channel.setSound(defaultSoundUri, audioAttributes)
//            channel.vibrationPattern = pattern
//            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val contentIntent = Intent(this, LoginActivity::class.java)
//        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//        val pendingIntentFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//
//        val pendingIntent = PendingIntent.getActivity(this, 0, contentIntent, pendingIntentFlags)
//
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.icon_noti)
//            .setContentTitle(titleNotification)
//            .setContentText(subtitleNotification)
//            .setStyle(
//                NotificationCompat.BigTextStyle()
//                    .bigText(subtitleNotification)
//            )
//            .setVibrate(pattern)
//            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
//            .setSound(defaultSoundUri)
//            .setDefaults(Notification.DEFAULT_SOUND)
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//
//        val isForeground = (this.applicationContext as? SalonApplication)?.isForeground() ?: false
//        if (!isForeground) {
//            notificationBuilder.setContentIntent(pendingIntent)
//        }
//
//        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
//    }
//
//}