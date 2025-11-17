package com.desynatadmin.ui.home.userManagement

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentUserDetailBinding
import com.desynatadmin.databinding.FragmentUserManagementBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailFragment : BaseFragment() {
    private var binding: FragmentUserDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var userData: GetUserResponse.Data.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDetailBinding.inflate(layoutInflater,container,false)
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
        val itemJson = arguments?.getString("userData")
        if (!itemJson.isNullOrEmpty()) {
            userData = Gson().fromJson(itemJson, GetUserResponse.Data.Data::class.java)
            setData()
            updateStatus()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "User Detail"
        }
    }

    override fun initControl() {
        binding?.apply {
            findNavController().popBackStack()
        }
    }

    override fun observe() {
    }

    private fun updateStatus(){
        binding?.apply {
            if(userData?.status=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }

        }
    }

    private fun setData(){

        binding?.apply {

            tvName.text = userData?.fullName
            tvLocation.text = userData?.countryInfo?.name
            tvJoinedOn.text = "Joined at ${userData?.createdAt?.let { formatUtcDateToDdMmYyyy(it) }}"
            tvEmail.text = userData?.email
            tvCall.text = userData?.countryCode+"-"+userData?.mobile
            tvCompletedOrder.text = userData?.fullName
            tvCancelledOrder.text = userData?.fullName
            Glide.with(ivProfileImage).load(userData?.profileImage).transform(CircleCrop()).into(ivProfileImage)

            if(userData?.status=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }
        }

    }


}