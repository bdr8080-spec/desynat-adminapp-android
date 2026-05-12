package com.desynatadmin.ui.auth

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.desynatadmin.R
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.databinding.ActivityAuthBinding
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private lateinit var controller: NavController
    private var binding: ActivityAuthBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
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

        val from = intent.getStringExtra("from")

        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

      /*  val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        controller.graph = controller.navInflater.inflate(R.navigation.auth_navigation_graph)*/

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController

        val navGraph = controller.navInflater.inflate(R.navigation.auth_navigation_graph)

        navGraph.setStartDestination(
            when {
                from == "language" -> R.id.signInFragment
                Constants.isLogout -> R.id.signInFragment
                else -> R.id.language
            }
        )

        controller.graph = navGraph

    }

    override fun initControl() {
    }

    override fun observer() {
    }
}