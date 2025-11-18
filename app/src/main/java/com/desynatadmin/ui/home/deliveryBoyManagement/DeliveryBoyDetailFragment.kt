package com.desynatadmin.ui.home.deliveryBoyManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentDeliveryBoyDetailBinding
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeliveryBoyDetailFragment : BaseFragment() {
    private var binding: FragmentDeliveryBoyDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var deliveryBoyData: GetAllDeliveryBoyResponse.Data.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryBoyDetailBinding.inflate(layoutInflater,container,false)
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
        val itemJson = arguments?.getString("deliveryBoyData")
        if (!itemJson.isNullOrEmpty()) {
            deliveryBoyData = Gson().fromJson(itemJson, GetAllDeliveryBoyResponse.Data.Data::class.java)
            setData()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Driver Details"
        }
    }

    private fun setData() {
        binding?.apply {
            tvFullName.text = deliveryBoyData?.fullNameEnglish
            tvEmail.text = deliveryBoyData?.email
            tvMobile.text = deliveryBoyData?.countryCode+"-"+deliveryBoyData?.mobile
            tvAddress.text = deliveryBoyData?.addressLine1
          //  tvOrderDelivered.text = deliveryBoyData?.or
            tvZone.text = deliveryBoyData?.zoneInfo?.nameEnglish

            etVehicleName.setText(deliveryBoyData?.vehicleName)
            etVehichleRegistrationNumber.setText(deliveryBoyData?.vehicleRegNo)

            ivProfileImage.load(deliveryBoyData?.profilePic) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivDriving1.load(deliveryBoyData?.drivingLicense?.get(0)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivDriving2.load(deliveryBoyData?.drivingLicense?.get(1)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivVehicle1.load(deliveryBoyData?.vehicleImage?.get(0)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivVehicle2.load(deliveryBoyData?.vehicleImage?.get(1)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivIdentity1.load(deliveryBoyData?.identityProof?.get(0)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivIdentity2.load(deliveryBoyData?.identityProof?.get(1)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivRegistration1.load(deliveryBoyData?.registrationCertificate?.get(0)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivRegistration2.load(deliveryBoyData?.identityProof?.get(1)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivPollutionPaper.load(deliveryBoyData?.pollutionPaper?.get(0)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivVehicleInsurance.load(deliveryBoyData?.vehicleInsurance?.get(0)?.documentPath) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("deliveryBoyData", Gson().toJson(deliveryBoyData))
                }
                findNavController().navigate(R.id.action_deliveryBoyDetailFragment_to_addDeliveryBoyFragment,bundle)
            }

            btnDelete.setOnClickListener {
                deleteDeliveryBoy()
            }
        }
    }

    private fun deleteDeliveryBoy(){
        val map = HashMap<String,Any>()
        map["user_id"] = deliveryBoyData?.id.toString()
        viewModel.deleteDeliveryBoy(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun observe() {

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.deleteDeliveryBoyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                findNavController().popBackStack()
                isObserver = false
            }
        }
    }


}