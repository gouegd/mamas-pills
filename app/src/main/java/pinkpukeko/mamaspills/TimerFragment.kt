package pinkpukeko.mamaspills

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.fragment_main.*

class TimerFragment : Fragment() {
    private var timesToRemind: Int = 4

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Do not recreate the fragment on config change (i.e. device rotation)
        this.retainInstance = true

        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateTimesText()
        buttonPlus.setOnClickListener { _ -> timesToRemind++; updateTimesText() }
        buttonMinus.setOnClickListener { _ ->
            if (timesToRemind > 1) {
                timesToRemind--; updateTimesText()
            }
        }
    }

    private fun updateTimesText() {
        textTimes.text = resources.getQuantityString(
                R.plurals.numberOfTimesRemaining,
                timesToRemind,
                timesToRemind
        )
    }
}