package com.example.localzes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessaging : FirebaseMessagingService() {
    private val NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID"
    private lateinit var currentAuth: FirebaseAuth
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        //all notifications will be received here

        currentAuth = FirebaseAuth.getInstance()
        val current = currentAuth.currentUser

        val notificationType = remoteMessage.data["notificationType"]
        if (notificationType.equals("New Order")) {
            val buyerUid = remoteMessage.data["buyerId"]
            val sellerUid = remoteMessage.data["sellerUid"]
            val orderId = remoteMessage.data["orderId"]
            val notificationTitle = remoteMessage.data["notificationTitle"]
            val notificationDescription = remoteMessage.data["notificationMessage"]
            if (current != null && current!!.uid == sellerUid.toString()) {
                showNotification(
                    orderId.toString(),
                    sellerUid.toString(),
                    buyerUid.toString(),
                    notificationTitle.toString(),
                    notificationDescription.toString(),
                    notificationType.toString()
                )
            }
        }
        if (notificationType.equals("OrderStatusChanged")) {
            val buyerUid = remoteMessage.data["buyerId"]
            val sellerUid = remoteMessage.data["sellerUid"]
            val orderId = remoteMessage.data["orderId"]
            val notificationTitle = remoteMessage.data["notificationTitle"]
            val notificationDescription = remoteMessage.data["notificationMessage"]
            if (current != null && current!!.uid == buyerUid) {
                showNotification(
                    orderId.toString(),
                    sellerUid.toString(),
                    buyerUid.toString(),
                    notificationTitle.toString(),
                    notificationDescription.toString(),
                    notificationType.toString()
                )
            }
        }
    }

    private fun showNotification(
        orderId: String,
        sellerUid: String,
        buyerId: String,
        notificationTitle: String,
        notificationDescription: String,
        notificationType: String
    ) {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId: Int = Random.nextInt(3000)
        if (Build.VERSION.SDK_INT >= 26) {
            setUpNotificationChannel(notificationManager)

        }
        var intent: Intent? =null
        if(notificationType == "New Order"){
            intent=Intent(this,OrdersDetailsSellerActivity::class.java)
            intent.putExtra("orderIdTv",orderId)
            intent.putExtra("orderByTv",sellerUid)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        else if(notificationType=="OrderStatusChanged"){
            intent=Intent(this,OrdersDetailsUserActivity::class.java)
            intent.putExtra("orderId",orderId)
            intent.putExtra("orderTo",buyerId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent:PendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val largeIcon:Bitmap=BitmapFactory.decodeResource(resources,R.drawable.ic_localze)
        val notificationSoundUri: Uri =RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder=NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.ic_localze)
            .setLargeIcon(largeIcon)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setSound(notificationSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId,notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpNotificationChannel(notificationManager: NotificationManager) {
        val channelName:CharSequence="Some Sample Text"
        val channelDescription:String="Channel Description Here"

        val notificationChannel:NotificationChannel=NotificationChannel(NOTIFICATION_CHANNEL_ID,channelName,NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.description=channelDescription
        notificationChannel.enableLights(true)
        notificationChannel.lightColor=R.color.red
        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}