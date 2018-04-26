package pinkpukeko.mamaspills

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pinkpukeko.mamaspills.util.NotificationUtil
import pinkpukeko.mamaspills.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val remaining = PrefUtil.getRemainingCount(context) - 1
        PrefUtil.setRemainingCount(remaining, context)

        NotificationUtil.showTimerExpired(context)

        if (remaining < 1) {
            PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
            PrefUtil.setAlarmSetTime(0, context)
        } else {
            val timerLength= PrefUtil.getTimerLength(context)
            TimerActivity.setAlarm(context, TimerActivity.nowSeconds, timerLength)
        }
    }
}