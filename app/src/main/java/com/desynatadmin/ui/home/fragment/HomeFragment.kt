package com.desynatadmin.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentHomeBinding
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isCountryObserver = true
    var user : LoginResponse.Data?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        if(SharedPreference.get(requireActivity()).getCountryList() == null) {
            getCountryApi()
        }
        else{
            countryList.clear()
            SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
        }
        user = SharedPreference.get(requireActivity()).getUser()
       binding?.apply {
           tvName.text = "Hi ${user?.fullName}"
       }
    }

    override fun initControl() {
        binding?.apply {
            ivProfileImage.setOnClickListener {
                (requireActivity() as? HomeActivity)?.openDrawer()
            }
        }
    }

    private fun getCountryApi(){
//        ProgressDialogUtils.getInstance().showProgress(requireActivity())
//        isCountryObserver = true
        viewModel.getCountry()
    }

    private fun setCountry() {
        if (!countryList.isNullOrEmpty()) {
            SharedPreference.get(requireActivity()).saveCountryList(countryList)
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
          //  if (isObserver||isCountryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
//                isCountryObserver = false
//            }
        }

        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
           // if (isCountryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                countryList.clear()
                countryList.addAll(response.data)
                setCountry()

//                isCountryObserver = false
//            }
        }
    }


}