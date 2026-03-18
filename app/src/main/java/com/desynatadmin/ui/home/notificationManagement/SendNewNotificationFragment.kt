package com.desynatadmin.ui.home.notificationManagement

import android.app.Dialog
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
import com.desynatadmin.R
import com.desynatadmin.adapter.AssignAffiliateUserAdapter
import com.desynatadmin.adapter.AssignDeliveryBoyAdapter
import com.desynatadmin.adapter.AssignSellerAdapter
import com.desynatadmin.adapter.AssignUserAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSendNewNotificationBinding
import com.desynatadmin.databinding.SelectNotificationUserDialogBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetNewAffiliateUserResponse
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.ui.home.notificationManagement.response.SendNotificationRequest
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SendNewNotificationFragment : BaseFragment(){
    private var binding: FragmentSendNewNotificationBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var assignUserAdapter: AssignUserAdapter?=null
    private var assignDriverAdapter: AssignDeliveryBoyAdapter?=null
    private var assignAffiliateUserAdapter: AssignAffiliateUserAdapter?=null
    private var assignSellerAdapter: AssignSellerAdapter?=null
    private var userList = ArrayList<GetUserResponse.Data.Data>()
    private var affiliateUserList = ArrayList<GetNewAffiliateUserResponse.Data.Data>()
    private var driverList = ArrayList<GetAllDeliveryBoyResponse.Data.Data>()
    private var sellerList = ArrayList<ApprovedSellerResponse.Data.Data>()
    private var dialog: Dialog?=null
    private var receiverType = 0 //1-admin,2-user,3-seller,4-affiliate,5-driver
    private var senderType = 1

    private var isAllSelected = false
    private var selectedReceiverList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendNewNotificationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = "Send New"

            CommonMethod.applyArabicOnlyFilter(etArabicTitle)
            CommonMethod.applyArabicOnlyFilter(etArabicDescription)
            CommonMethod.removeSpace(etEnglishTitle)
            CommonMethod.removeSpace(etArabicDescription)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSend.setOnClickListener {
                if (validation()){
                    sendNotification()
                }
            }

            tvSendType.setOnClickListener {
                selectUserDialog()
            }

            radioSendTo.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbCustomer -> {
                        receiverType = 2
                        tvSendType.isVisible = true

                        if (userList.isEmpty()){
                            getAllUser()
                        }
                        updateSelectedNames()

                    }
                    R.id.rbSeller -> {
                        receiverType = 3
                        tvSendType.isVisible = true
                        if (sellerList.isEmpty()){
                            getAllSeller()
                        }
                        updateSelectedNames()
                    }
                    R.id.rbAffiliate -> {
                        receiverType = 4
                        tvSendType.isVisible = true
                        if (affiliateUserList.isEmpty()){
                            getAffiliateUser()
                        }
                        updateSelectedNames()
                    }
                    R.id.rbDriver -> {
                        receiverType = 5
                        tvSendType.isVisible = true
                        if (driverList.isEmpty()){
                            getDeliveryBoy()
                        }
                        updateSelectedNames()

                    }

                }
            }
        }
    }

    private fun sendNotification() {
       val request = SendNotificationRequest(
           titleEnglish = binding?.etEnglishTitle?.text?.trim().toString(),
           titleArabic = binding?.etArabicTitle?.text?.trim().toString(),
           descriptionEnglish = binding?.etEnglishDescription?.text?.trim().toString(),
           descriptionArabic = binding?.etArabicDescription?.text?.trim().toString(),
           senderType = senderType,
//           driverId = if (receiverType==5) selectedReceiverList else null,
//           userId = if (receiverType==2) selectedReceiverList else null,
//           sellerId = if (receiverType==3) selectedReceiverList else null,
           driverId = driverList
               .filter { it.isSelected }
               .map { it.id.toString() }
               .takeIf { it.isNotEmpty() }
               ?.let { ArrayList(it) },
           userId = userList
               .filter { it.isSelected }
               .map { it.id.toString() }
               .takeIf { it.isNotEmpty() }
               ?.let { ArrayList(it) },
           sellerId = sellerList
               .filter { it.isSelected }
               .map { it.id.toString() }
               .takeIf { it.isNotEmpty() }
               ?.let { ArrayList(it) } ,
           affiliateId = affiliateUserList
               .filter { it.isSelected }
               .map { it.id.toString() }
               .takeIf { it.isNotEmpty() }
               ?.let { ArrayList(it) }


       )
        viewModel.sendNotification(request)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.sendNotificationResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Notification sent successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.getAllUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                userList.clear()
                userList.addAll(response.data.data)
                isObserver = false
            }
        }

        viewModel.getApprovedSellerList.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                sellerList.clear()
                sellerList.addAll(response.data.data)
                isObserver = false
            }
        }

        viewModel.getDeliveryBoyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                driverList.clear()
                driverList.addAll(response.data.data)
                isObserver = false
            }
        }

        viewModel.getApprovedAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                affiliateUserList.clear()
                affiliateUserList.addAll(response.data.data)
                isObserver = false
            }
        }
    }

    private fun selectUserDialog() {
        val gson = Gson()

        val tempUserList = ArrayList(
            userList.map {
                gson.fromJson(
                    gson.toJson(it),
                    GetUserResponse.Data.Data::class.java
                )
            }
        )
        val tempSellerList = ArrayList(
            sellerList.map {
                gson.fromJson(
                    gson.toJson(it),
                    ApprovedSellerResponse.Data.Data::class.java
                )
            }
        )

        val tempDriverList = ArrayList(
            driverList.map {
                gson.fromJson(
                    gson.toJson(it),
                    GetAllDeliveryBoyResponse.Data.Data::class.java
                )
            }
        )

        val tempAffiliateUserList = ArrayList(
            affiliateUserList.map {
                gson.fromJson(
                    gson.toJson(it),
                    GetNewAffiliateUserResponse.Data.Data::class.java
                )
            }
        )

        dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = SelectNotificationUserDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            tvSelectAllUser.setOnClickListener {

                isAllSelected = !isAllSelected

                when (receiverType) {
                    2 -> userList.forEach { it.isSelected = isAllSelected }
                    3 -> sellerList.forEach { it.isSelected = isAllSelected }
                    4 -> affiliateUserList.forEach { it.isSelected = isAllSelected }
                    5 -> driverList.forEach { it.isSelected = isAllSelected }
                }

                updateSelectAllUI(dialogBinding)
                assignUserAdapter?.notifyDataSetChanged()
                assignSellerAdapter?.notifyDataSetChanged()
                assignDriverAdapter?.notifyDataSetChanged()
            }

            when(receiverType){
                1->{

                }
                2->{
                    tvToolbar.text = "Select Customer"
                    assignUserAdapter = AssignUserAdapter(requireActivity(), tempUserList, object :
                        AssignUserAdapter.OnUserClick {
                        override fun onClick(position: Int) {
                            checkIfAllSelected(dialogBinding, tempUserList)
                        }

                    })
                    rvUser.adapter = assignUserAdapter
                }
                3->{
                    tvToolbar.text = "Select Seller"
                    assignSellerAdapter = AssignSellerAdapter(requireActivity(), tempSellerList, object :
                        AssignSellerAdapter.OnUserClick {
                        override fun onClick(position: Int) {
                            checkIfAllSelected(dialogBinding, tempSellerList)
                        }

                    })
                    rvUser.adapter = assignSellerAdapter

                }
                4->{
                    tvToolbar.text = "Select Affiliate"
                    assignAffiliateUserAdapter = AssignAffiliateUserAdapter(requireActivity(), tempAffiliateUserList, object :
                        AssignAffiliateUserAdapter.OnUserClick {
                        override fun onClick(position: Int) {
                            checkIfAllSelected(dialogBinding, tempAffiliateUserList)
                        }

                    })
                    rvUser.adapter = assignAffiliateUserAdapter

            }
                5->{
                    tvToolbar.text = "Select Driver"
                    assignDriverAdapter = AssignDeliveryBoyAdapter(requireActivity(), tempDriverList, object :
                        AssignDeliveryBoyAdapter.OnClick {
                        override fun onClick(position: Int) {
                            checkIfAllSelected(dialogBinding, tempDriverList)
                        }

                    })
                    rvUser.adapter = assignDriverAdapter
                }

            }


            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }

            btnSubmit.setOnClickListener {
                selectedReceiverList.clear()

                when (receiverType) {
                    2 -> {
                        userList.clear()
                        userList.addAll(tempUserList)
                        selectedReceiverList = ArrayList(
                            userList.filter { it.isSelected }
                                .map { it.id.toString() }
                        )
                    }
                    3 -> {
                        sellerList.clear()
                        sellerList.addAll(tempSellerList)
                        selectedReceiverList = ArrayList(
                            sellerList.filter { it.isSelected }
                                .map { it.id.toString() }
                        )
                    }
                    4 -> {
                        affiliateUserList.clear()
                        affiliateUserList.addAll(tempAffiliateUserList)
                        selectedReceiverList = ArrayList(
                            affiliateUserList.filter { it.isSelected }
                                .map { it.id.toString() }
                        )
                    }
                    5 -> {
                        driverList.clear()
                        driverList.addAll(tempDriverList)
                        selectedReceiverList = ArrayList(
                            driverList.filter { it.isSelected }
                                .map { it.id.toString() }
                        )
                    }
                    else -> {
                        userList.clear()
                        userList.addAll(tempUserList)
                        selectedReceiverList = ArrayList(
                            userList.filter { it.isSelected }
                                .map { it.id.toString() }
                        )
                    }
                }

                if (selectedReceiverList.isEmpty()) {
                    requireContext().showToast("Please select at least one user")
                    return@setOnClickListener
                }

                updateSelectedNames()

                dialog?.dismiss()
            }

        }

        dialog?.show()
    }

    private fun updateSelectedNames() {
        val selectedNames = when (receiverType) {
            2 -> userList.filter { it.isSelected }.map { it.fullName }
            3 -> sellerList.filter { it.isSelected }.map { it.first_name+" "+ it.last_name }
            4 -> affiliateUserList.filter { it.isSelected }.map { it.fullName}
            5 -> driverList.filter { it.isSelected }.map { it.fullNameEnglish }
            else -> emptyList()
        }

         if (selectedNames.isEmpty()) {
             binding?.tvSendType?.text =  "Select"
             binding?.tvSendType?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox_unselected,0,0,0)
        } else if (selectedNames.size == userList.size ||
            selectedNames.size == sellerList.size ||
            selectedNames.size == driverList.size) {
             binding?.tvSendType?.text = "All"
             binding?.tvSendType?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox,0,0,0)
        } else {
             binding?.tvSendType?.text =selectedNames.joinToString(", ")
             binding?.tvSendType?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox,0,0,0)
        }
    }

    private fun <T> checkIfAllSelected(
        binding: SelectNotificationUserDialogBinding,
        list: List<T>
    ) where T : Any {
        isAllSelected = when (list.firstOrNull()) {
            is GetUserResponse.Data.Data -> (list as List<GetUserResponse.Data.Data>).all { it.isSelected }
            is ApprovedSellerResponse.Data.Data -> (list as List<ApprovedSellerResponse.Data.Data>).all { it.isSelected }
            is GetAllDeliveryBoyResponse.Data.Data -> (list as List<GetAllDeliveryBoyResponse.Data.Data>).all { it.isSelected }
            else -> false
        }

        updateSelectAllUI(binding)
    }

    private fun updateSelectAllUI(binding: SelectNotificationUserDialogBinding) {
        if (isAllSelected) {
            binding.tvSelectAllUser.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_checkbox, 0, 0, 0
            )
        } else {
            binding.tvSelectAllUser.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_checkbox_unselected, 0, 0, 0
            )
        }
    }

    private fun validation(): Boolean {
        binding?.apply {

            if (etEnglishTitle.text.toString().trim().isEmpty()) {
                showToast("Please enter title in English")
                return false
            }

            if (etArabicTitle.text.toString().trim().isEmpty()) {
                showToast("Please enter title in Arabic")
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

            if (radioSendTo.checkedRadioButtonId == -1) {
                showToast("Please select receiver type")
                return false
            }

            if (selectedReceiverList.isEmpty()) {
                showToast("Please select at least one receiver")
                return false
            }
        }
        return true
    }

    private fun getAllUser(loading:Boolean=true){
        viewModel.getAllUser()
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }
    private fun getAffiliateUser(loading:Boolean=true){
        val map = HashMap<String, Any>()
        viewModel.getApprovedAffiliateUser(map)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }
    private fun getAllSeller(loading:Boolean=true){
        viewModel.hitApprovedSellerList()
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }
    private fun getDeliveryBoy(loading:Boolean=true){
        viewModel.getDeliveryBoy()
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }
}