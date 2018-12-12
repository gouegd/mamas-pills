package pinkpukeko.mamaspills

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import pinkpukeko.mamaspills.util.NotificationUtil
import pinkpukeko.mamaspills.util.PrefUtil
import java.text.DateFormat
import java.util.*

class TimerActivity : AppCompatActivity() {

    companion object {
        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000

        const val ALARM_ACTION = "pinkpukeko.mamaspills.ALARM"

    }

    enum class TimerState {
        Stopped, Running
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            NotificationUtil.showTimerExpired(context)

            timerState = TimerState.Stopped
            rerender()

            // put the RingingActivity on the screen
            val intent = Intent(context, RingingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)

        }
    }

    private var timerState = TimerState.Stopped

    // Wrapper to the list of times pills were taken, handling preferences loading/saving
    class PillsTaken(private val prefs: SharedPreferences) {
        // list of POSIX second when a pill was taken
        private var pillTimes = PrefUtil.getPillsTaken(prefs).toMutableList()

        val size: Int get() = pillTimes.size

        fun clear() {
            pillTimes.clear()
            PrefUtil.setPillsTaken(pillTimes, prefs)
        }

        fun add(time: Long) {
            pillTimes.add(time)
            PrefUtil.setPillsTaken(pillTimes, prefs)
        }

        fun <R> mapIndexed(transform: (Int, Long) -> R) =
            pillTimes.mapIndexed(transform)

    }

    private val preferences: SharedPreferences get() = PreferenceManager.getDefaultSharedPreferences(this)
    private var pillsTaken: PillsTaken? = null

    private val secondsRemaining: Long = 5 // 5 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pillsTaken = PillsTaken(preferences)

        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "      Mama's pills butler"

        rerender()

        buttonPillTaken.setOnClickListener { _ ->
            // record pill
            pillsTaken?.add(TimerActivity.nowSeconds)
            // start timer
            setAlarm(this, secondsRemaining)
            // change UI
            rerender()
        }

        buttonEarlyPill.setOnClickListener { _ ->
            // record pill
            pillsTaken?.add(TimerActivity.nowSeconds)
            // replace or cancel ongoing timer
//            if (pillsTaken?.size ?: 0 < MAX_PILLS_PER_DAY) {
                // start new timer for next alarm
                setAlarm(this, secondsRemaining)
//            } else {
//                removeAlarm(this)
//            }
            // change UI
            rerender()
        }

        buttonPillClear.setOnClickListener { _ ->
            // clear the pills record
            pillsTaken?.clear()
            // stop any ongoing timer
            removeAlarm(this)
            NotificationUtil.hideTimerNotification(this)
            // change UI
            rerender()
        }

        // Make sure to receive our own broadcasts
        val filter = IntentFilter()
        filter.addAction(ALARM_ACTION)
        registerReceiver(broadCastReceiver, filter)
    }

    private fun setAlarm(context: Context, secondsRemaining: Long): Long {
        timerState = TimerState.Running
        val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(wakeUpTime, pendingIntent), pendingIntent)
        PrefUtil.setAlarmSetTime(nowSeconds, preferences)
        // Also show in notifications
        NotificationUtil.showTimerRunning(context, wakeUpTime)
        return wakeUpTime
    }

    private fun removeAlarm(context: Context) {
        timerState = TimerState.Stopped
        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        PrefUtil.setAlarmSetTime(0, preferences)
    }

    fun rerender() {
        // 1. show recap of pills taken so far
        renderPillsTaken(pillsTaken)
        when {
            // 2.a alarm ongoing ? show 'take early'
            timerState == TimerState.Running -> {
                buttonEarlyPill.visibility = View.VISIBLE
                buttonPillTaken.visibility = View.GONE
//                buttonPillClear.visibility = View.INVISIBLE
            }
            // 2.b or enough pills taken ? show 'clear'
//            pillsTaken?.size ?: 0 >= MAX_PILLS_PER_DAY -> {
//                buttonEarlyPill.visibility = View.INVISIBLE
//                buttonPillTaken.visibility = View.INVISIBLE
////                buttonPillClear.visibility = View.VISIBLE
//            }
            // 2.c otherwise show 'take Nth pill'
            else -> {
                buttonEarlyPill.visibility = View.GONE
                buttonPillTaken.visibility = View.VISIBLE
//                buttonPillClear.visibility = View.INVISIBLE
            }
        }

        if (pillsTaken?.size ?: 0 > 0) {
            buttonPillClear.visibility = View.VISIBLE
        } else {
            buttonPillClear.visibility = View.GONE
        }
    }

    private fun renderPillsTaken(pillsTaken: PillsTaken?) {
        val allTimes = pillsTaken?.mapIndexed { ix, time ->
            resources.getString(
                R.string.pillTakenTime,
                ix + 1,
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(time * 1000))
            )
        }
        pillsTakenTextArea.text = allTimes?.joinToString("\n")
    }

}