package com.desynatadmin.ui.home.subscriptionPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddPlanBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.AddPlanRequest
import com.desynatadmin.ui.home.subscriptionPlan.response.GetAllPlanResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody

@AndroidEntryPoint
class AddPlanFragment : BaseFragment() {
    private var binding: FragmentAddPlanBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var subscriptionData: GetAllPlanResponse.Data?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var selectedCountryCode: String? = null
    var durationType: String =""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPlanBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        val itemJson = arguments?.getString("subscriptionData")
        binding?.apply {
        if (!itemJson.isNullOrEmpty()) {
            subscriptionData = Gson().fromJson(itemJson, GetAllPlanResponse.Data::class.java)
            toolbar.tvToolbar.text = "Update Plan"
            btnAdd.text = "Update"
            setData()
        }
        else{
            toolbar.tvToolbar.text = "Create New Plan"
            btnAdd.text = "Add"
        }
        }

        setCountry()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            etCountry.setOnClickListener {
                ccpCountryPicker.launchCountrySelectionDialog()
            }

            ccpCountryPicker.setOnCountryChangeListener {
                val countryName = ccpCountryPicker.selectedCountryName
                selectedCountryCode = ccpCountryPicker.selectedCountryNameCode
                etCountry.setText(countryName)
            }

            etMonth.setOnClickListener {
                val options = arrayOf("Days","Month", "Year")

                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setTitle("Select Duration Type")
                builder.setItems(options) { dialog, which ->
                    when(options[which]){
                        "Days" -> durationType="1"
                        "Month" -> durationType="2"
                        "Year" -> durationType="3"
                    }

                    etMonth.setText(options[which])
                    dialog.dismiss()
                }
                builder.show()
            }

            btnAdd.setOnClickListener {
                if (validation()){
                    addSubscription()
                }
            }
        }
    }

    fun addSubscription(){
        val map = HashMap<String, RequestBody>()

       /* if (subscriptionData!=null){
            map["brand_id"] = brandData?.id.toString().toPlainRequestBody()
        }*/
        binding?.apply {
            val request = AddPlanRequest(
                commission = etCommission.text?.trim().toString(),
                countryId = countryList.find { it.isoCode == selectedCountryCode }?.id.toString(),
                duration = etDuration.text?.trim().toString(),
                durationType = durationType,
                noOfHighlights = etNoOfHighlight.text?.trim().toString(),
                noOfProducts = etNoOfProducts.text?.trim().toString(),
                noOfVideos = etNoOfVideo.text?.trim().toString(),
                planNameArabic = etArabicPlanName.text?.trim().toString(),
                planNameEnglish = etEnglishPlanName.text?.trim().toString(),
                price = etPrice.text?.trim().toString(),
                stories = if (rbOpen.isChecked) "1" else "0"
            )

            if (subscriptionData!=null){
            viewModel.updateSubscriptionPlan(request)
        }
        else{
            viewModel.addSubscriptionPlan(request)
        }

            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.addSubscriptionPlanResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Plan purchased successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.editSubscriptionPlanResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Plan updated successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun setData(){
        binding?.apply {
            etEnglishPlanName.setText(subscriptionData?.planNameEnglish)
            etArabicPlanName.setText(subscriptionData?.planNameArabic)
            etDuration.setText(subscriptionData?.duration.toString())
            etMonth.setText(subscriptionData?.durationType.toString())
            etNoOfVideo.setText(subscriptionData?.noOfVideos.toString())
            etNoOfProducts.setText(subscriptionData?.noOfProducts.toString())
            etNoOfHighlight.setText(subscriptionData?.noOfHighlights.toString())
            etNoOfEmployee.setText(subscriptionData?.noOfEmployee.toString())
            etPrice.setText(subscriptionData?.price.toString())
            etCommission.setText(subscriptionData?.commissionPercentage.toString())
        }
    }


    private fun validation(): Boolean {
        binding?.apply {
            when {
                etCountry.text.isNullOrBlank() -> {
                    showToast("Please select a country")
                    return false
                }
                etEnglishPlanName.text.isNullOrBlank() -> {
                    showToast("Please enter plan name in English")
                    return false
                }
                etArabicPlanName.text.isNullOrBlank() -> {
                    showToast("Please enter plan name in Arabic")
                    return false
                }
                etDuration.text.isNullOrBlank() -> {
                    showToast("Please enter duration")
                    return false
                }
                etMonth.text.isNullOrBlank() -> {
                    showToast("Please select duration type (Month/Year)")
                    return false
                }
                etNoOfVideo.text.isNullOrBlank() -> {
                    showToast("Please enter number of videos")
                    return false
                }
                etNoOfProducts.text.isNullOrBlank() -> {
                    showToast("Please enter number of products")
                    return false
                }
                etNoOfHighlight.text.isNullOrBlank() -> {
                    showToast("Please enter number of highlights")
                    return false
                }
                etNoOfEmployee.text.isNullOrBlank() -> {
                    showToast("Please enter number of employees")
                    return false
                }
                !rbOpen.isChecked && !rbClose.isChecked -> {
                    showToast("Please select story option (Open/Close)")
                    return false
                }
                etPrice.text.isNullOrBlank() -> {
                    showToast("Please enter price")
                    return false
                }
                !rbYes.isChecked && !rbNo.isChecked -> {
                    showToast("Please select commission option (Yes/No)")
                    return false
                }
                rbYes.isChecked && etCommission.text.isNullOrBlank() -> {
                    showToast("Please enter commission percentage")
                    return false
                }
                rbYes.isChecked && etCommission.text.toString().toDoubleOrNull()?.let { it < 0 || it > 100 } == true -> {
                    showToast("Commission should be between 0 and 100")
                    return false
                }
                etDuration.text.toString().toIntOrNull() == null -> {
                    showToast("Duration should be a valid number")
                    return false
                }
                etNoOfVideo.text.toString().toIntOrNull() == null -> {
                    showToast("No. of videos should be a valid number")
                    return false
                }
                etNoOfProducts.text.toString().toIntOrNull() == null -> {
                    showToast("No. of products should be a valid number")
                    return false
                }
                etNoOfHighlight.text.toString().toIntOrNull() == null -> {
                    showToast("No. of highlights should be a valid number")
                    return false
                }
                etNoOfEmployee.text.toString().toIntOrNull() == null -> {
                    showToast("No. of employees should be a valid number")
                    return false
                }
                etPrice.text.toString().toDoubleOrNull() == null -> {
                    showToast("Price should be a valid number")
                    return false
                }
            }
        }
        return true
    }


    private fun setCountry() {

           countryList = SharedPreference.get(requireActivity()).getCountryList() as ArrayList<GetCountryResponse.Data>
            val countryCodes = countryList.map { it.isoCode }
            val countryCodeString = countryCodes.joinToString(",")
            binding?.ccpCountryPicker?.setCustomMasterCountries(countryCodeString)


    }

}