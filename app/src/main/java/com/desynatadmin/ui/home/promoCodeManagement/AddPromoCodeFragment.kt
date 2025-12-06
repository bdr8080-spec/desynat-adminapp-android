package com.desynatadmin.ui.home.promoCodeManagement

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.adapter.UserCountryPickerAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.CustomCountryPickerBinding
import com.desynatadmin.databinding.FragmentAddPromoCodeBinding
import com.desynatadmin.ui.home.promoCodeManagement.response.AddPromoCodeRequest
import com.desynatadmin.ui.home.promoCodeManagement.response.GetPromoCodeResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AddPromoCodeFragment : BaseFragment() {
    private var binding: FragmentAddPromoCodeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var promoCodeData: GetPromoCodeResponse.Data.Data?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var selectedCountryCode: String? = null
    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp=0L
    var toTimestamp=0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPromoCodeBinding.inflate(layoutInflater,container,false)
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
        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = arguments?.getString("promoCodeData")
        binding?.apply {
            CommonMethod.removeSpace(etEnglishPromoCode)
            CommonMethod.applyArabicOnlyFilter(etArabicPromoCode)
            CommonMethod.applyArabicOnlyFilter(etArabicDescription)
            CommonMethod.removeSpace(etEnglishDescription)

            if (!itemJson.isNullOrEmpty()) {
                promoCodeData = Gson().fromJson(itemJson, GetPromoCodeResponse.Data.Data::class.java)
                toolbar.tvToolbar.text = "Edit Promocode"
                btnSubmit.text = "Save Changes"
                setData()

            } else {
                toolbar.tvToolbar.text = "Add Promocode"

            }

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            etCountry.setOnClickListener {
                showUserCountryPicker(requireActivity(), countryList = countryList, anchor = etCountry){ selectedCountry ->
                    etCountry.setText(selectedCountry.name)
                    selectedCountryCode = selectedCountry.id.toString()
                }
            }

            etStartDate.setOnClickListener {
                openCustomDatePicker(requireActivity()){date,timestamp->
                    etStartDate.text=date
                    fromTimestamp=timestamp
                    startDate = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                }

            }

            etEndDate.setOnClickListener {
                openCustomDatePicker(requireActivity(), minDate = fromTimestamp){date,timestamp->
                    etEndDate.text=date
                    toTimestamp=timestamp
                    endDate = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                }
            }

            btnSubmit.setOnClickListener {
                if (validation()) {
                    addPromoCode()
                }
            }

        }
    }

    private fun addPromoCode(){

        val request = AddPromoCodeRequest(
            applicableFor = if (binding?.rbFirst?.isChecked==true) "1" else "2",
            countryId = selectedCountryCode.toString(),
            descriptionArabic = binding?.etArabicDescription?.text.toString(),
            descriptionEnglish = binding?.etEnglishDescription?.text.toString(),
            nameArabic = binding?.etArabicPromoCode?.text.toString(),
            nameEnglish = binding?.etEnglishPromoCode?.text.toString(),
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            uptoDiscount = binding?.etUpToDiscount?.text.toString(),
            minOrderValue = binding?.etMinimumOrderValue?.text.toString(),
            discountPercent = binding?.etDiscount?.text.toString(),
            promocodeId = if(promoCodeData!=null) promoCodeData?.id.toString() else null
        )

        if (promoCodeData!=null){
            viewModel.updatePromoCode(request)
        }
        else{
            viewModel.addPromoCode(request)
        }

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

        viewModel.addPromoCodeResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.updatePromoCodeResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("updatedPromoCode", Gson().toJson(response.data))
                CommonMethod.showSuccessDialog(requireContext(), response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun setCountry() {
        countryList.clear()
        SharedPreference.get(requireActivity()).getUserCountryList()?.let { countryList.addAll(it) }
    }

    private fun setData() {
        binding?.apply {
            etEnglishPromoCode.setText(promoCodeData?.nameEnglish)
            etArabicPromoCode.setText(promoCodeData?.nameArabic)
            etEnglishDescription.setText(promoCodeData?.descriptionEnglish)
            etArabicDescription.setText(promoCodeData?.descriptionArabic)
            etDiscount.setText(promoCodeData?.discountPercent)
            etUpToDiscount.setText(promoCodeData?.uptoDiscount)
            etMinimumOrderValue.setText(promoCodeData?.minOrderValue)
            selectedCountryCode = promoCodeData?.countryId.toString()
            startDate = promoCodeData?.startDate.toString()
            endDate = promoCodeData?.endDate.toString()
            if (promoCodeData?.applicableFor==1){
                rbFirst.isChecked = true
            }
            else{
                rbEvery.isChecked = true
            }
            if(selectedCountryCode!=null){
                etCountry.setText(countryList.firstOrNull { it.id.toString() == selectedCountryCode }?.name)
            }
            if (startDate!=null){
                etStartDate.text=CommonMethod.formatDate(startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (endDate!=null){
                etEndDate.text=CommonMethod.formatDate(endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
        }
    }

    private fun validation(): Boolean {
        binding?.apply {
            if (etCountry.text.toString().isBlank()) {
                showToast("Please select country")
                return false
            }

            if (etEnglishPromoCode.text.toString().trim().isEmpty()) {
                showToast("Please enter promocode name in English")
                return false
            }

            if (etArabicPromoCode.text.toString().trim().isEmpty()) {
                showToast("Please enter promocode name in Arabic")
                return false
            }

            if (etEnglishDescription.text.toString().trim().isEmpty()) {
                showToast("Please enter description in English")
                return false
            }

            if (etArabicDescription.text.toString().trim().isEmpty()) {
                showToast("Please enter description in Arabic")
                return false
            }

            if (radioGroup.checkedRadioButtonId == -1) {
                showToast("Please select applicable order type")
                return false
            }

            if (etDiscount.text.toString().trim().isEmpty()) {
                showToast("Please enter discount percentage")
                return false
            }

            if (etMinimumOrderValue.text.toString().trim().isEmpty()) {
                showToast("Please enter minimum order value")
                return false
            }

            if (etUpToDiscount.text.toString().trim().isEmpty()) {
                showToast("Please enter up to discount value")
                return false
            }

            if (etStartDate.text.toString().trim().isEmpty()) {
                showToast("Please select start date")
                return false
            }

            if (etEndDate.text.toString().trim().isEmpty()) {
                showToast("Please select end date")
                return false
            }

            if (!isEndDateValid()) {
                showToast("End date must be after start date")
                return false
            }
        }
        return true
    }

    private fun isEndDateValid(): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val start = sdf.parse(binding?.etStartDate?.text.toString())
            val end = sdf.parse(binding?.etEndDate?.text.toString())
            end != null && start != null && end >= start
        } catch (e: Exception) {
            false
        }
    }

    fun showUserCountryPicker(
        context: Context,
        anchor: View,
        countryList: ArrayList<GetCountryResponse.Data>,
        onCountrySelected: (GetCountryResponse.Data) -> Unit
    ) {
        val binding = CustomCountryPickerBinding.inflate(LayoutInflater.from(context))

        val popupWindow = PopupWindow(
            binding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f
        popupWindow.showAsDropDown(anchor)

        binding.rvCountry.adapter = UserCountryPickerAdapter(context, countryList,
            object : UserCountryPickerAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {
                    onCountrySelected(countryList[position])
                    popupWindow.dismiss()
                }
            }
        )
    }



}