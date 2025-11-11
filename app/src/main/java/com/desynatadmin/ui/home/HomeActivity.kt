package com.desynatadmin.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.adapter.BottomNavAdapter
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityHomeBinding
import com.desynatadmin.databinding.DrawerLayoutBinding
import com.desynatadmin.ui.auth.AuthActivity
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.m2.CountryCityZoneManaActivity
import com.desynatadmin.ui.m2.SellerManagementActivity
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initView()
        initControl()
        observer()
    }

    override fun initView() {

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
           /* R.id.catalog,
            R.id.myBag,
            R.id.profile,*/
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
              /*  R.id.catalog -> {
                    bottomAdapter?.setSelectedItem(1)
                      enableDrawer(false)
                }
                R.id.myBag -> {
                    bottomAdapter?.setSelectedItem(3)
                      enableDrawer(false)
                }
                R.id.profile -> {
                    bottomAdapter?.setSelectedItem(4)
                      enableDrawer(false)
                }*/
                else ->{
                    enableDrawer(false)
                }
            }


        }

        binding?.apply {
            drawerBinding = DrawerLayoutBinding.bind(navigationView.getHeaderView(0))
            setUpDrawerContent()
            drawerBinding.rlCountryCityManagement.setOnClickListener {
                startActivity(Intent(this@HomeActivity, CountryCityZoneManaActivity::class.java))
            }
            drawerBinding.rlSellereManagment.setOnClickListener {
                startActivity(Intent(this@HomeActivity, SellerManagementActivity::class.java))
            }

        }

        setDrawerData(SharedPreference.get(this).getUser())

    }

    override fun initControl() {
        binding?.apply {


        }
    }

    override fun observer() {
            viewModel.error.observe(this) { errorMessage ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    errorMessage?.let { showToast(it) }
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
                    0 -> controller.navigate(R.id.home)
                   // 1 -> controller.navigate(R.id.catalog)
                    //2 -> controller.navigate(R.id.catalog)
                    3 -> controller.navigate(R.id.planList)
                   // 4 -> controller.navigate(R.id.profile)
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
        drawerBinding.tvName.text = user?.fullName
        drawerBinding.tvEmail.text = user?.email
        /*drawerBinding.ivProfileImage.load(user?.profileImage) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
        }*/
    }

    private fun setUpDrawerContent(){
        drawerBinding.apply {

            ivBack.setOnClickListener {
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


}

data class BottomNavItem(
    var name : String,
    var image: Int,
    var selectedImage: Int,
)