package com.desynatadmin.ui.m2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.desynatadmin.R
import com.desynatadmin.base.BaseActivity

class RejectDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reject_details)
        initView()
        initControl()
        observer()

    }

    override fun initView() {

    }

    override fun initControl() {

    }

    override fun observer() {

    }
}