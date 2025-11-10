package com.desynatadmin.ui.m2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.desynatadmin.R
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.databinding.ActivityCountryCityZoneManaBinding
import com.desynatadmin.databinding.ActivityHomeBinding
import com.desynatadmin.databinding.AddCityBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryCityZoneManaActivity : BaseActivity() {
    private var binding: ActivityCountryCityZoneManaBinding? =null
    private lateinit var controller: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCountryCityZoneManaBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
        initControl()
        observer()

    }

    override fun initView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        val navGraph = controller.navInflater.inflate(R.navigation.country_city_navigation_graph)
        controller.graph = navGraph


    }

    override fun initControl() {


    }

    override fun observer() {

    }
}