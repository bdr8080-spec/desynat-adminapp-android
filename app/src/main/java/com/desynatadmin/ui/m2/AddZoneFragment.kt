package com.desynatadmin.ui.m2

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.LocationSearchAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddZoneBinding
import com.desynatadmin.databinding.FragmentCountryCityAndZoneManaBinding
import com.desynatadmin.ui.home.brandManagement.response.PlacePrediction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddZoneFragment : BaseFragment(), OnMapReadyCallback {

    private var binding: FragmentAddZoneBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: HomeViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var searchJob: Job? = null
    private var selectedLatLng: LatLng? = null
    private var locationSearchAdapter: LocationSearchAdapter?=null

    private val LOCATION_PERMISSION_REQUEST = 101
    var list=ArrayList<PlacePrediction>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddZoneBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        initMap()
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        locationSearchAdapter= LocationSearchAdapter(list){
            viewModel.httpDetailPlace(it.place_id,getString(R.string.map_key))
        }
        binding?.rvLocation?.adapter=locationSearchAdapter

    }

    override  fun initControl() {
        // Back button
        binding?.tvToolbar?.setOnClickListener {
            findNavController().popBackStack()
        }

        // Submit button
        binding?.btnSubmit?.setOnClickListener {
            selectedLatLng?.let {
                // Use it.latitude and it.longitude
            }
        }

        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(400)
                    if (!s.isNullOrBlank()) {
                        binding?.rvLocation?.visibility = View.VISIBLE
                        fetchPlaceSuggestions(s.toString())}
                    else binding?.rvLocation?.visibility = View.GONE
                }
            }
        })
    }


    override fun observe() {
        viewModel.responseLocation.observe(viewLifecycleOwner){


            locationSearchAdapter?.updateData(it.predictions ?: emptyList())
        }
        viewModel.responseDetailPlace.observe(viewLifecycleOwner){


            val location = it?.result?.geometry?.location
            location?.let {

                val latLng = LatLng(it.lat?:0.0, it.lng?:0.0)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                selectedLatLng=latLng

            }

        }
    }

    private fun initMap() {
        binding?.mapview?.onCreate(null)
        binding?.mapview?.getMapAsync(this)

        // Prevent NestedScrollView from intercepting gestures
        binding?.mapview?.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Enable My Location button if permission granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    selectedLatLng = currentLatLng
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }

        // Track camera center for selected location
        googleMap?.setOnCameraIdleListener {
            selectedLatLng = googleMap?.cameraPosition?.target
        }
    }

    // MapView lifecycle
    override fun onResume() {
        super.onResume()
        binding?.mapview?.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding?.mapview?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.mapview?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding?.mapview?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.mapview?.onSaveInstanceState(outState)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            onMapReady(googleMap!!)
        }
    }

    private fun fetchPlaceSuggestions(input: String) {
        lifecycleScope.launch {
            try {
                viewModel.httpLocationSearch(input,getString(R.string.map_key))

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}