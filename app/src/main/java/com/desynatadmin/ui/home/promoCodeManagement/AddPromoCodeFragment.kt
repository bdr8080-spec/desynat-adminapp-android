package com.desynatadmin.ui.home.promoCodeManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddPromoCodeBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPromoCodeFragment : BaseFragment() {
    private var binding: FragmentAddPromoCodeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var promoCodeData: GetBrandResponse.Data.Data?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var selectedCountryCode: String? = null

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
                promoCodeData = Gson().fromJson(itemJson, GetBrandResponse.Data.Data::class.java)
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
                CommonMethod.showCountryPicker(requireActivity(), countryList = countryList, anchor = etCountry, isDialCode = false){ selectedCountry ->
                    etCountry.setText(selectedCountry.name)
                    selectedCountryCode = selectedCountry.id.toString()
                }
            }

            btnSubmit.setOnClickListener {

            }

        }
    }

    private fun addPromoCode(){
       /* if (promoCodeData!=null){
            viewModel.updatePromoCode(request)
        }
        else{
            viewModel.addPromoCode(request)
        }*/

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
                CommonMethod.showSuccessDialog(requireContext(), "Promocode Added successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.updatePromoCodeResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Promocode Updated successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun setCountry() {
        countryList = SharedPreference.get(requireActivity()).getCountryList() as ArrayList<GetCountryResponse.Data>
    }

    private fun setData() {
        binding?.apply {
            etEnglishPromoCode.setText(promoCodeData?.nameEnglish)
            etArabicPromoCode.setText(promoCodeData?.nameArabic)
            etEnglishDescription.setText(promoCodeData?.nameArabic)
            etArabicDescription.setText(promoCodeData?.nameArabic)
            etDiscount.setText(promoCodeData?.nameArabic)
            etUpToDiscount.setText(promoCodeData?.nameArabic)
            etMinimumOrderValue.setText(promoCodeData?.nameArabic)
            etStartDate.setText(promoCodeData?.nameArabic)
            etEndDate.setText(promoCodeData?.nameArabic)
        }
    }

        }