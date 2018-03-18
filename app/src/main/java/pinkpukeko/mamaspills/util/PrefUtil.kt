package pinkpukeko.mamaspills.util

import android.content.Context
import android.preference.PreferenceManager
import pinkpukeko.mamaspills.TimerActivity

class PrefUtil {
    companion object {

        // in seconds
        fun getTimerLength(context: Context): Long{
            return 5
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "pinkpukeko.mamaspills.previous_timer_length_seconds"

        fun getPreviousTimerLengthSeconds(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }


        private const val TIMER_STATE_ID = "pinkpukeko.mamaspills.timer_state"

        fun getTimerState(context: Context): TimerActivity.TimerState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TimerActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: TimerActivity.TimerState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }


        private const val SECONDS_REMAINING_ID = "pinkpukeko.mamaspills.seconds_remaining"

        fun getSecondsRemaining(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }


        private const val ALARM_SET_TIME_ID = "pinkpukeko.mamaspills.backgrounded_time"

        fun getAlarmSetTime(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return  preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }

        private const val ALARM_REMAINING_COUNT_ID = "pinkpukeko.mamaspills.count_remaining"

        fun getRemainingCount(context: Context): Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(ALARM_REMAINING_COUNT_ID, 4)
        }

        fun setRemainingCount(count: Int, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(ALARM_REMAINING_COUNT_ID, count)
            editor.apply()
        }
    }
}