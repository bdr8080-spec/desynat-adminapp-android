package com.desynatadmin.ui.home.affiliateCommission

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.desynatadmin.R
import com.desynatadmin.adapter.AffiliateCommissionAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddTaxDialogBinding
import com.desynatadmin.databinding.FragmentAffiliateCommissionBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.affiliateCommission.response.GetAffiliateCommissionResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateCommissionFragment : BaseFragment() {
    private var binding: FragmentAffiliateCommissionBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var affiliateCommissionList = ArrayList<GetAffiliateCommissionResponse.Data>()
    private var affiliateCommissionAdapter : AffiliateCommissionAdapter?=null
    private var selectedPos = -1

    private var updateCommissionDialog: Dialog?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateCommissionBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = getString(R.string.affiliate_commission_percentage)

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

    toolbar.ivBack.setOnClickListener {
    when (val activity = requireActivity()) {
        is HomeActivity -> activity.onBackPressed()
        is SubAdminHomeActivity -> activity.onBackPressed()
        else -> activity.onBackPressed()
    }
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
                affiliateCommissionList.addAll(response.data)
                checkData()
                affiliateCommissionAdapter?.notifyDataSetChanged()
                isObserver = false
            }
        }

        viewModel.updateAffiliateCommissionResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                updateCommissionDialog?.dismiss()
                CommonMethod.showSuccessDialog(requireActivity(),
                    getString(R.string.affiliate_commission_updated_successfully))
                affiliateCommissionList[selectedPos].affiliateCommissionInfo = response.data
                affiliateCommissionAdapter?.notifyDataSetChanged()
                selectedPos = -1
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
        map["country_id"] = affiliateCommissionList[selectedPos]?.affiliateCommissionInfo?.countryId.toString()
        map["commission"] = commission
        viewModel.updateAffiliateCommission(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = affiliateCommissionList.isEmpty()
    }

    private fun commissionDialog() {
        updateCommissionDialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddTaxDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { it.let { it1 -> updateCommissionDialog?.setContentView(it1) } }

        val window: Window = updateCommissionDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        updateCommissionDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            tvToolbar.text = getString(R.string.affiliate_commission_percentage)
            tvTitle.text = getString(R.string.commission)
            tvTitle.hint = getString(R.string.enter_commission)
            btnSubmit.text = getString(R.string.save)
            etAmount.setText(CommonMethod.formatPrice(affiliateCommissionList[selectedPos]?.affiliateCommissionInfo?.commission?.toDouble()?:0.0))
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
                updateCommissionDialog?.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(isInvalid(etAmount, "Tax")) {
                    return@setOnClickListener
                }
                else{
                    updateCommission(etAmount.text.toString())
                }
            }

        }

        updateCommissionDialog?.show()
    }

    fun isInvalid(et: AppCompatEditText, fieldName: String): Boolean {
        val value = et.text.toString().trim()
        if (value.isEmpty()) {
            showToast("${getString(R.string.please_enter)} $fieldName")
            return true
        }
        val num = value.toDoubleOrNull() ?: 0.0
        if (num <= 0) {
            showToast("$fieldName ${getString(R.string.should_be_greater_than_zero)}")
            return true
        }
        return false
    }



}