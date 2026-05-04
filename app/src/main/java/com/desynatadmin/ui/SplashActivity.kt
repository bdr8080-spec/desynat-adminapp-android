package com.desynatadmin.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.desynatadmin.R
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.databinding.ActivitySplashBinding
import com.desynatadmin.ui.auth.AuthActivity
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference

class SplashActivity : BaseActivity() {
    private var binding: ActivitySplashBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false
        initView()
        initControl()
        observer()
    }

    override fun initView() {
        Constants.language = SharedPreference.get(this).language
        CommonMethod.setLocale(this, Constants.language )
        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPreference.get(this).accessToken.isNotEmpty()) {
                if (SharedPreference.get(this).getUser()?.type=="2"){
                    startActivity(Intent(this, SubAdminHomeActivity::class.java))
                    finish()
                }
                else{
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }

            }
            else{
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }

        },500)
    }

    override fun initControl() {
    }

    override fun observer() {
    }
}