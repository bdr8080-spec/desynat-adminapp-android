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
import com.desynatadmin.utils.Constants
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
            toolbar.tvToolbar.text = getString(R.string.update_plan)
            btnAdd.text = getString(R.string.update)
            tvPrice.text = getString(R.string.old_price)
            tvNewPrice.isVisible = true
            etNewPrice.isVisible = true
            tvNewDuration.isVisible = true
            llNewDuration.isVisible = true
            setData()
        }
        else{
            toolbar.tvToolbar.text = getString(R.string.create_new_plan)
            btnAdd.text = getString(R.string.add)
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
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(requireActivity(), countryList = countryList, anchor = etCountry, isDialCode = false){ selectedCountry ->
                    etCountry.setText(if (Constants.language=="ar") selectedCountry.nameAr else selectedCountry.name)
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

            radioGroupFreeMonth.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbYesFreeMonth -> {
                        tvNoOfFreeMonth.visibility = View.VISIBLE
                        etNoOfFreeMonth.visibility = View.VISIBLE
                    }
                    R.id.rbNoFreeMonth -> {
                        tvNoOfFreeMonth.visibility = View.GONE
                        etNoOfFreeMonth.visibility = View.GONE
                    }
                }
            }

            radioGroupProductType.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbDefined -> {
                        tvNoOfProduct.visibility = View.VISIBLE
                        etNoOfProducts.visibility = View.VISIBLE
                        viewProduct.visibility = View.VISIBLE
                        viewTvProduct.visibility = View.VISIBLE
                    }
                    R.id.rbUnlimited -> {
                        tvNoOfProduct.visibility = View.GONE
                        etNoOfProducts.visibility = View.GONE
                        viewProduct.visibility = View.GONE
                        viewTvProduct.visibility = View.GONE
                    }
                }
            }

            etMonth.setOnClickListener {
                val options = arrayOf(getString(R.string.month), getString(R.string.year))

                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.select_duration_type))
                builder.setItems(options) { dialog, which ->
                    when(options[which]){
                        getString(R.string.month) -> durationType="1"
                        getString(R.string.year) -> durationType="2"
                    }

                    etMonth.setText(options[which])
                    dialog.dismiss()
                }
                builder.show()
            }

            etNewMonth.setOnClickListener {
                val options = arrayOf(getString(R.string.month), getString(R.string.year))

                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.select_duration_type))
                builder.setItems(options) { dialog, which ->
                    when(options[which]){
                        getString(R.string.month) -> newDurationType="1"
                        getString(R.string.year) -> newDurationType="2"
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
                    noOfProducts =  if (rbDefined.isChecked) etNoOfProducts.text?.trim().toString() else null,
                    noOfVideos = etNoOfVideo.text?.trim().toString(),
                    planNameArabic = etArabicPlanName.text?.trim().toString(),
                    planNameEnglish = etEnglishPlanName.text?.trim().toString(),
                    price = etPrice.text?.trim().toString(),
                    stories = if (rbOpen.isChecked) "1" else "0",
                    plan_id = planId,
                    discounted_price = etNewPrice.text?.trim().toString(),
                    new_duration = etNewDuration.text?.trim().toString(),
                    new_duration_type = newDurationType,

                    productType = if (rbUnlimited.isChecked) "1" else "0",
                    isFreeMonths = if (rbYesFreeMonth.isChecked) "1" else "0",
                    noOfFreeMonths = if (rbYesFreeMonth.isChecked) etNoOfFreeMonth.text?.trim().toString() else null,
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
                    noOfProducts =  if (rbDefined.isChecked) etNoOfProducts.text?.trim().toString() else null,
                    noOfVideos = etNoOfVideo.text?.trim().toString(),
                    planNameArabic = etArabicPlanName.text?.trim().toString(),
                    planNameEnglish = etEnglishPlanName.text?.trim().toString(),
                    price = etPrice.text?.trim().toString(),
                    stories = if (rbOpen.isChecked) "1" else "0",
                    plan_id = planId,

                    productType = if (rbUnlimited.isChecked) "1" else "0",
                    isFreeMonths = if (rbYesFreeMonth.isChecked) "1" else "0",
                    noOfFreeMonths = if (rbYesFreeMonth.isChecked) etNoOfFreeMonth.text?.trim().toString() else null,
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
            etCountry.setText(if (Constants.language=="ar") {
                countryList.find { it.id == subscriptionData?.countryId}?.nameAr.toString()
            }
                    else{
                        countryList.find { it.id == subscriptionData?.countryId}?.name.toString()
            })
            when (subscriptionData?.durationType?.toString()) {
                "1" -> etMonth.setText(getString(R.string.month))
                "2" -> etMonth.setText(getString(R.string.year))
                else -> etMonth.setText("")
            }

            when (subscriptionData?.newDurationType?.toString()) {
                "1" -> etNewMonth.setText(getString(R.string.month))
                "2" -> etNewMonth.setText(getString(R.string.year))
                else -> etNewMonth.setText("")
            }

            rbUnlimited.isChecked = subscriptionData?.productType==1
            rbDefined.isChecked = subscriptionData?.productType==0
            if(subscriptionData?.productType==0){
                etNoOfProducts.isVisible = true
                tvNoOfProduct.isVisible = true
                viewProduct.isVisible = true
                viewTvProduct.isVisible = true

                etNoOfProducts.setText(subscriptionData?.noOfProducts.toString())

            }
            rbYesFreeMonth.isChecked = subscriptionData?.isFreeMonths==1
            rbNoFreeMonth.isChecked = subscriptionData?.isFreeMonths==0
            if(subscriptionData?.isFreeMonths==1){
                etNoOfFreeMonth.isVisible = true
                tvNoOfFreeMonth.isVisible = true

                etNoOfFreeMonth.setText(subscriptionData?.noOfFreeMonths.toString())

            }
        }
    }

    private fun validation(): Boolean {

        binding?.apply {
            when {
                etCountry.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_select_a_country))
                    return false
                }
                etEnglishPlanName.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_plan_name_in_english))
                    return false
                }
                etArabicPlanName.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_plan_name_in_arabic))
                    return false
                }
                etDuration.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_duration))
                    return false
                }
                isInvalid(etDuration, getString(R.string.duration))-> return false
                etMonth.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_select_duration_type_month_year))
                    return false
                }
                etNoOfVideo.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_number_of_videos))
                    return false
                }
                isInvalid(etNoOfVideo, getString(R.string.no_of_videos))-> return false

                !rbUnlimited.isChecked && !rbDefined.isChecked -> {
                    showToast(getString(R.string.please_select_product_type_option_yes_no))
                    return false
                }
                rbDefined.isChecked && etNoOfProducts.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_number_of_products))
                    return false
                }

              //  isInvalid(etNoOfProducts, "No. of Products")-> return false
                etNoOfHighlight.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_number_of_highlights))
                    return false
                }
                isInvalid(etNoOfHighlight, getString(R.string.no_of_highlights))-> return false
                etNoOfEmployee.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_number_of_employees))
                    return false
                }
                isInvalid(etNoOfEmployee, getString(R.string.no_of_employee))-> return false
                !rbOpen.isChecked && !rbClose.isChecked -> {
                    showToast(getString(R.string.please_select_story_option_open_close))
                    return false
                }
                etPrice.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_price))
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
              //  isInvalid(etPrice, "Price")-> return false
                !rbYes.isChecked && !rbNo.isChecked -> {
                    showToast(getString(R.string.please_select_commission_option_yes_no))
                    return false
                }
                rbYes.isChecked && etCommission.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_commission_percentage))
                    return false
                }
                rbYes.isChecked && etCommission.text.toString().toDoubleOrNull()?.let { it < 0 || it > 100 } == true -> {
                    showToast(getString(R.string.commission_should_be_between_0_and_100))
                    return false
                }

                !rbYesFreeMonth.isChecked && !rbNoFreeMonth.isChecked -> {
                    showToast(getString(R.string.please_select_free_month_option_yes_no))
                    return false
                }
                rbYesFreeMonth.isChecked && etNoOfFreeMonth.text.isNullOrBlank() -> {
                    showToast(getString(R.string.please_enter_number_of_free_month))
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
            showToast("${getString(R.string.please_enter)} $fieldName")
            return true
        }
        val num = value.toIntOrNull() ?: 0
        if (num <= 0) {
            showToast("$fieldName ${getString(R.string.should_be_greater_than_zero)}")
            return true
        }
        return false
    }

}