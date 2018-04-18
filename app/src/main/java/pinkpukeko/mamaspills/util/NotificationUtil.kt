package pinkpukeko.mamaspills.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import pinkpukeko.mamaspills.AppConstants
import pinkpukeko.mamaspills.R
import pinkpukeko.mamaspills.TimerActivity
import pinkpukeko.mamaspills.TimerNotificationActionReceiver
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtil {
    companion object {
        private const val CHANNEL_ID_TIMER = "menu_timer"
//        private const val CHANNEL_NAME_TIMER = "Timer App Timer"
        private const val TIMER_ID = 0

        fun showTimerExpired(context: Context){
            val startIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            startIntent.action = AppConstants.ACTION_START
            val startPendingIntent = PendingIntent.getBroadcast(context,
                    0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val remaining = PrefUtil.getRemainingCount(context)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            nBuilder.setContentTitle("Time for a pill ! $remaining remaining")
//                    .setContentText("Start again?")
                    .setContentIntent(getPendingIntentWithStack(context, TimerActivity::class.java))
                    .addAction(R.drawable.ic_play, "Start", startPendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

            nManager.notify(TIMER_ID, nBuilder.build())
        }

        fun showTimerRunning(context: Context, wakeUpTime: Long){
            val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            stopIntent.action = AppConstants.ACTION_STOP
            val stopPendingIntent = PendingIntent.getBroadcast(context,
                    0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, false)
            nBuilder.setContentTitle("Timer is Running.")
                    .setContentText("End: ${df.format(Date(wakeUpTime))}")
                    .setContentIntent(getPendingIntentWithStack(context, TimerActivity::class.java))
                    .setOngoing(true)
                    .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

            nManager.notify(TIMER_ID, nBuilder.build())
        }

        fun hideTimerNotification(context: Context){
            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.cancel(TIMER_ID)
        }

        private fun getBasicNotificationBuilder(context: Context, channelId: String, playSound: Boolean)
                : NotificationCompat.Builder{
            val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val nBuilder = NotificationCompat.Builder(context, channelId)
                    .setStyle(MediaStyle())
                    .setSmallIcon(R.drawable.ic_timer)
                    .setAutoCancel(true)
                    .setDefaults(0)

            if (playSound) nBuilder.setSound(notificationSound)
            return nBuilder
        }

        private fun <T> getPendingIntentWithStack(context: Context, javaClass: Class<T>): PendingIntent{
            val resultIntent = Intent(context, javaClass)
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

//        @TargetApi(26)
//        private fun NotificationManager.createNotificationChannel(channelID: String,
//                                                                  channelName: String,
//                                                                  playSound: Boolean){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
//                else NotificationManager.IMPORTANCE_LOW
//                val nChannel = NotificationChannel(channelID, channelName, channelImportance)
//                nChannel.enableLights(true)
//                nChannel.lightColor = Color.BLUE
//                this.createNotificationChannel(nChannel)
//            }
//        }
    }
}