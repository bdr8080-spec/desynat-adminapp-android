package com.desynatadmin.ui.m2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.desynatadmin.R
import com.desynatadmin.adapter.CountryAdapter
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityRejectDetailsBinding
import com.desynatadmin.databinding.ActivitySellerManagementBinding
import com.desynatadmin.databinding.AddCityBottomsheetBinding
import com.desynatadmin.databinding.FilterDialogBinding
import com.desynatadmin.databinding.FragmentRejectedScreenBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openDatePicker
import com.desynatadmin.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerManagementActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySellerManagementBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var back: TextView
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        back = findViewById(R.id.tvToolbar)
        back.setOnClickListener { finish() }
        initView()
        observer()
        // Default fragment when activity opens
        replaceFragment(NewScreenFragment())

        // Handle tab clicks
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> replaceFragment(NewScreenFragment())
                    1 -> replaceFragment(ApprovedScreenFragment())
                    2 -> replaceFragment(RejectedScreenFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initView(){
        binding.filter.setOnClickListener {
            filterBottomSheet()
        }
        viewModel.getCountryList()
        binding.etSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

                when (currentFragment) {
                    is NewScreenFragment -> currentFragment.filterData(s.toString())
                    is ApprovedScreenFragment -> currentFragment.filterData(s.toString())
                    is RejectedScreenFragment -> currentFragment.filterData(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun observer(){
        viewModel.getCountryListResult.observe(this) { response ->
                countryList.clear()
                countryList.addAll(response.data)

        }
    }

    private fun filterBottomSheet(){
        var fromTimestamp=0L
        var toTimestamp=0L
        var countryId=""



        val dialog = BottomSheetDialog(this)
        val sheetBinding = FilterDialogBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)
        sheetBinding.etCountry.setOnClickListener {
            sheetBinding.rvCountry.isVisible=!sheetBinding.rvCountry.isVisible
        }
        sheetBinding.closeIv.setOnClickListener {
            dialog.dismiss()
        }
        sheetBinding.etFrom.setOnClickListener {
            openDatePicker(this){date,timestamp->
                sheetBinding.etFrom.text=date
                fromTimestamp=timestamp
            }

        }
        sheetBinding.etTo.setOnClickListener {
            openDatePicker(this){date,timestamp->
                sheetBinding.etTo.text=date
                toTimestamp=timestamp
            }

        }
        sheetBinding.rvCountry.adapter=CountryAdapter(countryList,this){name,id->
            sheetBinding.rvCountry.isVisible=false
            countryId=id
            sheetBinding.etCountry.text=name
        }

        sheetBinding.apply.setOnClickListener {
            viewModel.httpFilterData(SharedPreference.get(this).accessToken,countryId,fromTimestamp.toString(),toTimestamp.toString())
            dialog.dismiss()
        }




        dialog.show()
    }
    }



