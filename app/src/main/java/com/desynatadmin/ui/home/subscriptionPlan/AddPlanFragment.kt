package com.desynatadmin.ui.home.subscriptionPlan

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.CountryPickerAdapter
import com.desynatadmin.adapter.FeatureList
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.CustomCountryPickerBinding
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
    var newDurationType: String =""
    var planId: String?=null
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
        setCountry()

        val itemJson = arguments?.getString("subscriptionData")
        binding?.apply {
        if (!itemJson.isNullOrEmpty()) {
            subscriptionData = Gson().fromJson(itemJson, GetAllPlanResponse.Data::class.java)
            toolbar.tvToolbar.text = "Update Plan"
            btnAdd.text = "Update"
            tvPrice.text = "Old Price"
            tvNewPrice.isVisible = true
            etNewPrice.isVisible = true
            tvNewDuration.isVisible = true
            llNewDuration.isVisible = true
            setData()
        }
        else{
            toolbar.tvToolbar.text = "Create New Plan"
            btnAdd.text = "Add"
        }

            etArabicPlanName.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                val regex = Regex("[a-zA-Z]")

                if (source != null && source.contains(regex)) {
                    return@InputFilter ""
                }

                if (dstart == 0 && source.startsWith(" ")) {
                    return@InputFilter ""
                }

                return@InputFilter null
            })
            CommonMethod.removeSpace(etEnglishPlanName)
        }

    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(requireActivity(), countryList = countryList, anchor = etCountry){ selectedCountry ->
                    binding?.etCountry?.setText(selectedCountry.name)
                    selectedCountryCode = selectedCountry.id.toString()
                }
            }

            radioGroupCommision.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbYes -> {
                        tvCommission.visibility = View.VISIBLE
                        etCommission.visibility = View.VISIBLE
                    }
                    R.id.rbNo -> {
                        tvCommission.visibility = View.GONE
                        etCommission.visibility = View.GONE
                    }
                }
            }

            etMonth.setOnClickListener {
                val options = arrayOf("Month", "Year")

                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setTitle("Select Duration Type")
                builder.setItems(options) { dialog, which ->
                    when(options[which]){
                        "Month" -> durationType="1"
                        "Year" -> durationType="2"
                    }

                    etMonth.setText(options[which])
                    dialog.dismiss()
                }
                builder.show()
            }

            etNewMonth.setOnClickListener {
                val options = arrayOf("Month", "Year")

                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setTitle("Select Duration Type")
                builder.setItems(options) { dialog, which ->
                    when(options[which]){
                        "Month" -> newDurationType="1"
                        "Year" -> newDurationType="2"
                    }

                    etNewMonth.setText(options[which])
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
        binding?.apply {


            if (subscriptionData!=null){
                val request = AddPlanRequest(
                    commission = if (rbYes.isChecked) "1" else "0",
                    commission_percentage = etCommission.text?.trim().toString(),
                    countryId = selectedCountryCode.toString(),
                    duration = etDuration.text?.trim().toString(),
                    durationType = durationType,
                    noOfHighlights = etNoOfHighlight.text?.trim().toString(),
                    noOfEmployee = etNoOfEmployee.text?.trim().toString(),
                    noOfProducts = etNoOfProducts.text?.trim().toString(),
                    noOfVideos = etNoOfVideo.text?.trim().toString(),
                    planNameArabic = etArabicPlanName.text?.trim().toString(),
                    planNameEnglish = etEnglishPlanName.text?.trim().toString(),
                    price = etPrice.text?.trim().toString(),
                    stories = if (rbOpen.isChecked) "1" else "0",
                    plan_id = planId,
                    discounted_price = etNewPrice.text?.trim().toString(),
                    new_duration = etNewDuration.text?.trim().toString(),
                    new_duration_type = newDurationType
                )
            viewModel.updateSubscriptionPlan(request)
        }
        else{
                val request = AddPlanRequest(
                    commission = if (rbYes.isChecked) "1" else "0",
                    commission_percentage = etCommission.text?.trim().toString(),
                    countryId = selectedCountryCode.toString(),
                    duration = etDuration.text?.trim().toString(),
                    durationType = durationType,
                    noOfHighlights = etNoOfHighlight.text?.trim().toString(),
                    noOfEmployee = etNoOfEmployee.text?.trim().toString(),
                    noOfProducts = etNoOfProducts.text?.trim().toString(),
                    noOfVideos = etNoOfVideo.text?.trim().toString(),
                    planNameArabic = etArabicPlanName.text?.trim().toString(),
                    planNameEnglish = etEnglishPlanName.text?.trim().toString(),
                    price = etPrice.text?.trim().toString(),
                    stories = if (rbOpen.isChecked) "1" else "0",
                    plan_id = planId
                )
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
                CommonMethod.showSuccessDialog(requireContext(), "Plan Added successfully!"){
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
            planId = subscriptionData?.id.toString()
            etEnglishPlanName.setText(subscriptionData?.planNameEnglish)
            etArabicPlanName.setText(subscriptionData?.planNameArabic)
            etDuration.setText(subscriptionData?.duration.toString())
            if (subscriptionData?.newDuration!=0) {
                etNewDuration.setText(subscriptionData?.newDuration.toString())
            }
            etNoOfVideo.setText(subscriptionData?.noOfVideos.toString())
            etNoOfProducts.setText(subscriptionData?.noOfProducts.toString())
            etNoOfHighlight.setText(subscriptionData?.noOfHighlights.toString())
            etNoOfEmployee.setText(subscriptionData?.noOfEmployee.toString())

            binding?.etCommission?.setText(CommonMethod.formatDoubleValue(subscriptionData?.commissionPercentage))
            binding?.etPrice?.setText(CommonMethod.formatDoubleValue(subscriptionData?.price))
            binding?.etNewPrice?.setText(CommonMethod.formatDoubleValue(subscriptionData?.discountedPrice))

            selectedCountryCode = countryList.find { it.id== subscriptionData?.countryId}?.id.toString()
            rbYes.isChecked = subscriptionData?.commission==1
            if(subscriptionData?.commission==1){
                etCommission.isVisible = true
                tvCommission.isVisible = true
            }
            rbNo.isChecked = subscriptionData?.commission==0
            rbOpen.isChecked = subscriptionData?.stories==1
            rbClose.isChecked = subscriptionData?.stories==0
            durationType = subscriptionData?.durationType.toString()
            etCountry.setText(countryList.find { it.id == subscriptionData?.countryId}?.name.toString())
            when (subscriptionData?.durationType?.toString()) {
                "1" -> etMonth.setText("Month")
                "2" -> etMonth.setText("Year")
                else -> etMonth.setText("")
            }

            when (subscriptionData?.newDurationType?.toString()) {
                "1" -> etNewMonth.setText("Month")
                "2" -> etNewMonth.setText("Year")
                else -> etNewMonth.setText("")
            }
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
                isInvalid(etDuration, "Duration")-> return false
                etMonth.text.isNullOrBlank() -> {
                    showToast("Please select duration type (Month/Year)")
                    return false
                }
                etNoOfVideo.text.isNullOrBlank() -> {
                    showToast("Please enter number of videos")
                    return false
                }
                isInvalid(etNoOfVideo, "No. of Videos")-> return false
                etNoOfProducts.text.isNullOrBlank() -> {
                    showToast("Please enter number of products")
                    return false
                }
                isInvalid(etNoOfProducts, "No. of Products")-> return false
                etNoOfHighlight.text.isNullOrBlank() -> {
                    showToast("Please enter number of highlights")
                    return false
                }
                isInvalid(etNoOfHighlight, "No. of Highlights")-> return false
                etNoOfEmployee.text.isNullOrBlank() -> {
                    showToast("Please enter number of employees")
                    return false
                }
                isInvalid(etNoOfEmployee, "No. of Employee")-> return false
                !rbOpen.isChecked && !rbClose.isChecked -> {
                    showToast("Please select story option (Open/Close)")
                    return false
                }
                etPrice.text.isNullOrBlank() -> {
                    showToast("Please enter price")
                    return false
                }
             /*   etPrice.text.isNullOrBlank()&&subscriptionData!=null -> {
                    showToast("Please enter old price")
                    return false
                }
                etNewPrice.text.isNullOrBlank()&&subscriptionData!=null -> {
                    showToast("Please enter new price")
                    return false
                }*/
                isInvalid(etPrice, "Price")-> return false
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
            }
        }
        return true
    }


    private fun setCountry() {
           countryList = SharedPreference.get(requireActivity()).getCountryList() as ArrayList<GetCountryResponse.Data>

    }

    fun isInvalid(et: AppCompatEditText, fieldName: String): Boolean {
        val value = et.text.toString().trim()
        if (value.isEmpty()) {
            showToast("Please enter $fieldName")
            return true
        }
        val num = value.toIntOrNull() ?: 0
        if (num <= 0) {
            showToast("$fieldName should be greater than 0")
            return true
        }
        return false
    }

}