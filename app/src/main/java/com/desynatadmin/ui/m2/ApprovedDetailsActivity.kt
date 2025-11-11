package com.desynatadmin.ui.m2

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.desynatadmin.R
import com.desynatadmin.adapter.HighlightsAdapter
import com.desynatadmin.adapter.ReelAdapter
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.databinding.ActivityApprovedDetailsBinding

class ApprovedDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityApprovedDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate *after* onCreate is called
        binding = ActivityApprovedDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Now the view hierarchy exists, safe to call this
        enableEdgeToEdge()

        initView()
        initControl()
        observer()
    }

    override fun initView() {

    }

    override fun initControl() {
        binding.reelRecyclerView.adapter = ReelAdapter()
        binding?.btnSellerInfo?.setOnClickListener {
            binding?.btnSellerInfo?.setBackgroundResource(R.drawable.btn_seller_selected)
            binding?.btnSellerInfo?.setTextColor(Color.WHITE)
            binding?.btnOtherInfo?.setBackgroundResource(R.drawable.btn_other_unselected)
            binding?.btnOtherInfo?.setTextColor(Color.BLACK)
            binding?.sellerInfoLayout?.visibility = View.VISIBLE
            binding?.otherInfoLayout?.visibility = View.GONE
        }

        binding?.btnOtherInfo?.setOnClickListener {
            binding?.btnOtherInfo?.setBackgroundResource(R.drawable.btn_seller_selected)
            binding?.btnOtherInfo?.setTextColor(Color.WHITE)

            binding?.btnSellerInfo?.setBackgroundResource(R.drawable.btn_other_unselected)
            binding?.btnSellerInfo?.setTextColor(Color.BLACK)
            binding?.sellerInfoLayout?.visibility = View.GONE
            binding?.otherInfoLayout?.visibility = View.VISIBLE
        }
        binding?.highlightsRecyclerView?.adapter = HighlightsAdapter()

    }


    override fun observer() {

    }
}