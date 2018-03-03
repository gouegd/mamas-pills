package pinkpukeko.mamaspills

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pinkpukeko.mamaspills.util.NotificationUtil
import pinkpukeko.mamaspills.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)

        PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}