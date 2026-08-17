package com.desynatadmin.ui.home.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ChangeLanguageDialogBinding
import com.desynatadmin.databinding.FragmentHomeBinding
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.fragment.response.GetDashboardResponse
import com.desynatadmin.ui.home.subAdminManagement.response.GetSubAdminDetailResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import me.leolin.shortcutbadger.ShortcutBadger
import kotlin.text.get

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var isDashboardObserver = false
    private var isCountryObserver = false
    private var isSubAdminObserver = false
    private var isNotificationObserver = false
    private var isProfileObserver = false
    var user : LoginResponse.Data?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var userInfo: LoginResponse.Data?=null
    private var subAdminInfo: GetSubAdminDetailResponse.Data?=null
    private var dashboardData: GetDashboardResponse.Data?=null

    var fromDate = ""
    var toDate = ""
    var fromTimestamp=0L
    var toTimestamp=0L
    var filter = "1"
    var countryId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        user = SharedPreference.get(requireActivity()).getUser()
        setData()
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.mainProgressBar?.isVisible = false
        getNotificationCount()
        getProfile()
        getDashBoardData()
        if(SharedPreference.get(requireActivity()).getCountryList() == null) {
            getCountryApi()
        }
        else{
            countryList.clear()
            SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
        }

        userInfo = SharedPreference.get(requireActivity()).getUser()
        if (userInfo?.type=="2"){
            getSubAdminDetail(userInfo?.id.toString())
            subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
            val hasDashboard = subAdminInfo?.permissionDetails
                ?.any {
                    it.moduleDetails.name.equals("dashboard", ignoreCase = true)
                } == true
            if (!hasDashboard){
                when (val activity = requireActivity()) {
                    is HomeActivity -> activity.openDrawer()
                    is SubAdminHomeActivity -> activity.openDrawer()
                    is SubAdminHomeActivity -> activity.hideBack(true)
                }
            }
            else{
                when (val activity = requireActivity()) {
                    is SubAdminHomeActivity -> activity.hideBack(false)
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()){date,timestamp->
                    etFrom.setText(date)
                    fromTimestamp=timestamp
                }

            }
            etTo.setOnClickListener {
                if (etFrom.text?.isNotEmpty() == true){
                openCustomDatePicker(requireActivity(), minDate = fromTimestamp){date,timestamp->
                    etTo.setText(date)
                    toTimestamp=timestamp
                    getDashBoardData()
                }
                    }
                else{
                    showToast(getString(R.string.please_select_start_date_first))
                }

            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbToday ->{
                        filter = "1"
                        getDashBoardData()
                    }

                    R.id.rbWeek ->{
                        filter = "2"
                        getDashBoardData()
                    }

                    R.id.rbMonth ->{
                        filter = "3"
                        getDashBoardData()
                    }

                    R.id.rbYear ->{
                        filter = "4"
                        getDashBoardData()
                    }

                }

            }


            ivProfileImage.setOnClickListener {
                when (val activity = requireActivity()) {
                    is HomeActivity -> activity.openDrawer()
                    is SubAdminHomeActivity -> activity.openDrawer()
                }
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(requireActivity(),etCountry,countryList, isDialCode = false){selectedCountry ->
                    etCountry.setText(if (Constants.language=="ar") selectedCountry.nameAr else selectedCountry.name)
                    countryId = selectedCountry.id.toString()
                    getDashBoardData()
                }
            }

            ivNotification.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_my_notificationFragment)
            }

            ivLanguage.setOnClickListener {
                changeLanguageDialog()
            }

            btnExport.setOnClickListener {
                val csv = CommonMethod.objectToCsv(dashboardData.toString())
                CommonMethod.saveCsvFile(requireContext(), "dashboard_${System.currentTimeMillis()}", csv)
            }
        }
    }

    private fun getCountryApi(){
        binding?.mainProgressBar?.isVisible = true
        viewModel.getCountry()
    }
private fun getNotificationCount(){
        viewModel.getNotificationCount()
    isNotificationObserver = true
    }

private fun getProfile(){
        viewModel.getProfile()
    isProfileObserver = true
    }

    private fun setCountry() {
        if (!countryList.isNullOrEmpty()) {
            SharedPreference.get(requireActivity()).saveCountryList(countryList)
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                ProgressDialogUtils.getInstance().hideProgress()
            binding?.mainProgressBar?.isVisible = false
                isObserver = false
            isDashboardObserver = false
        }
        viewModel.getSubAdminDetailResult.observe(viewLifecycleOwner) { response ->
            Log.e("api", "observe: ${isSubAdminObserver}" )

                Log.e("api", "observe: ${response.data}" )
                ProgressDialogUtils.getInstance().hideProgress()
                SharedPreference.get(requireActivity()).saveSubAdmin(response.data)
                when (val activity = requireActivity()) {
                    is SubAdminHomeActivity -> {
                        activity.setUpDrawerContent()
                        activity.setDrawerContentAdapter()
                    }
                }

        }

        viewModel.getHomeDashBoardResult.observe(viewLifecycleOwner) { response ->
            if (isDashboardObserver) {
                Log.e("api", "observe: ${isDashboardObserver}")
                binding?.mainProgressBar?.isVisible = false
                ProgressDialogUtils.getInstance().hideProgress()
                dashboardData = response.data
                setDashboardData()
                isDashboardObserver = false
            }
        }

        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
                ProgressDialogUtils.getInstance().hideProgress()
            binding?.mainProgressBar?.isVisible = false
                countryList.clear()
                countryList.addAll(response.data)
                setCountry()
        }

        viewModel.getProfileResult.observe(viewLifecycleOwner) { response ->
            if (isProfileObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                user = response.data
                SharedPreference.get(requireActivity()).saveUser(response.data)
                when (val activity = requireActivity()) {
                    is HomeActivity -> activity.setDrawerData(SharedPreference.get(requireActivity()).getUser())
                    is SubAdminHomeActivity -> activity.setDrawerData(SharedPreference.get(requireActivity()).getUser())
                }
                setData()
                isProfileObserver = false
            }
        }

        viewModel.getNotificationCountResult.observe(viewLifecycleOwner) { response ->
            if (isNotificationObserver){
                ProgressDialogUtils.getInstance().hideProgress()
                if (response.data!=0){
                    setNotificationCount(response.data?:0)
                }
                else{
                    SharedPreference.get(requireActivity()).badgeCount = 0
                    ShortcutBadger.removeCount(requireActivity())

                }
                isNotificationObserver = false
            }

        }
    }

    private fun changeLanguageDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ChangeLanguageDialogBinding.inflate(LayoutInflater.from(context))
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

            Constants.language = SharedPreference.get(requireActivity()).language
            if(Constants.language=="en"){
                cbEnglish.isChecked = true
                cbArabic.isChecked = false
            }
            else if(Constants.language=="ar"){
                cbEnglish.isChecked = false
                cbArabic.isChecked = true
            }

            closeIv.setOnClickListener {
                dialog.dismiss()
            }

            cbArabic.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbEnglish.isChecked = false
                }
                else {
                    if (!cbEnglish.isChecked) {
                        cbArabic.isChecked = true
                    }
                }
            }

            cbEnglish.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbArabic.isChecked = false
                }
                else {
                    if (!cbArabic.isChecked) {
                        cbEnglish.isChecked = true
                    }
                }
            }

            btnSave.setOnClickListener {
                if (cbArabic.isChecked) {
                    Constants.language = Constants.ARABIC
                }
                else if (cbEnglish.isChecked) {
                    Constants.language = Constants.ENGLISH
                }

                if (Constants.language==""){
                    showToast(getString(R.string.please_select_language))
                }
                else{
                    SharedPreference.get(requireActivity()).language = Constants.language
                    CommonMethod.setLocale(requireActivity(), Constants.language)

                   /* val intent = Intent(requireActivity(), if (requireActivity() is SubAdminHomeActivity)SubAdminHomeActivity::class.java else HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()*/
                    dialog.dismiss()
                    requireActivity().recreate()
                }
            }
        }

        dialog.show()
    }

    private fun setNotificationCount(data: Int) {
        binding?.relCount?.isVisible = true
        binding?.tvCount?.text = data.toString()
        ShortcutBadger.applyCount(requireActivity(), data)
    }

    private fun setData(){
        binding?.apply {
            tvName.text = "${getString(R.string.hi)} ${user?.fullName}"
            Glide.with(requireActivity()).load(user?.profilePic).placeholder(R.drawable.ic_user).into(ivProfileImage)
        }
    }

    private fun getSubAdminDetail(id: String){
        val map = HashMap<String,Any>()
        map["id"] = id
        viewModel.getSubAdminDetail(map)
        isSubAdminObserver = true

    }

    private fun setDashboardData(){
        Log.e("TAG", "setDashboardData: $dashboardData", )
        binding?.apply {
            tvTotalRevenue.text = "${CommonMethod.formatAmount(dashboardData?.totalRevenue.toString())} KD"
            tvByOrderPrice.text = "${CommonMethod.formatAmount(dashboardData?.totalOrderRevenue.toString())} KD"
            tvBySubscriptionPrice.text = "${CommonMethod.formatAmount(dashboardData?.totalSubscriptionPlanRevenue.toString())} KD"
            tvByAffiliateOrderPrice.text = "${CommonMethod.formatAmount(dashboardData?.totalAffiliateOrdersRevenue.toString())} KD"
            tvByBannerPrice.text = "${CommonMethod.formatAmount(dashboardData?.totalBannerRevenue.toString())} KD"

            tvSellerInActiveUser.text = dashboardData?.totalInactiveSellers.toString()
            tvSellerActiveUser.text = dashboardData?.totalActiveSellers.toString()
            tvTotalSeller.text = "${getString(R.string.total_seller_registered)} (${dashboardData?.totalSellers?:0})"

            tvAffiliatedInActiveUser.text = dashboardData?.totalInactiveAffiliateUsers.toString()
            tvAffiliatedActive.text = dashboardData?.totalActiveAffiliateUsers.toString()
            tvTotalAffiliated.text = "${getString(R.string.total_affiliated_registered)} (${dashboardData?.totalAffiliateUsers?:0})"

            tvInActiveUser.text = dashboardData?.totalInactiveUsers.toString()
            tvActiveUser.text = dashboardData?.totalActiveUsers.toString()
            tvTotalUser.text = "${getString(R.string.total_users_registered)} (${dashboardData?.totalUsers?:0})"

            tvDriveInActiveUser.text = dashboardData?.totalInactiveDeliveryBoy.toString()
            tvDriveInActive.text = dashboardData?.totalActiveDeliveryBoy.toString()
            tvTotalDriver.text = "${getString(R.string.total_drivers_registered)} (${dashboardData?.totalDeliveryBoy?:0})"


            tvCancelledOrder.text = dashboardData?.totalCancelledOrders.toString()
            tvCompletedOrder.text = dashboardData?.totalCompletedOrders.toString()
            tvOngoingOrder.text = dashboardData?.totalOngoingOrders.toString()
            tvNewOrder.text = dashboardData?.totalReturnedOrders.toString()
            tvOrder.text = "${getString(R.string.total_orders)} (${dashboardData?.totalOrders?:0})"

            tvWebsiteUser.text = dashboardData?.totalWebsiteVisitorUsers.toString()
            tvAppUser.text = dashboardData?.totalAppVisitorUsers.toString()
            tvTotalOnlineUser.text = "${getString(R.string.total_online_users)} (${dashboardData?.totalOnlineUsers?:0})"

            tvTotalTickets.text = "${getString(R.string.total_tickets)} (${dashboardData?.totalTickets?:0})"
            tvResolvedTicket.text = dashboardData?.totalResolvedTickets.toString()
            tvUnResolvedTicket.text = dashboardData?.totalUnresolvedTickets.toString()
            tvNewTicketCount.text = dashboardData?.totalTickets.toString()
        }
    }

    private fun getDashBoardData(){
        val map = HashMap<String, Any>()
        if (fromDate!=""&&toDate!=""){
            map["from_date"] = CommonMethod.formatDate(binding?.etFrom?.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
            map["to_date"] = CommonMethod.formatDate(binding?.etTo?.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
        }
        if (filter!=""){
            map["filter"] = filter
        }
        if (countryId!="") {
            map["country_id"] = countryId
        }
        viewModel.getHomeDashBoard(map)
        isDashboardObserver = true
      //  binding?.mainProgressBar?.isVisible = true
    }

}