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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.desynatadmin.ui.home.notificationManagement.response.GetAssignAffiliateUserResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetNewAffiliateUserResponse
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.ui.home.notificationManagement.response.AssignSellerResponse
import com.desynatadmin.ui.home.notificationManagement.response.AssignUserResponse
import com.desynatadmin.ui.home.notificationManagement.response.GetAssignDeliveryBoyResponse
import com.desynatadmin.ui.home.notificationManagement.response.SendNotificationRequest
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SendNewNotificationFragment : BaseFragment(){
    private var dialogBinding: SelectNotificationUserDialogBinding?=null
    private var binding: FragmentSendNewNotificationBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isAffiliateObserver = true
    private var isDriverObserver = true
    private var isSellerObserver = true
    private var assignUserAdapter: AssignUserAdapter?=null
    private var assignDriverAdapter: AssignDeliveryBoyAdapter?=null
    private var assignAffiliateUserAdapter: AssignAffiliateUserAdapter?=null
    private var assignSellerAdapter: AssignSellerAdapter?=null
    private var userList = ArrayList<AssignUserResponse.Data>()
    private var affiliateUserList = ArrayList<GetAssignAffiliateUserResponse.Data>()
    private var driverList = ArrayList<GetAssignDeliveryBoyResponse.Data>()
    private var sellerList = ArrayList<AssignSellerResponse.Data>()
    private var dialog: Dialog?=null
    private var receiverType = 0 //1-admin,2-user,3-seller,4-affiliate,5-driver
    private var senderType = 1

    private var isAllSelected = false
    private var selectedReceiverList = ArrayList<String>()

    private var tempUserList = ArrayList<AssignUserResponse.Data>()
    private var tempSellerList = ArrayList<AssignSellerResponse.Data>()
    private var tempDriverList = ArrayList<GetAssignDeliveryBoyResponse.Data>()
    private var tempAffiliateUserList = ArrayList<GetAssignAffiliateUserResponse.Data>()

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
            toolbar.tvToolbar.text = getString(R.string.send_new)

            CommonMethod.applyArabicOnlyFilter(etArabicTitle)
            CommonMethod.applyArabicOnlyFilter(etArabicDescription)
            CommonMethod.applyEnglishOnlyFilter(etEnglishTitle)
            CommonMethod.applyEnglishOnlyFilter(etEnglishDescription)
//            CommonMethod.removeSpace(etEnglishTitle)
//            CommonMethod.removeSpace(etArabicDescription)
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
                if (receiverType!= 6){
                    selectUserDialog()
                }
            }

            radioSendTo.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbAll -> {
                        receiverType = 6
                        tvSendType.isVisible = true
                        isAllSelected = true
                        tvSendType.text = getString(R.string.all)
                        tvSendType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox,0,0,0)
                        if (userList.isEmpty()){
                            getAllUser()
                        }
                        else{
                            userList.forEach { it.isSelected = true }
                            assignUserAdapter?.notifyDataSetChanged()

                        }
                        if (sellerList.isEmpty()){
                            getAllSeller()
                        }
                        else{
                            sellerList.forEach { it.isSelected = true }
                            assignSellerAdapter?.notifyDataSetChanged()
                        }
                        if (affiliateUserList.isEmpty()){
                            getAffiliateUser()
                        }
                        else{
                            affiliateUserList.forEach { it.isSelected = true }
                            assignAffiliateUserAdapter?.notifyDataSetChanged()
                        }
                        if (driverList.isEmpty()){
                            getDeliveryBoy()
                        }
                        else{
                            driverList.forEach { it.isSelected = true }
                            assignDriverAdapter?.notifyDataSetChanged()
                        }
                    }

                    R.id.rbCustomer -> {
                        receiverType = 2
                        tvSendType.isVisible = true

                        isAllSelected = userList.all { it.isSelected }

                        if (userList.isEmpty()){
                            getAllUser()
                        }

                        updateSelectedNames()
                    }

                    R.id.rbSeller -> {
                        receiverType = 3
                        isAllSelected = sellerList.all { it.isSelected }
                        tvSendType.isVisible = true
                        if (sellerList.isEmpty()){
                            getAllSeller()
                        }
                        updateSelectedNames()
                    }
                    R.id.rbAffiliate -> {
                        receiverType = 4
                        isAllSelected = affiliateUserList.all { it.isSelected }
                        tvSendType.isVisible = true
                        if (affiliateUserList.isEmpty()){
                            getAffiliateUser()
                        }
                        updateSelectedNames()
                    }
                    R.id.rbDriver -> {
                        receiverType = 5
                        isAllSelected = driverList.all { it.isSelected }
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

        viewModel.getAssignUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                    userList.clear()
                userList.addAll(response.data)
                if (receiverType == 6) {
                    userList.forEach { it.isSelected = true }
                }
                isObserver = false
            }
        }

        viewModel.assignSellerListResult.observe(viewLifecycleOwner) { response ->
            if (isSellerObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                    sellerList.clear()
                sellerList.addAll(response.data)
                if (receiverType == 6) {
                    sellerList.forEach { it.isSelected = true }
                }
                isSellerObserver = false
            }
        }

        viewModel.assignDeliveryBoyResult.observe(viewLifecycleOwner) { response ->
            if (isDriverObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                    driverList.clear()
                driverList.addAll(response.data)
                if (receiverType == 6) {
                    driverList.forEach { it.isSelected = true }
                }
                isDriverObserver = false
            }
        }

        viewModel.getAssignAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isAffiliateObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                    affiliateUserList.clear()
                affiliateUserList.addAll(response.data)
                if (receiverType == 6) {
                    affiliateUserList.forEach { it.isSelected = true }
                }
                isAffiliateObserver = false
            }
        }
    }

    private fun selectUserDialog() {
        val gson = Gson()
        tempUserList.clear()
        tempUserList.addAll(
            userList.map {
                gson.fromJson(gson.toJson(it), AssignUserResponse.Data::class.java)
            }
        )


        tempSellerList.clear()
        tempSellerList.addAll(
            sellerList.map {
                gson.fromJson(gson.toJson(it), AssignSellerResponse.Data::class.java)
            }
        )


        tempAffiliateUserList.clear()
        tempAffiliateUserList.addAll(
            affiliateUserList.map {
                gson.fromJson(gson.toJson(it), GetAssignAffiliateUserResponse.Data::class.java)
            }
        )


        tempDriverList.clear()
        tempDriverList.addAll(
            driverList.map {
                gson.fromJson(gson.toJson(it), GetAssignDeliveryBoyResponse.Data::class.java)
            }
        )

        isAllSelected = when (receiverType) {
            2 -> tempUserList.all { it.isSelected }
            3 -> tempSellerList.all { it.isSelected }
            4 -> tempAffiliateUserList.all { it.isSelected }
            5 -> tempDriverList.all { it.isSelected }
            else -> false
        }

        dialog = Dialog(requireActivity(), R.style.CustomDialog)
        dialogBinding = SelectNotificationUserDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding?.root.let { it?.let { it1 -> dialog?.setContentView(it1) } }

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

        dialogBinding?.apply {

            val layoutManager = rvUser.layoutManager as LinearLayoutManager
            updateSelectAllUI(dialogBinding!!)

//            tvSelectAllUser.setOnClickListener {
//
//                isAllSelected = !isAllSelected
//
//                when (receiverType) {
//                    2 -> userList.forEach { it.isSelected = isAllSelected }
//                    3 -> sellerList.forEach { it.isSelected = isAllSelected }
//                    4 -> affiliateUserList.forEach { it.isSelected = isAllSelected }
//                    5 -> driverList.forEach { it.isSelected = isAllSelected }
//                }
//
//                updateSelectAllUI(dialogBinding)
//                assignUserAdapter?.notifyDataSetChanged()
//                assignSellerAdapter?.notifyDataSetChanged()
//                assignDriverAdapter?.notifyDataSetChanged()
//                assignAffiliateUserAdapter?.notifyDataSetChanged()
//            }

            tvSelectAllUser.setOnClickListener {

                isAllSelected = !isAllSelected

                when (receiverType) {
                    2 -> tempUserList.forEach { it.isSelected = isAllSelected }
                    3 -> tempSellerList.forEach { it.isSelected = isAllSelected }
                    4 -> tempAffiliateUserList.forEach { it.isSelected = isAllSelected }
                    5 -> tempDriverList.forEach { it.isSelected = isAllSelected }
                }

                updateSelectAllUI(dialogBinding!!)

                assignUserAdapter?.notifyDataSetChanged()
                assignSellerAdapter?.notifyDataSetChanged()
                assignDriverAdapter?.notifyDataSetChanged()
                assignAffiliateUserAdapter?.notifyDataSetChanged()
            }

            when(receiverType){
                1->{

                }
                2->{
                    tvToolbar.text = getString(R.string.select_customer)
                    assignUserAdapter = AssignUserAdapter(requireActivity(), tempUserList, object :
                        AssignUserAdapter.OnUserClick {
                        override fun onClick(position: Int) {
                            checkIfAllSelected(dialogBinding!!, tempUserList)
                        }

                    })
                    rvUser.adapter = assignUserAdapter
                }
                3->{
                    tvToolbar.text = getString(R.string.select_seller)
                    assignSellerAdapter = AssignSellerAdapter(requireActivity(), tempSellerList, object :
                        AssignSellerAdapter.OnUserClick {
                        override fun onClick(position: Int) {
                            checkIfAllSelected(dialogBinding!!, tempSellerList)
                        }

                    })
                    rvUser.adapter = assignSellerAdapter

                }
                4->{
                    tvToolbar.text = getString(R.string.select_affiliate)
                    assignAffiliateUserAdapter = AssignAffiliateUserAdapter(requireActivity(), tempAffiliateUserList, object :
                        AssignAffiliateUserAdapter.OnUserClick {
                        override fun onClick(position: Int) {
                            checkIfAllSelected(dialogBinding!!, tempAffiliateUserList)
                        }

                    })
                    rvUser.adapter = assignAffiliateUserAdapter

            }
                5->{
                    tvToolbar.text = getString(R.string.select_driver)
                    assignDriverAdapter = AssignDeliveryBoyAdapter(requireActivity(), tempDriverList, object :
                        AssignDeliveryBoyAdapter.OnClick {
                        override fun onClick(position: Int) {
                            checkIfAllSelected(dialogBinding!!, tempDriverList)
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
                    requireContext().showToast(getString(R.string.please_select_at_least_one_user))
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
            5 -> driverList.filter { it.isSelected }.map { if (Constants.language=="ar") it?.fullNameArabic?:it?.fullNameEnglish else it.fullNameEnglish }
            else -> emptyList()
        }

         if (selectedNames.isEmpty()&&receiverType!=6) {
             binding?.tvSendType?.text = getString(R.string.select)
             binding?.tvSendType?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox_unselected,0,0,0)
        }
         else if(receiverType==6){
             binding?.tvSendType?.text = getString(R.string.all)
             binding?.tvSendType?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox,0,0,0)
         }
         else if (
             (receiverType == 2 && selectedNames.size == userList.size) ||
             (receiverType == 3 && selectedNames.size == sellerList.size) ||
             (receiverType == 4 && selectedNames.size == affiliateUserList.size) ||
             (receiverType == 5 && selectedNames.size == driverList.size)
         ) {
             binding?.tvSendType?.text = getString(R.string.all)
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
            is GetAssignAffiliateUserResponse.Data -> (list as List<GetAssignAffiliateUserResponse.Data>).all { it.isSelected }
            else -> false
        }

        updateSelectAllUI(binding)
    }

    private fun updateSelectAllUI(binding: SelectNotificationUserDialogBinding) {
        if (isAllSelected) {
            binding.tvSelectAllUser.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_checkbox, 0, 0, 0
            )
        }
         else {
            binding.tvSelectAllUser.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_checkbox_unselected, 0, 0, 0
            )
        }
    }

    private fun validation(): Boolean {
        binding?.apply {

            if (etEnglishTitle.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_title_in_english))
                return false
            }

            if (etArabicTitle.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_title_in_arabic))
                return false
            }

            if (etEnglishDescription.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_description_in_english))
                return false
            }

            if (etArabicDescription.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_description_in_arabic))
                return false
            }

            if (radioSendTo.checkedRadioButtonId == -1) {
                showToast(getString(R.string.please_select_receiver_type))
                return false
            }

            if (selectedReceiverList.isEmpty()&&receiverType!=6) {
                showToast(getString(R.string.please_select_at_least_one_receiver))
                return false
            }
        }
        return true
    }

    private fun getAllUser(loading:Boolean=true){
        val map = HashMap<String,Any>()
//        map["page"] = currentPage
//        map["limit"] = 10
        viewModel.getAssignUser()
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }
    private fun getAffiliateUser(loading:Boolean=true){
        val map = HashMap<String,Any>()
//        map["page"] = currentPage
//        map["limit"] = 10
        viewModel.getAssignAffiliateUser(map)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isAffiliateObserver = true
    }
    private fun getAllSeller(loading:Boolean=true){

      //  viewModel.hitApprovedSellerList(page = currentPage.toString())
        viewModel.assignSellerList( )
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isSellerObserver = true
    }
    private fun getDeliveryBoy(loading:Boolean=true){
        val map = HashMap<String,Any>()
//        map["page"] = currentPage
//        map["limit"] = 10
     //   viewModel.getDeliveryBoy(page = currentPage.toString())
        viewModel.getAssignDeliveryBoy()
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isDriverObserver = true
    }
}