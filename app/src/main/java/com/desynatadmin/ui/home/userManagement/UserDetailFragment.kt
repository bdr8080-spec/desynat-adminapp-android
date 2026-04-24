package com.desynatadmin.ui.home.userManagement

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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddAmountDialogBinding
import com.desynatadmin.databinding.FragmentUserDetailBinding
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailFragment : BaseFragment() {
    private var binding: FragmentUserDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var userData: GetUserResponse.Data.Data?=null
    var actionAccess: Boolean=true

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
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("user_management", true)
                    }

                actionAccess = module?.canAction == 1
                tvAddMoney.isVisible = actionAccess
                ivSwitch.isClickable = actionAccess
            }
            toolbar.tvToolbar.text = "User Detail"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvAddMoney.setOnClickListener {
                addAmountDialog()
            }

            ivSwitch.setOnClickListener {
                if (actionAccess){
                    updateUserStatus()
                }
            }
        }
    }

    private fun addAmount(amount:String){
        val map = HashMap<String,Any>()
        map["user_id"] = userData?.id.toString()
        map["amount"] = amount
        viewModel.addWalletAmount(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateUserStatus(){
        val map = HashMap<String,Any>()
        map["user_id"] = userData?.id.toString()
        viewModel.blockUnblockUser(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }

        viewModel.updateUserStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
               userData?.status = response.data.status
                updateStatus()
                isObserver = false
            }
        }

        viewModel.walletBalanceResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message){
                    setWalletData(response.data.amount)
                }
                isObserver = false
            }
        }

    }

    private fun setWalletData(amount: String) {
        binding?.apply {
            tvWalletBalance.text = "$amount KD"
        }
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
            if (userData?.walletInfo!=null){
                tvWalletBalance.text = "${CommonMethod.formatAmount(userData?.walletInfo?.amount.toString())} KD"
            }
            tvName.text = userData?.fullName
            tvLocation.text = userData?.countryInfo?.name
            tvJoinedOn.text = "Joined at ${userData?.createdAt?.let { formatUtcDateToDdMmYyyy(it) }}"
            tvEmail.text = userData?.email
            tvCall.text = userData?.countryCode+"-"+userData?.mobile
          /*  tvCompletedOrder.text = userData?.fullName
            tvCancelledOrder.text = userData?.fullName*/
            Glide.with(ivProfileImage).load(userData?.profileImage).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).transform(CircleCrop()).into(ivProfileImage)

            if(userData?.status=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }
        }

    }

    private fun addAmountDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddAmountDialogBinding.inflate(LayoutInflater.from(context))
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

            etAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editable: Editable?) {
                    val text = editable.toString()

                    if (text.length > 1 && text.startsWith("0") && !text.startsWith("0.")) {
                        val newValue = text.trimStart('0')

                        etAmount.removeTextChangedListener(this)
                        etAmount.setText(newValue)
                        etAmount.setSelection(newValue.length)
                        etAmount.addTextChangedListener(this)
                    }
                }
            })

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
               if(isInvalid(etAmount, "Amount")) {
                    return@setOnClickListener
                }
                else{
                    addAmount(etAmount.text?.trim().toString())
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
        val num = value.toIntOrNull() ?: 0
        if (num <= 0) {
            showToast("$fieldName should be greater than 0")
            return true
        }
        return false
    }

}