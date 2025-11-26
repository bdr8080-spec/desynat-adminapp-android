package com.desynatadmin.ui.home.deliveryBoyManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentDeliveryBoyDetailBinding
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.CommonMethod.loadDocument
import com.desynatadmin.utils.CommonMethod.openPdfInBrowser
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
            tvAddress.text = "${deliveryBoyData?.addressLine1} ${deliveryBoyData?.addressLine2} ${deliveryBoyData?.city} ${deliveryBoyData?.state}"
          //  tvOrderDelivered.text = deliveryBoyData?.or
            tvZone.text = deliveryBoyData?.zoneInfo?.nameEnglish

            etVehicleName.setText(deliveryBoyData?.vehicleName)
            etVehichleRegistrationNumber.setText(deliveryBoyData?.vehicleRegNo)

            ivProfileImage.load(deliveryBoyData?.profilePic) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

         /*   ivDriving1.load(deliveryBoyData?.drivingLicense?.get(0)?.documentPath) {
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
            }*/

            if (deliveryBoyData?.registrationCertificate!=null) {
                if(deliveryBoyData?.registrationCertificate?.get(0)==null){
                    ivRegistration1.isVisible = false
                }
                if(deliveryBoyData?.registrationCertificate?.get(1)==null){
                    ivRegistration2.isVisible = false
                }


                loadDocument(ivRegistration1, deliveryBoyData?.registrationCertificate?.get(0)?.documentPath)
                loadDocument(ivRegistration2, deliveryBoyData?.registrationCertificate?.get(1)?.documentPath)

             /*   ivRegistration1.load(deliveryBoyData?.registrationCertificate?.get(0)?.documentPath) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }

                ivRegistration2.load(deliveryBoyData?.identityProof?.get(1)?.documentPath) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }*/
            }
            else{
                tvRegistration.isVisible = false
                llRegistration.isVisible = false
            }

            if (deliveryBoyData?.pollutionPaper!=null){
                loadDocument(ivPollutionPaper, deliveryBoyData?.pollutionPaper?.get(0)?.documentPath)
               /* ivPollutionPaper.load(deliveryBoyData?.pollutionPaper?.get(0)?.documentPath) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }*/
            }
            else{
                tvPollutionPaper.isVisible = false
                llPollutionPaper.isVisible = false
            }

//            ivVehicleInsurance.load(deliveryBoyData?.vehicleInsurance?.get(0)?.documentPath) {
//                crossfade(true)
//                placeholder(R.drawable.ic_placeholder)
//                error(R.drawable.ic_placeholder)
//            }

            loadDocument(ivDriving1, deliveryBoyData?.drivingLicense?.get(0)?.documentPath)
            loadDocument(ivDriving2, deliveryBoyData?.drivingLicense?.get(1)?.documentPath)

            loadDocument(ivVehicle1, deliveryBoyData?.vehicleImage?.get(0)?.documentPath)
            loadDocument(ivVehicle2, deliveryBoyData?.vehicleImage?.get(1)?.documentPath)

            loadDocument(ivIdentity1, deliveryBoyData?.identityProof?.get(0)?.documentPath)
            loadDocument(ivIdentity2, deliveryBoyData?.identityProof?.get(1)?.documentPath)

            loadDocument(ivVehicleInsurance, deliveryBoyData?.vehicleInsurance?.get(0)?.documentPath)

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
                CommonMethod.deleteAlertDialog(
                    requireActivity(),
                    "Are you sure you want to delete the delivery boy account?"
                ) { action ->
                    when (action) {
                        CommonMethod.DialogAction.DELETE -> {
                            deleteDeliveryBoy()
                        }

                        CommonMethod.DialogAction.DISCARD -> {

                        }
                    }
                }
            }

            ivVehicle1.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.vehicleImage?.get(0)?.documentPath ?: "")
            }

            ivVehicle2.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.vehicleImage?.get(1)?.documentPath ?: "")
            }

            ivVehicleInsurance.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.vehicleInsurance?.get(0)?.documentPath ?: "")
            }

            ivPollutionPaper.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.pollutionPaper?.get(0)?.documentPath ?: "")
            }

            ivIdentity1.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.identityProof?.get(0)?.documentPath ?: "")
            }

            ivIdentity2.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.identityProof?.get(1)?.documentPath ?: "")
            }

            ivDriving1.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.drivingLicense?.get(0)?.documentPath ?: "")
            }

            ivDriving2.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.drivingLicense?.get(1)?.documentPath ?: "")
            }


            ivRegistration1.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.registrationCertificate?.get(0)?.documentPath ?: "")
            }

            ivRegistration2.setOnClickListener {
                openPdfInBrowser(requireContext(), deliveryBoyData?.registrationCertificate?.get(1)?.documentPath ?: "")
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