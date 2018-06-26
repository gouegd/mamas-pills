package pinkpukeko.mamaspills

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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

        const val PILLS_PER_DAY = 4
        const val ALARM_ACTION = "pinkpukeko.mamaspills.ALARM"

    }

    enum class TimerState {
        Stopped, Running
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            NotificationUtil.showTimerExpired(context)

            timerState = TimerState.Stopped
            rerender()

//            when (intent?.action) {
//                BROADCAST_DEFAULT_ALBUM_CHANGED -> handleAlbumChanged()
//                BROADCAST_CHANGE_TYPE_CHANGED -> handleChangeTypeChanged()
//            }
        }
    }

    private var timerLengthSeconds: Long = 10
    private var timerState = TimerState.Stopped
    // list of POSIX second when a pill was taken
    private var pillsTaken: MutableList<Long> = mutableListOf()

    private var secondsRemaining: Long = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "      Mama's pills reminder"

        // recovering the instance state
//              timerState = TimerState.valueOf(savedInstanceState.getString(timerStateKey))
        pillsTaken = PrefUtil.getPillsTaken(this).toMutableList()

        rerender()

        buttonPillTaken.setOnClickListener { _ ->
            // record pill
            pillsTaken.add(TimerActivity.nowSeconds)
            // TODO supercharge add method to always do this?
            PrefUtil.setPillsTaken(pillsTaken, this)
            // start timer
            setAlarm(this, secondsRemaining)
            // change UI
            rerender()
        }

        buttonEarlyPill.setOnClickListener { _ ->
            // record pill
            pillsTaken.add(TimerActivity.nowSeconds)
            // TODO supercharge add method to always do this?
            PrefUtil.setPillsTaken(pillsTaken, this)
            // stop ongoing timer
            removeAlarm(this)
            if (pillsTaken.size < PILLS_PER_DAY) {
                // start new timer for next alarm
                setAlarm(this, secondsRemaining)
            }
            // change UI
            rerender()
        }

        buttonPillClear.setOnClickListener { _ ->
            // record pill
            pillsTaken.clear()
            // TODO supercharge add method to always do this?
            PrefUtil.setPillsTaken(pillsTaken, this)
            // stop ongoing timer
            removeAlarm(this)
            // change UI
            rerender()
        }

        val filter = IntentFilter()
        filter.addAction(ALARM_ACTION)
        registerReceiver(broadCastReceiver, filter)
    }

//    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
//        super.onSaveInstanceState(outState, outPersistentState)
//
//        outState?.run {
//            putString(timerStateKey, timerState.toString())
//            putLongArray(pillsTakenKey, pillsTaken.toLongArray())
//        }
//
//    }

    override fun onResume() {
        super.onResume()

//        initTimer()

        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
//        PrefUtil.setTimerState(timerState, this)
    }

    fun setAlarm(context: Context, secondsRemaining: Long): Long {
        timerState = TimerState.Running
        val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(wakeUpTime, pendingIntent), pendingIntent)
        PrefUtil.setAlarmSetTime(nowSeconds, context)
        // Also show in notifications
        NotificationUtil.showTimerRunning(context, wakeUpTime)
        return wakeUpTime
    }

    fun removeAlarm(context: Context) {
        timerState = TimerState.Stopped
        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        PrefUtil.setAlarmSetTime(0, context)
    }

    fun rerender() {
        // 1. show recap of pills taken so far
        renderPillsTaken(pillsTaken)
        // 2.a alarm ongoing ? show 'take early'
        if (timerState == TimerState.Running) {
            buttonEarlyPill.visibility = View.VISIBLE
            buttonPillTaken.visibility = View.INVISIBLE
            buttonPillClear.visibility = View.INVISIBLE
        }
        // 2.b or enough pills taken ? show 'clear'
        else if(pillsTaken.size >= PILLS_PER_DAY) {
            buttonEarlyPill.visibility = View.INVISIBLE
            buttonPillTaken.visibility = View.INVISIBLE
            buttonPillClear.visibility = View.VISIBLE
        }
        // 2.c otherwise show 'take Nth pill'
        else {
            buttonEarlyPill.visibility = View.INVISIBLE
            buttonPillTaken.visibility = View.VISIBLE
            buttonPillClear.visibility = View.INVISIBLE
        }
    }

    private fun renderPillsTaken(pillsTaken: List<Long>) {
        val allTimes = pillsTaken.mapIndexed { ix, time ->
            resources.getString(
                R.string.pillTakenTime,
                ix + 1,
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(time * 1000))
            )
        }
        pillsTakenTextArea.text = allTimes.joinToString("\n")
    }

//    private fun initTimer() {
//        timerState = PrefUtil.getTimerState(this)
//
//        secondsRemaining = if (timerState == TimerState.Running)
//            PrefUtil.getSecondsRemaining(this)
//        else
//            timerLengthSeconds
//
//        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
//        if (alarmSetTime > 0)
//            secondsRemaining -= nowSeconds - alarmSetTime
//
////        updateButtons()
////        updateCountdownUI()
//    }

//    // The visual timer has expired
//    private fun onTimerFinished() {
//        timerState = TimerState.Stopped
//
//        //set the length of the timer to be the one set in SettingsActivity
//        //if the length was changed when the timer was running
//        setNewTimerLength()
//
//        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
//        secondsRemaining = timerLengthSeconds
//
//        updateButtons()
//        updateCountdownUI()
//    }

//    private fun updateCountdownUI() {
//        val minutesUntilFinished = secondsRemaining / 60
//        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
//        val secondsStr = secondsInMinuteUntilFinished.toString()
//        textView_countdown.text = String.format("%d:%s", minutesUntilFinished, secondsStr.padStart(2, '0'))
//    }

//    private fun updateButtons() {
//        when (timerState) {
//            TimerState.Running -> {
//                start.visibility = View.INVISIBLE
//                buttonMinus.isEnabled = false
//                buttonPlus.isEnabled = false
//            }
//            TimerState.Stopped -> {
//                start.visibility = View.VISIBLE
//                buttonMinus.isEnabled = true
//                buttonPlus.isEnabled = true
//            }
//        }
//    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}