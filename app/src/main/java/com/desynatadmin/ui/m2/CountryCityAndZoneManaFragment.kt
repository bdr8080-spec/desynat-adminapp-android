package com.desynatadmin.ui.m2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
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
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.SingleTonDataPass
import com.desynatadmin.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryCityAndZoneManaFragment : BaseFragment() {

    private var binding: FragmentCountryCityAndZoneManaBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var navController: NavController
    private var cityList: ArrayList<CityResponse.Data>? = arrayListOf()
    private var message = ""
    private var serviceCityList: ArrayList<ServiceCityListResponse.Data.Data>? = arrayListOf()
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private lateinit var cityAdapter: CityListAdapter
    private var isObserver = true
    var cuountyVisible = false
    var searchCondition = ""
    var countryId = ""
    private var key = "1"
    private var pageNo = 1
    private var lastPage = 1

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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {
            handleBackPress()
        }
    }

    private fun handleBackPress() {
        if (key == "2") {
            hitServiceApi()
            binding?.recyclerView?.visibility = View.VISIBLE
            binding?.tvCountry?.visibility = View.VISIBLE
            binding?.tvDropdown?.visibility = View.VISIBLE
            binding?.pagingLayout?.visibility = View.VISIBLE
            binding?.zoneListRecyclerView?.visibility = View.GONE

            key = "1"
        } else {
            requireActivity().finish()
        }
    }

    fun hitZoneList() {
        viewModel.hitZoneList(
            SharedPreference.get(requireContext()).accessToken,
            SingleTonDataPass.countryId,
            SingleTonDataPass.cityId,
        )

    }

    override fun onResume() {
        super.onResume()
        if (key == "2") {
            hitZoneList()
            binding?.recyclerView?.visibility = View.GONE
            binding?.tvCountry?.visibility = View.GONE
            binding?.tvDropdown?.visibility = View.GONE
            binding?.pagingLayout?.visibility = View.GONE
            binding?.zoneListRecyclerView?.visibility = View.VISIBLE
        } else {
            hitServiceApi()
            binding?.recyclerView?.visibility = View.VISIBLE
            binding?.tvCountry?.visibility = View.VISIBLE
            binding?.tvDropdown?.visibility = View.VISIBLE
            binding?.pagingLayout?.visibility = View.VISIBLE
            binding?.zoneListRecyclerView?.visibility = View.GONE
        }
    }

    override fun initView() {
        if(SharedPreference.get(requireActivity()).getCountryList() == null) {
            viewModel.getCountry()
        }
        else{
            countryList.clear()
            SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
            setCountry()

        }

        binding?.apply {
            if (key == "2") {
                binding?.recyclerView?.visibility = View.GONE
                binding?.tvCountry?.visibility = View.GONE
                binding?.tvDropdown?.visibility = View.GONE
                binding?.pagingLayout?.visibility = View.GONE
                binding?.zoneListRecyclerView?.visibility = View.VISIBLE
            } else {
                binding?.recyclerView?.visibility = View.VISIBLE
                binding?.tvCountry?.visibility = View.VISIBLE
                binding?.tvDropdown?.visibility = View.VISIBLE
                binding?.pagingLayout?.visibility = View.VISIBLE
                binding?.zoneListRecyclerView?.visibility = View.GONE
            }
            cityAdapter =
                CityListAdapter(serviceCityList ?: arrayListOf(), object : CityListAdapter.OnClick {
                    override fun onViewCLick(position: Int) {
                        recyclerView.visibility = View.GONE
                        tvCountry.visibility = View.GONE
                        tvDropdown.visibility = View.GONE
                        pagingLayout.visibility = View.GONE
                        //   zoneListRecyclerView.visibility = View.VISIBLE
                        SingleTonDataPass.countryId = serviceCityList?.get(position)?.country_id ?: ""
                        SingleTonDataPass.cityId = serviceCityList?.get(position)?.id ?: ""
                        hitZoneList()
                        SingleTonDataPass.cityName = serviceCityList?.get(position)?.name ?: ""
                        key = "2"
                    }

                    override fun onEditClick(position: Int) {

                    }

                    override fun onDeleteClick(position: Int) {
                        val map = HashMap<String, Any>()
                        map["city_id"] = serviceCityList?.get(position)?.id ?: ""
                        viewModel.hitDeleteServiceCity(
                            SharedPreference.get(requireContext()).accessToken,
                            map
                        )
                    }

                    override fun onStatusClick(position: Int) {
                        val map = HashMap<String, Any>()
                        map["city_id"] = serviceCityList?.get(position)?.id ?: ""
                        viewModel.hitChangeCityStatus(
                            SharedPreference.get(requireContext()).accessToken,
                            map
                        )
                    }
                })
            recyclerView.adapter = cityAdapter
        }
    }

    override fun initControl() {

        binding?.imForward?.setOnClickListener {
            if (lastPage > pageNo) {
                pageNo++
                hitServiceApi()
            }
        }

        binding?.imBackward?.setOnClickListener {
            if (pageNo > 1) {
                pageNo--
                hitServiceApi()
            }
        }


        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchCondition = p0.toString()
                if (key == "1") {
                    hitServiceApi()
                } else {
                    hitZoneList()
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding?.apply {
            tvToolbar.setOnClickListener {
                 handleBackPress()
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
                        Log.e("citylist", "initControl: ${cityList}" )
                            val dialog = BottomSheetDialog(requireContext())
                            val sheetBinding = AddCityBottomsheetBinding.inflate(layoutInflater)
                            dialog.setContentView(sheetBinding.root)
                            var isCityVisible = false
                            sheetBinding.btnClose.setOnClickListener { dialog.dismiss() }
                            var cityId = ""
                            if (!cityList.isNullOrEmpty()) {
                                sheetBinding.apply {
                                    tvCountry.isVisible = true
                                    tvDropdown.isVisible = true
                                    submitBtn.isVisible = true
                                    tvAlreadyAdded.isVisible = false
                                }
                                sheetBinding.cityRecycler.adapter = ListAdapter(
                                    cityList ?: arrayListOf(),
                                    object : ListAdapter.OnCLick {
                                        override fun onClick(position: Int) {
                                            sheetBinding.tvDropdown.text =
                                                cityList?.get(position)?.name
                                            cityId = cityList?.get(position)?.id.toString()
                                            sheetBinding.cityRecycler.isVisible = false
                                            isCityVisible = false
                                        }
                                    })
                            }
                            else{
                                sheetBinding.apply {
                                    tvCountry.isVisible = false
                                    tvDropdown.isVisible = false
                                    submitBtn.isVisible = false
                                    tvAlreadyAdded.isVisible = true
                                    if (message!="") {
                                        tvAlreadyAdded.text = message
                                    }
                                }
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
                        SingleTonDataPass.isEdit = false
                        SingleTonDataPass.coordinate = emptyList()
                        navController.navigate(R.id.action_countryCityAndZoneManaFragment_to_addZoneFragment)
                    }
                }
            }

           /* // Optional back button to return from Zone → City view
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
            }*/
        }
    }

    override fun observe() {

        viewModel.getZoneData.observe(viewLifecycleOwner) {

            if (!it.data.data.isNullOrEmpty()) {
                binding?.noData?.isVisible = false
                binding?.zoneListRecyclerView?.isVisible = true
                binding?.zoneListRecyclerView?.adapter =
                    ZoneListAdapter(
                        it.data.data ?: arrayListOf(),
                        object : ZoneListAdapter.OnClick {
                            override fun onClick(position: Int) {
                                SingleTonDataPass.isView = true
                                SingleTonDataPass.isEdit = false
                                SingleTonDataPass.coordinate =
                                    it.data.data.get(position).coordinates
                                SingleTonDataPass.enName = it.data.data.get(position).name_english
                                SingleTonDataPass.arName = it.data.data.get(position).name_arabic
                                navController.navigate(R.id.action_countryCityAndZoneManaFragment_to_addZoneFragment)
                            }

                            override fun editClick(position: Int) {
                                SingleTonDataPass.isView = false
                                SingleTonDataPass.isEdit = true
                                SingleTonDataPass.coordinate =
                                    it.data.data.get(position).coordinates
                                SingleTonDataPass.enName = it.data.data.get(position).name_english
                                SingleTonDataPass.arName = it.data.data.get(position).name_arabic
                                SingleTonDataPass.zoneId = it.data?.data?.get(position)?.id ?: ""
                                navController.navigate(R.id.action_countryCityAndZoneManaFragment_to_addZoneFragment)
                            }

                            override fun deleteClick(position: Int) {
                                val map = HashMap<String, Any>()
                                map["zone_id"] = it.data.data?.get(position)?.id ?: ""
                                viewModel.hitDeleteZone(
                                    SharedPreference.get(requireContext()).accessToken,
                                    map
                                )
                            }

                            override fun updateStatus(position: Int) {
                                val map = HashMap<String, Any>()
                                map["zone_id"] = it.data.data?.get(position)?.id ?: ""
                                viewModel.hitUpdateZoneStatus(
                                    SharedPreference.get(requireContext()).accessToken,
                                    map
                                )
                            }
                        })
            } else {
                binding?.noData?.isVisible = true
                binding?.zoneListRecyclerView?.isVisible = false
            }
        }


        viewModel.getUpdateZoneStatus.observe(viewLifecycleOwner) {
            showToast("Updated successfully")
            hitZoneList()
        }

        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
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
        }

        viewModel.hitCityList.observe(viewLifecycleOwner) { response ->
            ProgressDialogUtils.getInstance().hideProgress()
            cityList?.clear()
            cityList?.addAll(response.data)
            message = response.message
            if (key!="2"){
                hitServiceApi()
            }

        }

        viewModel.getDeleteZone.observe(viewLifecycleOwner) {
            CommonMethod.showSuccessDialog(requireContext(), "Zone Deleted successfully!")
            hitZoneList()
        }

        viewModel.getServiceCityList.observe(viewLifecycleOwner) { response ->
            ProgressDialogUtils.getInstance().hideProgress()
            lastPage = response?.data?.last_page ?: 0
            binding?.tvPages?.text = "$pageNo/$lastPage"
            serviceCityList?.clear()
            serviceCityList?.addAll(response?.data?.data ?: arrayListOf())
            if (serviceCityList.isNullOrEmpty()){
                binding?.noData?.isVisible = true
                binding?.recyclerView?.isVisible = false
            }else{
                binding?.noData?.isVisible = false
                binding?.recyclerView?.isVisible = true
            }
            cityAdapter.notifyDataSetChanged()

            if (pageNo<lastPage){
                binding?.imForward?.setImageResource(R.drawable.ic_next_brand)
            }
            else{
                binding?.imForward?.setImageResource(R.drawable.ic_next_brand_disable)
            }

            if (pageNo>1){
                binding?.imBackward?.setImageResource(R.drawable.ic_back_brand)
            }
            else{
                binding?.imBackward?.setImageResource(R.drawable.ic_back_brand_disable)
            }

        }

        viewModel.getChangeCityStatus.observe(viewLifecycleOwner) { response ->
            ProgressDialogUtils.getInstance().hideProgress()
            if(response?.data?.status == 0){
                showToast("City Unblock successfully")
            }
            else{
                showToast("City block successfully")
            }
            hitServiceApi()
        }

        viewModel.getDeleteCity.observe(viewLifecycleOwner) { response ->
            ProgressDialogUtils.getInstance().hideProgress()
            CommonMethod.showSuccessDialog(requireContext(), "City Deleted successfully!")
            hitServiceApi()
            viewModel.hitCity(
                SharedPreference.get(requireContext()).accessToken,
                countryId
            )
        }

        viewModel.getAddServiceCity.observe(viewLifecycleOwner) { response ->
            ProgressDialogUtils.getInstance().hideProgress()
            showToast("City Added successfully")
            hitServiceApi()
            viewModel.hitCity(
                SharedPreference.get(requireContext()).accessToken,
                countryId
            )
        }
    }

    fun hitServiceApi() {
        viewModel.hitServiceCityList(
            SharedPreference.get(requireContext()).accessToken,
            countryId,
            "5",
            searchCondition,
            pageNo.toString()
        )
    }

    private fun setCountry() {
        if (!countryList.isNullOrEmpty()) {
            SharedPreference.get(requireActivity()).saveCountryList(countryList)
            binding?.tvDropdown?.setText(countryList[0].name)
            viewModel.hitCity(
                SharedPreference.get(requireContext()).accessToken,
                countryList?.get(0)?.id.toString()
            )
            binding?.tvDropdown?.text = countryList?.get(0)?.name
            countryId = countryList?.get(0)?.id.toString()
            binding?.countryRecycler?.adapter =
                ListAdapter(countryList, object : ListAdapter.OnCLick {
                    override fun onClick(position: Int) {
                        cuountyVisible = false
                        binding?.countryRecycler?.isVisible = false
                        countryId = countryList?.get(position)?.id.toString()
                        binding?.tvDropdown?.text = countryList?.get(position)?.name
                        viewModel.hitCity(
                            SharedPreference.get(requireContext()).accessToken,
                            countryId
                        )
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
