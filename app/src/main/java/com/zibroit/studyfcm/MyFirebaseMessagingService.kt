package com.zibroit.studyfcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    /*토큰이 갱신 될때 마다 토큰 반환*/
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remotemessage: RemoteMessage) {
        super.onMessageReceived(remotemessage)

        createNotificationChannel()

        val type = remotemessage.data["type"]
            ?.let{ NotificationType.valueOf(it) }
        //.let : 지정된 값이 null이 아닌 경우에 코드를 실행해야 하는 경우.
        val title = remotemessage.data["title"]
        val message = remotemessage.data["message"]
        type ?: return

        NotificationManagerCompat.from(this)
            .notify(type.id,createNotification(type,title,message))
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun createNotification(type: NotificationType,title:String?, message:String?): Notification {
        val intent = Intent(this,MainActivity::class.java).apply {
            putExtra("notificationType","${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        /*pendingIntent : 인텐트를 가지고 있는 클래스로, 기본 목적은 다른 애플리케이션의 권한을 허가해 갖고있는 Intent를 마치 본인 앱의 프로세스에서 실행하는 것처럼 사용하게 하는 것*/
        val pendingIntent = PendingIntent.getActivity(this,type.id,intent,FLAG_UPDATE_CURRENT)

        val notificationBuilder =  NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        when(type){
            /*기본형*/
            NotificationType.NORMAL -> Unit
            /*확장형*/
            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("확장형 Notification"
                        +"FCM"
                        +"Notification")
                )
            }
            /*커스텀*/
            NotificationType.CUSTOM -> {
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(packageName, R.layout.view_custom_notification)
                            .apply { // 수신 객체 람다 내부에서 수신 객체의 함수를 사용하지 않고 수신 객체 자신을 다시 반환하려는 경우에 사용. 주로 객체 초기화
                                setTextViewText(R.id.title,title)
                                setTextViewText(R.id.message,message)
                            })
            }
        }
        return notificationBuilder.build()
    }

    companion object{
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_DESCRIPTION = " Emoji Party를 위한 채널"
        private const val CHANNEL_ID = "Channel Id"
    }
}