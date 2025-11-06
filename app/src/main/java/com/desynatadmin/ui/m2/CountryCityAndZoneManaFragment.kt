package com.desynatadmin.ui.m2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.CityListAdapter
import com.desynatadmin.adapter.ZoneListAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.AddCityBottomsheetBinding
import com.desynatadmin.databinding.FragmentCountryCityAndZoneManaBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CountryCityAndZoneManaFragment : BaseFragment() {

    private var binding: FragmentCountryCityAndZoneManaBinding? = null
    private lateinit var navController: NavController
    private var key = "1"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountryCityAndZoneManaBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        initView()
        initControl()
    }

    override fun initView() {
        binding?.apply {
            // City list shown by default
            recyclerView.visibility = View.VISIBLE
            zoneListRecyclerView.visibility = View.GONE

            recyclerView.adapter = CityListAdapter { selectedCity ->
                // Hide the city layout views
                recyclerView.visibility = View.GONE
                tvCountry.visibility = View.GONE
                tvDropdown.visibility = View.GONE
                pagingLayout.visibility = View.GONE

                // Show the zone list instead
                zoneListRecyclerView.visibility = View.VISIBLE
                zoneListRecyclerView.adapter = ZoneListAdapter(selectedCity){

                }

                key = "2"
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            tvToolbar.setOnClickListener {
                navController.popBackStack()
            }

            floatingAction.setOnClickListener {
                Log.d("ZoneDebug", "Floating clicked → key = $key")

                when (key) {
                    "1" -> {
                        // open bottom sheet for adding city
                        val dialog = BottomSheetDialog(requireContext())
                        val sheetBinding = AddCityBottomsheetBinding.inflate(layoutInflater)
                        dialog.setContentView(sheetBinding.root)

                        sheetBinding.btnClose.setOnClickListener { dialog.dismiss() }

                        dialog.show()
                    }

                    "2" -> {
                        // navigate to AddZoneFragment
                        navController.navigate(
                            R.id.action_countryCityAndZoneManaFragment_to_addZoneFragment
                        )
                    }
                }
            }

            // Optional back button to return from Zone → City view
            imBackward.setOnClickListener {
                if (key == "2") {
                    recyclerView.visibility = View.VISIBLE
                    tvCountry.visibility = View.VISIBLE
                    tvDropdown.visibility = View.VISIBLE
                    pagingLayout.visibility = View.VISIBLE
                    zoneListRecyclerView.visibility = View.GONE
                    key = "1"
                } else {
                    navController.popBackStack()
                }
            }
        }
    }

    override fun observe() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
