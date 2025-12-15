package com.desynatadmin.ui.home.affiliateCommission

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.AffiliateCommissionAdapter
import com.desynatadmin.adapter.ShippingCountryTaxAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddTaxDialogBinding
import com.desynatadmin.databinding.FragmentShippingManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.AddTaxResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.GetShippingTaxResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateCommissionFragment : BaseFragment() {
    private var binding: FragmentShippingManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var affiliateCommissionList = ArrayList<String>()
    private var affiliateCommissionAdapter : AffiliateCommissionAdapter?=null
    private var selectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShippingManagementBinding.inflate(layoutInflater,container,false)
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
        binding?.apply {
            toolbar.tvToolbar.text = "Affiliate Commission Percentage"

            affiliateCommissionList.clear()
            affiliateCommissionAdapter = AffiliateCommissionAdapter(requireActivity(),affiliateCommissionList,object : AffiliateCommissionAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    selectedPos = position
                    commissionDialog()
                }

            })

            rvAffiliateCommission.adapter = affiliateCommissionAdapter
        }

        getAffiliateCommission()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }

        viewModel.getAffiliatedCommissionResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                affiliateCommissionList.clear()
              //  affiliateCommissionList.addAll(response.data)
                checkData()
                affiliateCommissionAdapter?.notifyDataSetChanged()
                isObserver = false
            }
        }

        viewModel.updateAffiliateCommissionResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                affiliateCommissionAdapter?.notifyDataSetChanged()
                isObserver = false
            }
        }
    }

    private fun getAffiliateCommission(){
        viewModel.getAffiliatedCommission()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateCommission(commission:String) {
        val map = HashMap<String,Any>()
      //  map["country_id"] = affiliateCommissionList[selectedPos]?.id.toString()
        map["tax"] = commission
        viewModel.updateAffiliateCommission(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = affiliateCommissionList.isEmpty()
    }

    private fun commissionDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddTaxDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            tvToolbar.text = "Affiliate Commission Percentage"
            tvTitle.text = "Commission"
            tvTitle.hint = "Enter commission"
            btnSubmit.text = "Save"
        //    etAmount.setText(CommonMethod.formatPrice(shippingTaxData?.taxInfo?.tax?.toDouble()?:0.0))
            var isEditing = false
            var previousValue = ""

            etAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    previousValue = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    val value = s.toString()

                    if (value.isNotEmpty()) {
                        val num = value.toFloatOrNull() ?: 0f

                        if (num > 100) {
                            etAmount.setText(previousValue)
                            etAmount.setSelection(previousValue.length.coerceAtMost(etAmount.text?.length ?: 0))
                        }
                    }

                    isEditing = false
                }
            })


            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(isInvalid(etAmount, "Tax")) {
                    return@setOnClickListener
                }
                else{
                    updateCommission(etAmount.text.toString())
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

    fun isInvalid(et: AppCompatEditText, fieldName: String): Boolean {
        val value = et.text.toString().trim()
        if (value.isEmpty()) {
            showToast("Please enter $fieldName")
            return true
        }
        val num = value.toDoubleOrNull() ?: 0.0
        if (num <= 0) {
            showToast("$fieldName should be greater than 0")
            return true
        }
        return false
    }



}