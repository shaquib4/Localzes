package com.localzes.android

import android.R.string
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
import android.text.TextUtils.split
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class MyFirebaseMessaging : FirebaseMessagingService() {
    private val NOTIFICATION_CHANNEL_ID = "com.example.localzes.test"
    private lateinit var currentAuth: FirebaseAuth
    private lateinit var intent: Intent
    private lateinit var firebaseUser: FirebaseUser
    override fun onMessageReceived(@NonNull remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        //all notifications will be received here

        currentAuth = FirebaseAuth.getInstance()
        firebaseUser = currentAuth.currentUser!!

        val notificationType = remoteMessage.data["notificationType"]
        if (notificationType.equals("New Order")) {
            val buyerUid = remoteMessage.data["buyerId"]
            val sellerUid = remoteMessage.data["sellerUid"]
            val orderId = remoteMessage.data["orderId"]
            /*val listOfIds = remoteMessage.data["ListOfIds"]*/
            val notificationTitle = remoteMessage.data["notificationTitle"]
            val notificationDescription = remoteMessage.data["notificationMessage"]
            /*val string: List<String> = listOfIds!!.split(",")*/
            val string= arrayOf(sellerUid.toString(),"o1oWEVyiVMNYgsDYVuEo73Wp47f2")
            if (firebaseUser != null) {
                /*for (i in string) {
                    if (currentAuth!!.uid == i) {
                        showNotification(
                            orderId.toString(),
                            i,
                            buyerUid.toString(),
                            notificationTitle.toString(),
                            notificationDescription.toString(),
                            notificationType.toString()
                        )
                    }
                }*/
                for (i in string) {
                    showNotification(
                        orderId.toString(),
                        i,
                        buyerUid.toString(),
                        notificationTitle.toString(),
                        notificationDescription.toString(),
                        notificationType.toString()
                    )
                }
            }
        }
        if (notificationType.equals("OrderStatusChanged")) {
            val buyerUid = remoteMessage.data["buyerId"]
            val sellerUid = remoteMessage.data["sellerUid"]
            val orderId = remoteMessage.data["orderId"]
            val totalCost = remoteMessage.data["totalAmount"]
            val notificationTitle = remoteMessage.data["notificationTitle"]
            val notificationDescription = remoteMessage.data["notificationMessage"]
            if (firebaseUser != null && currentAuth!!.uid == buyerUid.toString()) {
                showNotification1(
                    orderId.toString(),
                    sellerUid.toString(),
                    buyerUid.toString(),
                    notificationTitle.toString(),
                    notificationDescription.toString(),
                    notificationType.toString(),
                    totalCost.toString()
                )
            }
        }
        if (notificationType.equals("New Order List")) {
            val buyerUid = remoteMessage.data["buyerId"]
            val sellerUid = remoteMessage.data["sellerUid"]
            val orderId = remoteMessage.data["orderId"]
            val notificationTitle = remoteMessage.data["notificationTitle"]
            val notificationDescription = remoteMessage.data["notificationMessage"]
            if (firebaseUser != null && currentAuth!!.uid == sellerUid.toString()) {
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
        if (notificationType.equals("PaymentMethod")) {
            val buyerUid = remoteMessage.data["buyerId"]
            val sellerUid = remoteMessage.data["sellerUid"]
            val orderId = remoteMessage.data["orderId"]
            val notificationTitle = remoteMessage.data["notificationTitle"]
            val notificationDescription = remoteMessage.data["notificationMessage"]
            if (firebaseUser != null && currentAuth!!.uid == sellerUid.toString()) {
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
        if (notificationType.equals("OrderListStatusChanged")) {
            val buyerUid = remoteMessage.data["buyerId"]
            val sellerUid = remoteMessage.data["sellerUid"]
            val orderId = remoteMessage.data["orderId"]
            val notificationTitle = remoteMessage.data["notificationTitle"]
            val notificationDescription = remoteMessage.data["notificationMessage"]
            val totalCost = remoteMessage.data["totalCost"]
            if (firebaseUser != null && currentAuth!!.uid == buyerUid.toString()) {
                showNotification1(
                    orderId.toString(),
                    sellerUid.toString(),
                    buyerUid.toString(),
                    notificationTitle.toString(),
                    notificationDescription.toString(),
                    notificationType.toString(),
                    totalCost.toString()
                )
            }
        }
    }

    private fun showNotification1(
        orderId: String,
        sellerUid: String,
        buyerId: String,
        notificationTitle: String,
        notificationDescription: String,
        notificationType: String,
        totalCost: String
    ) {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt(3000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpNotificationChannel(notificationManager)
        }
        if (notificationType == "OrderListStatusChanged") {
            intent = Intent(this, UserListOrderDetails::class.java)
            intent.putExtra("orderId", orderId)
            intent.putExtra("orderTo", sellerUid)
            intent.putExtra("totalCost", totalCost)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        }
        if (notificationType == "OrderStatusChanged") {
            intent = Intent(this, OrdersDetailsUserActivity::class.java)
            intent.putExtra("orderId", orderId)
            intent.putExtra("orderTo", sellerUid)
            intent.putExtra("totalAmount", totalCost)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val largeIcon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.localze_shop)
        val notificationSoundUri: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.ic_localze)
            .setLargeIcon(largeIcon)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setSound(notificationSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationDescription))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpNotificationChannel(notificationManager)

        }
        when (notificationType) {
            "New Order" -> {
                intent = Intent(this, OrdersDetailsSellerActivity::class.java)
                intent.putExtra("orderIdTv", orderId)
                intent.putExtra("orderByTv", buyerId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            "New Order List" -> {
                intent = Intent(this, ListOrderDetailSeller::class.java)
                intent.putExtra("orderId", orderId)
                intent.putExtra("orderBy", buyerId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            "PaymentMethod" -> {
                intent = Intent(this, ListOrderDetailSeller::class.java)
                intent.putExtra("orderId", orderId)
                intent.putExtra("orderBy", buyerId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val largeIcon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.localze_shop)
        val notificationSoundUri: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.ic_localze)
            .setLargeIcon(largeIcon)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationDescription))
            .setSound(notificationSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpNotificationChannel(notificationManager: NotificationManager) {
        val channelName: CharSequence = "Some Sample Text"
        val channelDescription: String = "Channel Description Here"

        val notificationChannel: NotificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.description = channelDescription
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = R.color.red
        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}