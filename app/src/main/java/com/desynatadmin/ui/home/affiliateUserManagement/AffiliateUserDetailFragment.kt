package com.desynatadmin.ui.home.affiliateUserManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAffiliateUserDetailBinding
import com.desynatadmin.databinding.RejectReasonLayoutBinding
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetNewAffiliateUserResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateUserDetailFragment : BaseFragment() {
    private var binding: FragmentAffiliateUserDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var userType = "1"
    private var userData :GetNewAffiliateUserResponse.Data.Data?=null
    var actionAccess: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateUserDetailBinding.inflate(layoutInflater,container,false)
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
        userType = arguments?.getString("userType").toString()
        if (!itemJson.isNullOrEmpty()) {
            userData = Gson().fromJson(itemJson, GetNewAffiliateUserResponse.Data.Data::class.java)
            setData()
            updateStatus()
            setStatusData()
        }
        binding?.apply {
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("affiliate_user_management", true)
                    }

                actionAccess = module?.canAction == 1
                btnAccept.isVisible = actionAccess
                btnReject.isVisible = actionAccess
                ivSwitch.isClickable = actionAccess
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSwitch.setOnClickListener {
                if (actionAccess){
                    updateAffiliateUserStatus()
                }
            }

            btnAccept.setOnClickListener {
                acceptAffiliateUser()
            }

            btnReject.setOnClickListener {
                rejectReasonDialog()
            }

            doc1.setOnClickListener {
                CommonMethod.imagePreviewDialog(requireActivity(),userData?.document1?:"")
            }

            doc2.setOnClickListener {
                CommonMethod.imagePreviewDialog(requireActivity(),userData?.document2?:"")
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

        viewModel.getAffiliateUserDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.updateAffiliateUserStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
               userData?.status = response.data.status
                updateStatus()
                isObserver = false
            }
        }

        viewModel.acceptAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),getString(R.string.affiliate_user_approved_successfully)){
                    userType = "2"
                    setStatusData()
                }
                isObserver = false
            }
        }

        viewModel.rejectAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),getString(R.string.affiliate_user_rejected_successfully)){
                    userType = "3"
                    userData?.rejectReason = response.data.rejectReason
                    userData?.rejectType = response.data.rejectType
                    setStatusData()
                }
                isObserver = false
            }
        }

    }

    private fun setStatusData(){
        binding?.apply {
            when (userType){
                "1" ->{
                    btnAccept.isVisible = true
                    btnReject.isVisible = true
                    tvStatus.isVisible = true
                    view2.isVisible = false
                    relStatus.isVisible = false
                    clEarning.isVisible = false
                    clRejected.isVisible = false
                }
                "2"->{
                    btnAccept.isVisible = false
                    btnReject.isVisible = false
                    tvStatus.isVisible = false
                    view2.isVisible = true
                    relStatus.isVisible = true
                    clEarning.isVisible = true
                    clRejected.isVisible = false
                    tvTotalEarningsValue.text = "0 KD"
                    tvTotalOrdersValue.text = "0"
                }
                "3"->{
                    btnAccept.isVisible = false
                    btnReject.isVisible = false
                    tvStatus.isVisible = false
                    view2.isVisible = true
                    relStatus.isVisible = false
                    clEarning.isVisible = false
                    clRejected.isVisible = true
                    tvRejectionReason.text = if (userData?.adminVerify=="3") getString(R.string.complete)
                    else if (userData?.adminVerify=="4") getString(R.string.document_reject) else ""
                    tvRejectionDescription.text = userData?.rejectReason
                }
            }
        }
    }

    private fun updateStatus(){
        binding?.apply {
            if(userData?.status=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
                tvSwitch.text = getString(R.string.active)
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
                tvSwitch.text = getString(R.string.inactive)
                tvSwitch.setTextColor(Color.parseColor("#666666"))
            }

        }
    }

    private fun setData(){
        binding?.apply {
            tvName.text = userData?.fullName
            tvLocation.text = if (Constants.language=="ar") userData?.countryDetails?.nameAr else userData?.countryDetails?.name
            tvJoinedOn.text = "${getString(R.string.joined_at)} ${formatUtcDateToDdMmYyyy(userData?.createdAt.toString())}"
            tvEmail.text = userData?.email
            tvCall.text = userData?.countryCode+"-"+userData?.mobile
            Glide.with(requireActivity()).load(userData?.profileImage).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).transform(
                CircleCrop()
            ).into(ivProfileImage)

            Glide.with(requireActivity()).load(userData?.document1).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(doc1)


            Glide.with(requireActivity()).load(userData?.document2).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(doc2)

           tvAddress.text  = userData?.addressDetails?.locations

            tvIban.text = userData?.bankDetails?.get(0)?.iban
            tvswiftbic.text = userData?.bankDetails?.get(0)?.swift
            tvBankName.text = userData?.bankDetails?.get(0)?.bankName
            tvAccountHolderName.text = userData?.bankDetails?.get(0)?.accountHolderName
            tvAccountNumber.text = userData?.bankDetails?.get(0)?.accountNo
            if (userData?.bankDetails?.get(0)?.uploadProof!="null" && userData?.bankDetails?.get(0)?.uploadProof!=null && userData?.bankDetails?.get(0)?.uploadProof!=""){
                imAccountProof.isVisible = true
                tvProofAccount.isVisible = true
                Glide.with(requireActivity()).load(userData?.bankDetails?.get(0)?.uploadProof).into(imAccountProof)
            }
            else{
                imAccountProof.isVisible = false
                tvProofAccount.isVisible = false
            }
        }
    }

    private fun rejectReasonDialog() {
        var rejectionType = ""
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RejectReasonLayoutBinding.inflate(LayoutInflater.from(context))
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


            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            rbComplete.setOnClickListener {
                rejectionType = "complete"
                rbComplete.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_selected,0,0,0)
                rbReject.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_unselected,0,0,0)
            }

            rbReject.setOnClickListener {
                rejectionType = "document"
                rbComplete.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_unselected,0,0,0)
                rbReject.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_selected,0,0,0)
            }

            btnAccept.setOnClickListener {
                if(rejectionType=="") {
                    showToast(getString(R.string.please_select_reason_type))
                }
                else if(etReason.text?.trim().toString().isEmpty()) {
                    showToast(getString(R.string.please_enter_reason))
                }
                else{
                    rejectAffiliateUser(etReason.text?.trim().toString(),rejectionType)
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

    private fun updateAffiliateUserStatus(){
        val map = HashMap<String,Any>()
        map["user_id"] = userData?.id.toString()
        viewModel.blockUnblockAffiliateUser(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

  private fun getAffiliateUserDetail(){
        viewModel.getAffiliateUserDetail(userData?.id.toString())
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun acceptAffiliateUser(){
        val map = HashMap<String,Any>()
        map["user_id"] = userData?.id.toString()
        viewModel.acceptAffiliateUser(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun rejectAffiliateUser(reason:String,type:String){
        val map = HashMap<String,Any>()
        map["user_id"] = userData?.id.toString()
        map["reject_type"] = type
        map["reject_reason"] = reason
        viewModel.rejectAffiliateUser(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

}