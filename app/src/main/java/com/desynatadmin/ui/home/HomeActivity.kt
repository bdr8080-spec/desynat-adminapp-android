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
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import coil.load
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.adapter.BottomNavAdapter
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityHomeBinding
import com.desynatadmin.databinding.DrawerLayoutBinding
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
import me.leolin.shortcutbadger.ShortcutBadger
import kotlin.collections.get

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private var binding: ActivityHomeBinding? =null
    private lateinit var controller: NavController
    var isOpenDrawer = false
    var bottonNavItemList = ArrayList<BottomNavItem>()
    private var bottomAdapter : BottomNavAdapter? =null
    private lateinit var drawerBinding: DrawerLayoutBinding
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var userInfo: LoginResponse.Data?=null
    private var subAdminInfo: GetSubAdminDetailResponse.Data?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
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

        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)


        binding?.apply {
            drawerBinding = DrawerLayoutBinding.bind(
                binding!!.navigationView.findViewById(R.id.drawerRoot)
            )
            setDrawerData(SharedPreference.get(this@HomeActivity).getUser())
            setUpDrawerContent()
        }


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        val navGraph = controller.navInflater.inflate(R.navigation.home_navigation_graph)
        controller.graph = navGraph
        bottonNavItemList.clear()
        bottonNavItemList.add(BottomNavItem(getString(R.string.home),R.drawable.ic_home,R.drawable.ic_selected_home))
        bottonNavItemList.add(BottomNavItem(getString(R.string.products),R.drawable.ic_product,R.drawable.ic_product_selected))
        bottonNavItemList.add(BottomNavItem(getString(R.string.streaming),R.drawable.ic_streaming,R.drawable.ic_streaming))
        bottonNavItemList.add(BottomNavItem(getString(R.string.plans),R.drawable.ic_plan,R.drawable.ic_plan_selected))
        bottonNavItemList.add(BottomNavItem(getString(R.string.payments),R.drawable.ic_payment,R.drawable.ic_payment_selected))

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
                binding?.rvBottomNav?.isVisible = true
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

override fun onBackPressed() {
    Log.e("ehdf", "onBackPressed: $isOpenDrawer   ${binding?.drawerLayout?.isDrawerOpen(GravityCompat.START)}")
    if (isOpenDrawer == true&&binding?.drawerLayout?.isDrawerOpen(GravityCompat.START)==false){
        binding?.drawerLayout?.openDrawer(GravityCompat.START,false)
        super.onBackPressed()
    }
    else if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
        isOpenDrawer = false
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
    } else {
        super.onBackPressed()
    }
}
    fun setDrawerData(user: LoginResponse.Data?) {
        Log.e("api", "observe: $user" )

        drawerBinding?.tvName?.text = user?.fullName
        drawerBinding?.tvEmail?.text = user?.email

        drawerBinding?.ivProfileImage?.let { Glide.with(this).load(user?.profilePic).placeholder(R.drawable.ic_user).into(it) }

    }

    private fun setUpDrawerContent(){
        drawerBinding.apply {
            ivProfileImage.setOnClickListener {
                val user = SharedPreference.get(this@HomeActivity).getUser()
                if (user?.profilePic!=null){
                    CommonMethod.imagePreviewDialog(this@HomeActivity,user?.profilePic.toString())
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
                ProgressDialogUtils.getInstance().showProgress(this@HomeActivity, false)
            }

            rlCategoryManagement.setOnClickListener {
                controller.navigate(R.id.category)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlBrandManagement.setOnClickListener {
                controller.navigate(R.id.brand)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlUserManagement.setOnClickListener {
                controller.navigate(R.id.user)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlDeliveryBoyManagement.setOnClickListener {
                controller.navigate(R.id.deliveryBoy)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlReasonManagement.setOnClickListener {
                controller.navigate(R.id.reason)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }
//            rlSellerStoryRequest.setOnClickListener {
//                controller.navigate(R.id.bannerRequestManagement1Fragment)
//                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
//            }

            rlCountryCityManagement.setOnClickListener {
                startActivity(Intent(this@HomeActivity, CountryCityZoneManaActivity::class.java))
            }

            rlSellereManagment.setOnClickListener {
                startActivity(Intent(this@HomeActivity, SellerManagementActivity::class.java))
            }

            rlPromoCodeManagement.setOnClickListener {
                controller.navigate(R.id.promoCode)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlGiftCard.setOnClickListener {
                controller.navigate(R.id.giftCard)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlShippingManagement.setOnClickListener {
                controller.navigate(R.id.shippingTax)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlBannerPerDayPrice.setOnClickListener {
                controller.navigate(R.id.bannerPerDayPrice)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlBannerRequest.setOnClickListener {
                controller.navigate(R.id.bannerRequest)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlAffilateCommissionManagement.setOnClickListener {
                controller.navigate(R.id.affiliateCommission)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlOrderManagement.setOnClickListener {
                controller.navigate(R.id.order)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlFullScreenBanner.setOnClickListener {
                controller.navigate(R.id.fullScreenBanner)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlDealsManagement.setOnClickListener {
                controller.navigate(R.id.dealManagement)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlLive.setOnClickListener {
                controller.navigate(R.id.liveRequestTimeSlot)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlStoryManagement.setOnClickListener {
                controller.navigate(R.id.storyManagement)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlAffiliateManagement.setOnClickListener {
                controller.navigate(R.id.affiliateUserManagement)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlAffiliateWithdrawal.setOnClickListener {
                controller.navigate(R.id.withdrawalRequest)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlTicketManagement.setOnClickListener {
                controller.navigate(R.id.ticketManagement)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlSupportChat.setOnClickListener {
                controller.navigate(R.id.supportChat)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            relSubAdmin.setOnClickListener {
                controller.navigate(R.id.subAdminManagement)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

/*            rlPayoutManagement.setOnClickListener {
                controller.navigate(R.id.sellerPayout)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }*/

            rlRefundManagement.setOnClickListener {
                controller.navigate(R.id.refundManagement)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlCms.setOnClickListener {
                controller.navigate(R.id.cmsManagement)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            rlNotificationManagement.setOnClickListener {
                controller.navigate(R.id.notification)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }
        }
    }
    private fun enableDrawer(enable: Boolean) {
        if (enable) {
            binding?.drawerLayout?.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
            binding?.navigationView?.isVisible = true
        } else {
            binding?.drawerLayout?.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            binding?.navigationView?.isVisible = false
        }
    }


    override fun onResume() {
        super.onResume()

//        SharedPreference.get(this).badgeCount = 0
//        ShortcutBadger.removeCount(this)
        //   NotificationManagerCompat.from(this).cancelAll()
    }

}

data class BottomNavItem(
    var name : String,
    var image: Int,
    var selectedImage: Int,
)