package com.desynatadmin.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        }

        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        val navGraph = controller.navInflater.inflate(R.navigation.home_navigation_graph)
        controller.graph = navGraph
        bottonNavItemList.clear()
        bottonNavItemList.add(BottomNavItem("Home",R.drawable.ic_home,R.drawable.ic_selected_home))
        bottonNavItemList.add(BottomNavItem("Products",R.drawable.ic_product,R.drawable.ic_product_selected))
        bottonNavItemList.add(BottomNavItem("Streaming",R.drawable.ic_streaming,R.drawable.ic_streaming))
        bottonNavItemList.add(BottomNavItem("Plans",R.drawable.ic_plan,R.drawable.ic_plan_selected))
        bottonNavItemList.add(BottomNavItem("Payments",R.drawable.ic_payment,R.drawable.ic_payment_selected))

        setBottomAdapter()

        val bottomNavDestinations = setOf(
            R.id.home,
            R.id.productManagement,
            R.id.event,
            R.id.planList,
            R.id.paymentManagement,
        )

        controller.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id in bottomNavDestinations) {
                binding?.rvBottomNav?.isVisible = false
                binding?.viewBackground?.isVisible = true
            } else {
                binding?.rvBottomNav?.isVisible = false
                binding?.viewBackground?.isVisible = false
            }

            when (destination.id) {
                R.id.home -> {
                    bottomAdapter?.setSelectedItem(0)
                    enableDrawer(true)
                }
                R.id.productManagement -> {
                    bottomAdapter?.setSelectedItem(1)
                    enableDrawer(false)
                }
                R.id.event -> {
                    bottomAdapter?.setSelectedItem(2)
                    enableDrawer(false)
                }
                R.id.planList -> {
                    bottomAdapter?.setSelectedItem(3)
                    enableDrawer(false)
                }

                R.id.paymentManagement -> {
                    bottomAdapter?.setSelectedItem(4)
                    enableDrawer(false)
                }

                else ->{
                    enableDrawer(false)
                }
            }
        }


    }

    private fun setDrawerAdapter() {
        subAdminDrawerAdapter = SubAdminDrawerAdapter(this,subAdminDrawerList,object :
            SubAdminDrawerAdapter.ClickOnItem{
            override fun onClickItem(position: Int) {
                when(subAdminDrawerList[position].moduleDetails?.name){
                    "dashboard"->{
                        controller.navigate(R.id.home)
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
            Log.e("api", "observe: ${response.data}" )

            Log.e("api", "observe: ${response.data}" )
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
    fun setBottomAdapter(){
        bottomAdapter = BottomNavAdapter(this,bottonNavItemList, object :BottomNavAdapter.BottomNavItemClick{
            override fun onBottomNavItemClick(position: Int) {
                when(position) {
                    0 -> {
                        ProgressDialogUtils.getInstance().hideProgress()
                        controller.navigate(R.id.home)
                    }
                    1 ->{
                        ProgressDialogUtils.getInstance().hideProgress()
                        controller.navigate(R.id.productManagement)
                    }
                    2 -> controller.navigate(R.id.event)
                    3 -> {
                        ProgressDialogUtils.getInstance().hideProgress()
                        controller.navigate(R.id.planList)
                    }
                    4 -> {
                        ProgressDialogUtils.getInstance().hideProgress()
                        controller.navigate(R.id.paymentManagement)
                    }
                }
            }
        })
        binding?.rvBottomNav?.adapter = bottomAdapter
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
        drawerBinding.tvName.text = user?.fullName
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

