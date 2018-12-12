package pinkpukeko.mamaspills

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ringing.*





class RingingActivity : AppCompatActivity() {

    private val alarmURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ringing)

        ringtone = RingtoneManager.getRingtone(applicationContext, alarmURI)
        ringtone?.play()

        buttonStop.setOnClickListener { _ -> this.finish() }
    }

    override fun onDestroy() {
        ringtone?.stop()
        super.onDestroy()
    }
}