package com.desynatadmin.ui.m2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.CityListAdapter
import com.desynatadmin.adapter.ListAdapter
import com.desynatadmin.adapter.ZoneListAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddCityBottomsheetBinding
import com.desynatadmin.databinding.FragmentCountryCityAndZoneManaBinding
import com.desynatadmin.ui.home.brandManagement.response.CityResponse
import com.desynatadmin.ui.home.brandManagement.response.ServiceCityListResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryCityAndZoneManaFragment : BaseFragment() {

    private var binding: FragmentCountryCityAndZoneManaBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var navController: NavController
    private var cityList: ArrayList<CityResponse.Data>? = arrayListOf()
    private var serviceCityList: ArrayList<ServiceCityListResponse.Data.Data>? = arrayListOf()
    private lateinit var cityAdapter : CityListAdapter
    private var isObserver = true
    var cuountyVisible = false
    var searchCondition = ""
    var countryId = ""
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
        observe()
    }

    override fun initView() {
        viewModel.hitCountryList()
        binding?.apply {
            recyclerView.visibility = View.VISIBLE
            zoneListRecyclerView.visibility = View.GONE
            cityAdapter = CityListAdapter(serviceCityList ?: arrayListOf(), object : CityListAdapter.OnClick {
                override fun onViewCLick(position: Int) {
                    recyclerView.visibility = View.GONE
                    tvCountry.visibility = View.GONE
                    tvDropdown.visibility = View.GONE
                    pagingLayout.visibility = View.GONE
                    zoneListRecyclerView.visibility = View.VISIBLE
                    zoneListRecyclerView.adapter = ZoneListAdapter()
                    key = "2"
                }

                override fun onEditClick(position: Int) {

                }

                override fun onDeleteClick(position: Int) {
                    val map = HashMap<String, Any>()
                    map["city_id"] = serviceCityList?.get(position)?.id ?: ""
                    viewModel.hitDeleteServiceCity(SharedPreference.get(requireContext()).accessToken,
                        map)
                }

                override fun onStatusClick(position: Int) {
                    val map = HashMap<String, Any>()
                    map["city_id"] = serviceCityList?.get(position)?.id ?: ""
                    viewModel.hitChangeCityStatus(SharedPreference.get(requireContext()).accessToken, map)
                }
            })
            recyclerView.adapter = cityAdapter
        }
    }

    override fun initControl() {


        binding?.etSearch?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding?.apply {
            tvToolbar.setOnClickListener {
                if(key == "1"){
                    navController.popBackStack()
                }
                else{
                    recyclerView.visibility = View.INVISIBLE
                    tvCountry.visibility = View.INVISIBLE
                    tvDropdown.visibility = View.INVISIBLE
                    pagingLayout.visibility = View.INVISIBLE
                    zoneListRecyclerView.visibility = View.INVISIBLE
                }
            }
            tvDropdown.setOnClickListener {
                if (cuountyVisible) {
                    cuountyVisible = false
                    binding?.countryRecycler?.isVisible = false
                } else {
                    cuountyVisible = true
                    binding?.countryRecycler?.isVisible = true
                }

            }

            floatingAction.setOnClickListener {
                when (key) {
                    "1" -> {
                        // open bottom sheet for adding city
                        val dialog = BottomSheetDialog(requireContext())
                        val sheetBinding = AddCityBottomsheetBinding.inflate(layoutInflater)
                        dialog.setContentView(sheetBinding.root)
                        var isCityVisible = false
                        sheetBinding.btnClose.setOnClickListener { dialog.dismiss() }
                        var cityId = ""
                        if (!cityList.isNullOrEmpty()) {
                            sheetBinding.cityRecycler.adapter = ListAdapter(
                                cityList ?: arrayListOf(),
                                object : ListAdapter.OnCLick {
                                    override fun onClick(position: Int) {
                                        sheetBinding.tvDropdown.text = cityList?.get(position)?.name
                                        cityId = cityList?.get(position)?.id.toString()
                                        sheetBinding.cityRecycler.isVisible = false
                                        isCityVisible = false
                                    }
                                })
                        }

                        sheetBinding.submitBtn.setOnClickListener {
                            if (cityId.isNullOrEmpty()) {
                                showToast("Please select city")
                            } else {
                                val map = HashMap<String, Any>()
                                map["city_id"] = cityId
                                viewModel.hitAddServiceCity(
                                    SharedPreference.get(requireContext()).accessToken,
                                    map
                                )
                                dialog?.dismiss()
                            }
                        }

                        sheetBinding.tvDropdown.setOnClickListener {
                            if (isCityVisible) {
                                sheetBinding.cityRecycler.isVisible = false
                                isCityVisible = false
                            } else {
                                sheetBinding.cityRecycler.isVisible = true
                                isCityVisible = true
                            }
                        }

                        dialog.show()
                    }

                    "2" -> {
                        // navigate to AddZoneFragment
                        navController.navigate(R.id.action_countryCityAndZoneManaFragment_to_addZoneFragment)
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
        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                viewModel.hitCity(
                    SharedPreference.get(requireContext()).accessToken,
                    response?.data?.get(0)?.id.toString()
                )
                binding?.tvDropdown?.text = response?.data?.get(0)?.name
                countryId = response?.data?.get(0)?.id.toString()
                binding?.countryRecycler?.adapter =
                    ListAdapter(response.data, object : ListAdapter.OnCLick {
                        override fun onClick(position: Int) {
                            cuountyVisible = false
                            binding?.countryRecycler?.isVisible = false
                            countryId = response?.data?.get(position)?.id.toString()
                            binding?.tvDropdown?.text = response?.data?.get(position)?.name
                            viewModel.hitCity(
                                SharedPreference.get(requireContext()).accessToken,
                                countryId
                            )
                        }
                    })
                isObserver = false
            }
        }

        viewModel.hitCityList.observe(viewLifecycleOwner) { response ->
            ProgressDialogUtils.getInstance().hideProgress()
            cityList?.clear()
            cityList?.addAll(response.data)
            hitServiceApi()

        }

        viewModel.getServiceCityList.observe(viewLifecycleOwner) { response ->
            ProgressDialogUtils.getInstance().hideProgress()
            serviceCityList?.clear()
            serviceCityList?.addAll(response?.data?.data ?: arrayListOf())
            cityAdapter.notifyDataSetChanged()
        }

        viewModel.getChangeCityStatus.observe(viewLifecycleOwner) { response ->
            ProgressDialogUtils.getInstance().hideProgress()
            hitServiceApi()

        }
        viewModel.getDeleteCity.observe(viewLifecycleOwner) { response ->
            ProgressDialogUtils.getInstance().hideProgress()
            showToast("City deleted successfully")
            hitServiceApi()
        }

        viewModel.getAddServiceCity.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast("City Added successfully")

                isObserver = false
            }
        }
    }

    fun hitServiceApi(){
        viewModel.hitServiceCityList(
            SharedPreference.get(requireContext()).accessToken,
            countryId,
            "6",
            searchCondition
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
