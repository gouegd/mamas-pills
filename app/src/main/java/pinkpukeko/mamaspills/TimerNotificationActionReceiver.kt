package pinkpukeko.mamaspills

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pinkpukeko.mamaspills.util.NotificationUtil
import pinkpukeko.mamaspills.util.PrefUtil

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){
            AppConstants.ACTION_STOP -> {
                TimerActivity.removeAlarm(context)
                PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
                NotificationUtil.hideTimerNotification(context)
            }
            AppConstants.ACTION_START -> {
                val secondsRemaining = PrefUtil.getTimerLength(context)
                val wakeUpTime = TimerActivity.setAlarm(context, TimerActivity.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(TimerActivity.TimerState.Running, context)
                PrefUtil.setSecondsRemaining(secondsRemaining, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}