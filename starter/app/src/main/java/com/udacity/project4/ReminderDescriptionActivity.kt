package com.udacity.project4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.ui.reminderListFragment.ReminderDataItem
import kotlinx.android.synthetic.main.activity_reminder_description.*

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
//        TODO: Add the implementation of the reminder details
        val bundle: Bundle? = intent.extras
        val reminderDataItem = bundle?.get("EXTRA_ReminderDataItem") as ReminderDataItem
        location_textView.text = reminderDataItem.location
        title_textView.text = reminderDataItem.title
        description_textView.text = reminderDataItem.description
    }
}
