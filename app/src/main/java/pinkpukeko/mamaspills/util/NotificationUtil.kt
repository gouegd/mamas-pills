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
        private const val CHANNEL_ID_TIMER_ON = "timer_on"
        private const val CHANNEL_ID_TIMER_TRIGGERED = "timer_triggered"
        private const val TIMER_ON_ID = 0
        private const val TIMER_TRIGGERED_ID = 1

        fun showTimerExpired(context: Context){
//            val startIntent = Intent(context, TimerNotificationActionReceiver::class.java)
//            startIntent.action = AppConstants.ACTION_START
//            val startPendingIntent = PendingIntent.getBroadcast(context,
//                    0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val remaining = PrefUtil.getRemainingCount(context)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER_TRIGGERED, true)
            nBuilder.setContentTitle("Time for a pill ! $remaining more after this one")
                    .setContentIntent(getPendingIntentWithStack(context, TimerActivity::class.java))
//                    .addAction(R.drawable.ic_play, "Start", startPendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

            nManager.notify(TIMER_TRIGGERED_ID, nBuilder.build())
        }

        fun showTimerRunning(context: Context, wakeUpTime: Long){
//            val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
//            stopIntent.action = AppConstants.ACTION_STOP
//            val stopPendingIntent = PendingIntent.getBroadcast(context,
//                    0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
            val pillCount = 1 + PrefUtil.getTotalAlarmCount() - PrefUtil.getRemainingCount(context)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER_ON, false)
            nBuilder.setContentTitle("Next pill alarm at ${df.format(Date(wakeUpTime))}")
                    .setContentText("This will be the pill number $pillCount today")
                    .setContentIntent(getPendingIntentWithStack(context, TimerActivity::class.java))
                    .setOngoing(true)
//                    .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            nManager.notify(TIMER_ON_ID, nBuilder.build())
        }

        fun hideTimerNotification(context: Context){
            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.cancel(TIMER_TRIGGERED_ID)
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

    }
}