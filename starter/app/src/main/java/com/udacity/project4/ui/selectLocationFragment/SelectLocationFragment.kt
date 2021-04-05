package com.udacity.project4.ui.selectLocationFragment

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.RemindersActivity.Companion.locationPermissionGranted
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.ui.saveReminderFragment.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    lateinit var map: GoogleMap
    private val DEFAULT_ZOOM = 15

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_select_location,
            container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        _viewModel.selectedPOICount.value = null

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val toast = Toast.makeText(requireActivity().applicationContext,
        R.string.saveText, Toast.LENGTH_LONG)
        toast.show()


//        TODO: add the map setup implementation
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(requireActivity())

//        TODO: call this function after the user confirms on the selected location
        onLocationSelected()


        binding.savePOILatLgnButton.setOnClickListener {
            when (_viewModel.selectedPOICount.value) {
                null -> {
                    Toast.makeText(requireActivity().applicationContext,
                            "You need to select a POI before to save something",
                            Toast.LENGTH_LONG).show()
                }
                1 -> {
                    //Navigate to another fragment to get the user location
                    _viewModel.navigationCommand.value =
                            NavigationCommand.To(
                                    SelectLocationFragmentDirections.
                                    actionSelectLocationFragmentToSaveReminderFragment()
                            )
                }
                2 -> {
                    Toast.makeText(requireActivity().applicationContext,
                            "You have selected more than one POI, please, press the screen and" +
                                    "hold some seconds untils all the POI's are deleted",
                            Toast.LENGTH_LONG).show()
                }
            }
        }

        return binding.root
    }

    //        TODO: add style to the map

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getDeviceLocation()
/*        setMapLongClick(map)*/
        setPoiClick(map)
    }

    /** THE METHOD TO USE DEVICE LOCATION IS IN REMINDERSACTIVITY
     * THE NAME ITS private fun getLocationPermission() {...}
     */

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    fun getDeviceLocation() {
        /** Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.*/
        try {
            if (locationPermissionGranted) {
                Log.i("asd", "$locationPermissionGranted")
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            //TODO: zoom to the user location after taking his permission
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()))
                            map.addMarker(MarkerOptions().
                                          position(LatLng(lastKnownLocation!!.latitude,
                                                   lastKnownLocation!!.longitude)).
                                                    title("Marker in your actual location")

                            )
                        }
                    } else {
                        //        TODO: zoom to the user location after taking his permission
                        map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                        LatLng(defaultLocation.latitude,
                                                defaultLocation.longitude
                                        ), DEFAULT_ZOOM.toFloat()))
                        map.addMarker(MarkerOptions().
                                      position(defaultLocation).
                                      title("Marker in default location"))
                        map.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

/*    //TODO: put a marker to location that the user selected.
    // This method will be used in onMapReady()
    private fun setMapLongClick(map:GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(MarkerOptions()
                .position(latLng)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet))
        }
    }*/

    //TODO: put a marker on a POI that the user selected.
    // This method will be used in onMapReady()
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val snippet = String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Long: %2$.5f",
                    poi.latLng.latitude,
                    poi.latLng.longitude
            )
            val poiMarker = map.addMarker(
                    MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name)
                            .snippet(snippet)
            )
            if (_viewModel.selectedPOICount.value == null) {
                _viewModel.selectedPOICount.value = 1
                _viewModel.latitude.value = poi.latLng.latitude
                _viewModel.longitude.value = poi.latLng.longitude
                _viewModel.reminderSelectedLocationStr.value = poi.name
            }else{
                _viewModel.selectedPOICount.value = 2
            }
            poiMarker.showInfoWindow()
        }

        map.setOnMapLongClickListener {
            map.clear()
            _viewModel.selectedPOICount.value = null
        }

    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
    }
}
