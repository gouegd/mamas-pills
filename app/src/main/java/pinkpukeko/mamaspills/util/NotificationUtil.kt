package pinkpukeko.mamaspills.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import pinkpukeko.mamaspills.R
import pinkpukeko.mamaspills.TimerActivity
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtil {
    companion object {
        private const val CHANNEL_ID = "puke?ko!"
        private const val NOTIFICATION_ID = 0

        private val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        private fun getNotificationManager(context: Context) : NotificationManager {
            return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }


        fun showTimerExpired(context: Context){
//            val startIntent = Intent(context, TimerNotificationActionReceiver::class.java)
//            startIntent.action = AppConstants.ACTION_START
//            val startPendingIntent = PendingIntent.getBroadcast(context,
//                    0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID)
            nBuilder.setContentTitle("It's time for a pill")
                    .setContentText("Let me know when you take it !")
                    .setContentIntent(getPendingIntentWithStack(context, TimerActivity::class.java))
                    .setOngoing(true)
//                    .addAction(R.drawable.ic_play, "Start", startPendingIntent)

            val nManager = getNotificationManager(context)
//            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

            nManager.notify(NOTIFICATION_ID , nBuilder.build())
        }

        fun showTimerRunning(context: Context, wakeUpTime: Long){
//            val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
//            stopIntent.action = AppConstants.ACTION_STOP
//            val stopPendingIntent = PendingIntent.getBroadcast(context,
//                    0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID, 0)
            nBuilder.setContentTitle("Next pill alarm at ${df.format(Date(wakeUpTime))}")
                    .setContentText("Let me know if you take it earlier :)")
                    .setContentIntent(getPendingIntentWithStack(context, TimerActivity::class.java))
                    .setOngoing(true)
//                    .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)

            val nManager = getNotificationManager(context)

            nManager.notify(NOTIFICATION_ID , nBuilder.build())
        }

        fun hideTimerNotification(context: Context){
            val nManager = getNotificationManager(context)
            nManager.cancel(NOTIFICATION_ID)
        }

        private fun getBasicNotificationBuilder(context: Context, channelId: String, defaults: Int = Notification.DEFAULT_ALL)
                : NotificationCompat.Builder{
            val nBuilder = NotificationCompat.Builder(context, channelId)
                    .setStyle(MediaStyle())
                    .setSmallIcon(R.drawable.ic_timer)
                    .setDefaults(defaults)
                    .setAutoCancel(false)

//            if (playSound) nBuilder.setSound(notificationSound)
            return nBuilder
        }

        private fun <T> getPendingIntentWithStack(context: Context, javaClass: Class<T>): PendingIntent{
            val resultIntent = Intent(context, javaClass)
            resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    }
}