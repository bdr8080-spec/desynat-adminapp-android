package com.desynatadmin.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.adapter.BottomNavAdapter
import com.desynatadmin.adapter.SubAdminDrawerAdapter
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivitySubAdminHomeBinding
import com.desynatadmin.databinding.DrawerLayoutBinding
import com.desynatadmin.databinding.SubAdminDrawerLayoutBinding
import com.desynatadmin.ui.auth.AuthActivity
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.subAdminManagement.response.GetSubAdminDetailResponse
import com.desynatadmin.ui.m2.CountryCityZoneManaActivity
import com.desynatadmin.ui.m2.SellerManagementActivity
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubAdminHomeActivity : BaseActivity() {
    private var binding: ActivitySubAdminHomeBinding? =null
    private lateinit var controller: NavController
    var isOpenDrawer = false
    var bottonNavItemList = ArrayList<BottomNavItem>()
    private var bottomAdapter : BottomNavAdapter? =null
    private lateinit var drawerBinding: SubAdminDrawerLayoutBinding
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var userInfo: LoginResponse.Data?=null
    private var subAdminInfo: GetSubAdminDetailResponse.Data?=null

    private var subAdminDrawerList = ArrayList<GetSubAdminDetailResponse.Data.PermissionDetail>()
    private var subAdminDrawerAdapter: SubAdminDrawerAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySubAdminHomeBinding.inflate(layoutInflater)
        if (Constants.language == "ar") {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
        setContentView(binding?.root)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false

        initView()
        initControl()
        observer()
    }

    override fun initView() {

        binding?.apply {
            //  drawerBinding = DrawerLayoutBinding.bind(navigationView.getHeaderView(0))
            drawerBinding = SubAdminDrawerLayoutBinding.bind(
                binding!!.navigationView.findViewById(R.id.drawerRoot)
            )

        }

        setDrawerAdapter()

        userInfo = SharedPreference.get(this).getUser()
        if (userInfo?.type=="2"){
            setDrawerData(userInfo)
            getSubAdminDetail(userInfo?.id.toString())
            subAdminInfo = SharedPreference.get(this).getSubAdmin()
            val hasDashboard = subAdminInfo?.permissionDetails
                ?.any {
                    it.moduleDetails.name.equals("dashboard", ignoreCase = true)
                } == true
            if (!hasDashboard){
                openDrawer()
                hideBack(true)
            }
            else{
                hideBack(false)
            }
            if (subAdminInfo?.permissionDetails?.size!=0){
                subAdminDrawerList.clear()
                subAdminInfo?.permissionDetails?.let { subAdminDrawerList.addAll(it) }
                setDrawerAdapter()
            }

        }

        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        controller = navHostFragment.navController

        val navGraph = controller.navInflater.inflate(R.navigation.home_navigation_graph)

        navGraph.setStartDestination(R.id.home)

        controller.graph = navGraph

    }

    private fun setDrawerAdapter() {
        subAdminDrawerAdapter = SubAdminDrawerAdapter(this,subAdminDrawerList,object :
            SubAdminDrawerAdapter.ClickOnItem{
            override fun onClickItem(position: Int) {
                when(subAdminDrawerList[position].moduleDetails?.name){
                    "dashboard"->{
                        controller.navigate(R.id.home)
                        enableDrawer(true)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "brand_management"->{
                        controller.navigate(R.id.brand)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "category_management"->{
                        controller.navigate(R.id.category)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "seller_management"->{
                        startActivity(Intent(this@SubAdminHomeActivity, SellerManagementActivity::class.java))
                    }
                    "country_management"->{
                        startActivity(Intent(this@SubAdminHomeActivity, CountryCityZoneManaActivity::class.java))
                    }
                    "plan_management"->{
                        controller.navigate(R.id.planList)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "user_management"->{
                        controller.navigate(R.id.user)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "affiliate_user_management"->{
                        controller.navigate(R.id.affiliateUserManagement)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "delivery_boy_management"->{
                        controller.navigate(R.id.deliveryBoy)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "reason_management"->{
                        controller.navigate(R.id.reason)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "shipping_management"->{
                        controller.navigate(R.id.shippingTax)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "gift_card_management"->{
                        controller.navigate(R.id.giftCard)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "banner_per_day_price_management"->{
                        controller.navigate(R.id.bannerPerDayPrice)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "promo_code_management"->{
                        controller.navigate(R.id.promoCode)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "order_management"->{
                        controller.navigate(R.id.order)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "affiliate_commission_management"->{
                        controller.navigate(R.id.affiliateCommission)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "banner_request_management"->{
                        controller.navigate(R.id.bannerRequest)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "live_request_management"->{
                        controller.navigate(R.id.liveRequestTimeSlot)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "story_management"->{
                        controller.navigate(R.id.storyManagement)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "deal_management"->{
                        controller.navigate(R.id.dealManagement)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "full_screen_banner_management"->{
                        controller.navigate(R.id.fullScreenBanner)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "product_management"->{
                        controller.navigate(R.id.productManagement)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "affiliate_withdraw_request_management"->{
                        controller.navigate(R.id.withdrawalRequest)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "cms_management"->{
                        controller.navigate(R.id.cmsManagement)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "notification_management"->{
                        controller.navigate(R.id.notification)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "ticket_management"->{
                        controller.navigate(R.id.ticketManagement)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "payment_management"->{
                        controller.navigate(R.id.paymentManagement)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "seller_livestream_management"->{
                        controller.navigate(R.id.event)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "support_chat"->{
                        controller.navigate(R.id.supportChat)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "account_setting"->{
                        controller.navigate(R.id.adminProfile)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "sub_admin_management"->{
                        controller.navigate(R.id.subAdminManagement)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                    "refund_management"->{
                        controller.navigate(R.id.refundManagement)
                        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                    }
                }
            }

        })
        drawerBinding?.rvDrawer?.adapter = subAdminDrawerAdapter
    }

    override fun initControl() {
        binding?.apply {


        }
    }

    override fun observer() {
            viewModel.error.observe(this) { errorMessage ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()

                    isObserver = false
                }
            }

        viewModel.getHomeSubAdminDetailResult.observe(this) { response ->
            Log.e("api", "observe: ${response.data}")
            ProgressDialogUtils.getInstance().hideProgress()
            SharedPreference.get(this).saveSubAdmin(response.data)
            setUpDrawerContent()
            setDrawerContentAdapter()


        }

            viewModel.logoutResult.observe(this) { response ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    response?.let {
                        ProgressDialogUtils.getInstance().hideProgress()

                        Constants.isLogout = true
                        startActivity(Intent(this, AuthActivity::class.java))
                        SharedPreference.get(this).accessToken =""
                        finishAffinity()
                    }
                    isObserver = false
                }
            }
        }

    fun openDrawer(){
        isOpenDrawer = true
        binding?.drawerLayout?.openDrawer(GravityCompat.START)
    }

    fun hideBack(show: Boolean=false){
        drawerBinding?.ivBack?.isInvisible = show
    }

    override fun onBackPressed() {
        subAdminInfo = SharedPreference.get(this).getSubAdmin()

        val hasDashboard = subAdminInfo?.permissionDetails
            ?.any {
                it.moduleDetails.name.equals("dashboard", ignoreCase = true)
            } == true

        val isDrawerOpen = binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true

        Log.e("ehdf", "onBackPressed: $isOpenDrawer   $isDrawerOpen")

        if (isOpenDrawer && !isDrawerOpen) {
            super.onBackPressed()
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
            return
        }

        if (isDrawerOpen) {
            if (hasDashboard) {
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                isOpenDrawer = false
            } else {
                isOpenDrawer = false
                super.onBackPressed()
            }
            return
        }

        super.onBackPressed()
    }

    fun setDrawerData(user: LoginResponse.Data?) {
        drawerBinding.tvName.text = if (Constants.language=="ar") user?.fullNameAr?:user?.fullName else user?.fullName
        drawerBinding.tvEmail.text = user?.email

        Glide.with(this).load(user?.profilePic).placeholder(R.drawable.ic_user).into(drawerBinding.ivProfileImage)

    }

     fun setUpDrawerContent(){
         Log.e("api", "observe: setUpDrawerContent" )
        drawerBinding.apply {
            ivProfileImage.setOnClickListener {
                if (subAdminInfo?.profilePic!=null){
                    CommonMethod.imagePreviewDialog(this@SubAdminHomeActivity,subAdminInfo?.profilePic.toString())
                }
            }

            ivBack.setOnClickListener {
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            ivEdit.setOnClickListener {
                controller.navigate(R.id.adminProfile)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            relLogout.setOnClickListener {
                viewModel.logout()
                isObserver = true
                ProgressDialogUtils.getInstance().showProgress(this@SubAdminHomeActivity, false)
            }

        }
    }
    private fun enableDrawer(enable: Boolean) {
        if (enable) {
            binding?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            binding?.navigationView?.isVisible = true
        } else {
            binding?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            binding?.navigationView?.isVisible = false
        }
    }


    override fun onResume() {
        super.onResume()

//        SharedPreference.get(this).badgeCount = 0
//        ShortcutBadger.removeCount(this)
        //   NotificationManagerCompat.from(this).cancelAll()
    }

    fun setDrawerContentAdapter(){
        setDrawerAdapter()
        Log.e("api", "observe: setDrawerAdapter" )

        userInfo = SharedPreference.get(this).getUser()
        if (userInfo?.type=="2"){
            subAdminInfo = SharedPreference.get(this).getSubAdmin()
            subAdminDrawerList.clear()
            subAdminInfo?.permissionDetails?.let { subAdminDrawerList.addAll(it) }
            subAdminDrawerAdapter?.notifyDataSetChanged()

            val module = subAdminInfo?.permissionDetails
                ?.firstOrNull {
                    it.moduleDetails.name.equals("account_setting", true)
                }

            val actionAccess = module?.canAction == 1
        //    drawerBinding?.ivEdit?.isVisible = actionAccess

//            drawerBinding?.ivProfileImage?.setOnClickListener {
//                controller.navigate(R.id.adminProfile)
//                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
//            }

        }
    }

    private fun getSubAdminDetail(id: String){
        val map = HashMap<String,Any>()
        map["id"] = id
        viewModel.getHomeSubAdminDetail(map)

    }

}

