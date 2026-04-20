package com.desynatadmin.ui.m2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.LocationSearchAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddZoneBinding
import com.desynatadmin.ui.home.brandManagement.response.AddZoneRequest
import com.desynatadmin.ui.home.brandManagement.response.PlacePrediction
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.SingleTonDataPass
import com.desynatadmin.utils.arabicFilter
import com.desynatadmin.utils.englishFilter
import com.desynatadmin.utils.showToast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class AddZoneFragment : BaseFragment(), OnMapReadyCallback {

    private var binding: FragmentAddZoneBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: HomeViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var searchJob: Job? = null
    private var selectedLatLng: LatLng? = null
    private var locationSearchAdapter: LocationSearchAdapter? = null
    private lateinit var navController: NavController
    private val LOCATION_PERMISSION_REQUEST = 101
    var list = ArrayList<PlacePrediction>()

    // Polygon-related
    private val polygonPoints = mutableListOf<LatLng>()
    private var currentPolygon: Polygon? = null

    // 👇 Default city name
    private var defaultCityName = "New Delhi"

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
        navController = findNavController()
        defaultCityName = SingleTonDataPass.cityName
        initMap()
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.etZonaNameAr?.filters = arrayOf(arabicFilter)
        binding?.etZonaNameEn?.filters = arrayOf(englishFilter)

        locationSearchAdapter = LocationSearchAdapter(list) {
            viewModel.httpDetailPlace(it.place_id, getString(R.string.map_key))
            binding?.rvLocation?.isVisible = false
        }

        if (SingleTonDataPass.isEdit == true) {
            binding?.etZonaNameEn?.setText(SingleTonDataPass.enName)
            binding?.etZonaNameAr?.setText(SingleTonDataPass.arName)
            binding?.tvToolbar?.text = "Edit Zone"
        } else if (SingleTonDataPass.isView == true) {
            binding?.etSearch?.visibility = View.INVISIBLE
            binding?.btnReset?.visibility = View.INVISIBLE
            binding?.clLayout?.visibility = View.INVISIBLE
            binding?.tvToolbar?.text = "View"
        }
        binding?.rvLocation?.adapter = locationSearchAdapter
    }

    override fun initControl() {
        binding?.tvToolbar?.setOnClickListener { findNavController().popBackStack() }

        binding?.btnSubmit?.setOnClickListener {
            if (polygonPoints.isNotEmpty()) {
                val coordinateList = polygonPoints.map {
                    AddZoneRequest.Coordinate(lat = it.latitude, lng = it.longitude)
                }

                if (SingleTonDataPass.isEdit == true) {
                    viewModel.hitUpdateZone(
                        input = SharedPreference.get(requireContext()).accessToken,
                        req = AddZoneRequest(
                            zone_id = SingleTonDataPass.zoneId ?: "",
                            city_id = SingleTonDataPass.cityId ?: "",
                            coordinates = coordinateList,
                            country_id = SingleTonDataPass.countryId ?: "",
                            name_arabic = SingleTonDataPass.arName ?: "",
                            name_english = SingleTonDataPass.enName ?: ""
                        )
                    )
                } else {
                    viewModel.hitAddZone(
                        SharedPreference.get(requireContext()).accessToken,
                        map = AddZoneRequest(
                            city_id = SingleTonDataPass.cityId,
                            country_id = SingleTonDataPass.countryId,
                            name_arabic = binding?.etZonaNameAr?.text.toString(),
                            name_english = binding?.etZonaNameEn?.text.toString(),
                            coordinates = coordinateList
                        )
                    )
                }
            } else {
                requireContext().showToast("Draw a polygon first")
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
                        fetchPlaceSuggestions(s.toString())
                    } else binding?.rvLocation?.visibility = View.GONE
                }
            }
        })

        binding?.btnReset?.setOnClickListener {
            clearPolygon()
            SingleTonDataPass.coordinate = emptyList()
        }
    }

    override fun observe() {
        viewModel.responseLocation.observe(viewLifecycleOwner) {
            locationSearchAdapter?.updateData(it.predictions ?: emptyList())
        }

        viewModel.getUpdateZone.observe(viewLifecycleOwner) {
            showToast("Updated successfully")
            navController.popBackStack()
        }

        viewModel.getAddZoneData.observe(viewLifecycleOwner) {
            showToast("Zone added successfully")
            navController.popBackStack()
        }

        viewModel.responseDetailPlace.observe(viewLifecycleOwner) { detailResponse ->
            val location = detailResponse?.result?.geometry?.location
            location?.let {
                val latLng = LatLng(it.lat ?: 0.0, it.lng ?: 0.0)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                selectedLatLng = latLng
            }
        }
    }

    private fun initMap() {
        binding?.mapview?.onCreate(null)
        binding?.mapview?.getMapAsync(this)
        binding?.mapview?.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableMyLocation()
        showDefaultCity(defaultCityName)

        // Load polygon if editing or viewing
        if ((SingleTonDataPass.isEdit == true && SingleTonDataPass.coordinate.isNotEmpty()) ||
            (SingleTonDataPass.isView == true && SingleTonDataPass.coordinate.isNotEmpty())
        ) {
            polygonPoints.clear()
            polygonPoints.addAll(SingleTonDataPass.coordinate.map { LatLng(it.lat, it.lng) })
            drawPolygon()
            val bounds = LatLngBounds.builder()
            polygonPoints.forEach { bounds.include(it) }
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
        }

        googleMap?.setOnMapClickListener { latLng ->
            polygonPoints.add(latLng)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.location))
                    .anchor(0.5f, 1f)
            )
            drawPolygon()
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    private fun showDefaultCity(cityName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocationName(cityName, 1)
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    val cityLatLng = LatLng(location.latitude, location.longitude)
                    withContext(Dispatchers.Main) {
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 12f))
                        selectedLatLng = cityLatLng
                    }
                } else {
                    withContext(Dispatchers.Main) { moveToUserLocation() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { moveToUserLocation() }
            }
        }
    }

    private fun moveToUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                selectedLatLng = currentLatLng
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    private fun drawPolygon() {
        currentPolygon?.remove()
        if (polygonPoints.size >= 3) {
            val polygonOptions = PolygonOptions()
                .addAll(polygonPoints)
                .strokeColor(0xFFFF0000.toInt()) // red border
                .fillColor(0x44FF0000) // transparent red fill
                .strokeWidth(4f)
            currentPolygon = googleMap?.addPolygon(polygonOptions)
        } else {
            val polylineOptions = PolylineOptions()
                .addAll(polygonPoints)
                .color(0xFFFF0000.toInt())
                .width(3f)
            googleMap?.addPolyline(polylineOptions)
        }
    }

    private fun clearPolygon() {
        polygonPoints.clear()
        googleMap?.clear()
        currentPolygon = null
    }

    private fun fetchPlaceSuggestions(input: String) {
        lifecycleScope.launch {
            try {
                viewModel.httpLocationSearch(input, getString(R.string.map_key))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ Converts SVG/vector drawable → BitmapDescriptor for custom marker
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int, sizeDp: Int = 36): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
        val sizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            sizeDp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
        vectorDrawable.setBounds(0, 0, sizePx, sizePx)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        }
    }

    override fun onResume() { super.onResume(); binding?.mapview?.onResume() }
    override fun onPause() { super.onPause(); binding?.mapview?.onPause() }
    override fun onDestroy() { super.onDestroy(); binding?.mapview?.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); binding?.mapview?.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.mapview?.onSaveInstanceState(outState)
    }
}
