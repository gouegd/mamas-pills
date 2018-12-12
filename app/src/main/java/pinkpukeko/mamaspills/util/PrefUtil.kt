package pinkpukeko.mamaspills.util

import android.content.SharedPreferences
import pinkpukeko.mamaspills.TimerActivity

class PrefUtil {
    companion object {

        private const val PILLSTAKEN_STATE_ID = "pinkpukeko.mamaspills.pills_taken"

        fun getPillsTaken(preferences: SharedPreferences): List<Long> {
            val pills = preferences.getString(PILLSTAKEN_STATE_ID, "")
            val oneDayAgo = TimerActivity.nowSeconds - 24 * 60 * 60
            return pills.split(",").filter{ t -> t.isNotBlank() }.map { t -> t.toLong() }
            // keep only events of less than a day ago
                    //.filter{ t -> t > oneDayAgo }
        }

        fun setPillsTaken(pillsTaken: MutableList<Long>, preferences: SharedPreferences) {
            val editor = preferences.edit()
            editor.putString(PILLSTAKEN_STATE_ID, pillsTaken.joinToString(","))
            editor.apply()
        }

        private const val TIMER_STATE_ID = "pinkpukeko.mamaspills.timer_state"

        fun getTimerState(preferences: SharedPreferences): TimerActivity.TimerState{
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TimerActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: TimerActivity.TimerState, preferences: SharedPreferences){
            val editor = preferences.edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "pinkpukeko.mamaspills.backgrounded_time"

        fun getAlarmSetTime(preferences: SharedPreferences): Long =
            preferences.getLong(ALARM_SET_TIME_ID, 0)

        fun setAlarmSetTime(time: Long, preferences: SharedPreferences){
            val editor = preferences.edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }
}