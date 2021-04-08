package com.udacity.project4.ui.saveReminderFragment

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.udacity.project4.R
import com.udacity.project4.RemindersActivity
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.ui.reminderListFragment.ReminderDataItem
import com.udacity.project4.utils.sendNotification
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class SaveReminderFragment : BaseFragment() {

    /**
     * Usefull variables for the Activity and fragment's associated.
     */
    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        const val LOCATION_PERMISSION_INDEX = 0
        const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
        var locationPermissionGranted = false
        internal const val ACTION_GEOFENCE_EVENT =
                "SaveReminderFragment.ui.action.ACTION_GEOFENCE_EVENT"
        const val GEOFENCE_RADIUS_IN_METERS = 100f
        val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)
    }

    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    lateinit var geofencingClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy{
        val intent= Intent(requireContext(),
                GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,
                                          R.layout.fragment_save_reminder,
                                          container,
                                         false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        geofencingClient = LocationServices.getGeofencingClient(requireActivity().applicationContext)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value
            val reminderDataItem = ReminderDataItem(title, description, location, latitude, longitude)
//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
            addGeofence(reminderDataItem, _viewModel.validateEnteredData(reminderDataItem))
            // TODO : 2) save the reminder to the local db
            _viewModel.validateAndSaveReminder(reminderDataItem)
            sendNotification(requireActivity().applicationContext,reminderDataItem)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    /**
     * With this method, i will add geofences after the user save a POI
     */
    // Build the Geofence Object
    fun addGeofence(reminderDataItem: ReminderDataItem, boolean: Boolean) {
        if (boolean) {
            val geofence = Geofence.Builder()
                    // Set the request ID, string to identify the geofence.
                    .setRequestId(reminderDataItem.title)
                    // Set the circular region of this geofence.
                    .setCircularRegion(reminderDataItem.latitude!!,
                            reminderDataItem.longitude!!,
                            GEOFENCE_RADIUS_IN_METERS
                    )
                    // Set the expiration duration of the geofence. This geofence gets
                    // automatically removed after this period of time.
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build()

            // Build the geofence request
            val geofencingRequest = GeofencingRequest.Builder()
                    // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
                    // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
                    // is already inside that geofence.
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    // Add the geofences to be monitored by geofencing service.
                    .addGeofence(geofence)
                    .build()

            val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
            intent.action = ACTION_GEOFENCE_EVENT

            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                addOnSuccessListener {
                    // Geofences added.
                    Log.e("Add Geofence", geofence.requestId)
                }
                addOnFailureListener {
                    // Failed to add geofences.
                    if ((it.message != null)) {
                        Log.w("asd", it.message!!)
                    }
                }
            }
        }
    }
}
