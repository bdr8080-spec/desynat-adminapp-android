package com.desynatadmin.ui.home.banner

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.desynatadmin.R
import com.desynatadmin.adapter.BannerPerDayPriceAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddPerDayPriceDialogBinding
import com.desynatadmin.databinding.FragmentBannerPerDayPriceBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.banner.response.GetBannerPricePerDayResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerPerDayPriceFragment : BaseFragment() {
    private var binding: FragmentBannerPerDayPriceBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var bannerPerDayPriceList = ArrayList<GetBannerPricePerDayResponse.Data>()
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var countryMap: Map<Int, GetCountryResponse.Data>?=null
    private var bannerPerDayPriceAdapter : BannerPerDayPriceAdapter?=null
    private var type = 1 //1-top,2-bottom,3-sub category
    private var selectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerPerDayPriceBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        countryList.clear()
        SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
        countryMap = countryList.associateBy { it.id }
        ProgressDialogUtils.getInstance().hideProgress()

        binding?.apply {
            toolbar.tvToolbar.text = "Banner per day price"


            bannerPerDayPriceList.clear()
            bannerPerDayPriceAdapter =
                BannerPerDayPriceAdapter(requireActivity(), bannerPerDayPriceList,
                    countryMap!!,object : BannerPerDayPriceAdapter.OnClick {
                    override fun onItemClick(position: Int) {
                        selectedPos = position
                        addPriceDialog()
                    }

                })
            rvBannerPerDayPrice.adapter = bannerPerDayPriceAdapter

            when(type){
                1->{
                    getBanner()
                    tvTop.setTextColor(requireActivity().getColor(R.color.white))
                    tvBottom.setTextColor(requireActivity().getColor(R.color.black))
                    tvSubCategory.setTextColor(requireActivity().getColor(R.color.black))
                    tvTop.setBackgroundResource(R.drawable.selected_left_border)
                    tvBottom.background = null
                    tvSubCategory.background = null
                }
                2->{
                    getBanner()
                    tvBottom.setTextColor(requireActivity().getColor(R.color.white))
                    tvTop.setTextColor(requireActivity().getColor(R.color.black))
                    tvSubCategory.setTextColor(requireActivity().getColor(R.color.black))
                    tvBottom.setBackgroundResource(R.drawable.selected_mid_border)
                    tvTop.background = null
                    tvSubCategory.background = null
                }

                3->{
                    getBanner()
                    tvSubCategory.setTextColor(requireActivity().getColor(R.color.white))
                    tvTop.setTextColor(requireActivity().getColor(R.color.black))
                    tvBottom.setTextColor(requireActivity().getColor(R.color.black))
                    tvSubCategory.setBackgroundResource(R.drawable.selected_right_border)
                    tvTop.background = null
                    tvBottom.background = null
                }
            }
        }

    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener{
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            tvTop.setOnClickListener {
                type = 1
                getBanner()
                tvTop.setTextColor(requireActivity().getColor(R.color.white))
                tvBottom.setTextColor(requireActivity().getColor(R.color.black))
                tvSubCategory.setTextColor(requireActivity().getColor(R.color.black))
                tvTop.setBackgroundResource(R.drawable.selected_left_border)
                tvBottom.background = null
                tvSubCategory.background = null
            }

            tvBottom.setOnClickListener {
                type = 2
                getBanner()
                tvBottom.setTextColor(requireActivity().getColor(R.color.white))
                tvTop.setTextColor(requireActivity().getColor(R.color.black))
                tvSubCategory.setTextColor(requireActivity().getColor(R.color.black))
                tvBottom.setBackgroundResource(R.drawable.selected_mid_border)
                tvTop.background = null
                tvSubCategory.background = null
            }

            tvSubCategory.setOnClickListener {
                type = 3
                getBanner()
                tvSubCategory.setTextColor(requireActivity().getColor(R.color.white))
                tvTop.setTextColor(requireActivity().getColor(R.color.black))
                tvBottom.setTextColor(requireActivity().getColor(R.color.black))
                tvSubCategory.setBackgroundResource(R.drawable.selected_right_border)
                tvTop.background = null
                tvBottom.background = null
            }

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }

        viewModel.bannerPerDayResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                    bannerPerDayPriceList.clear()
                bannerPerDayPriceList.addAll(response.data)
                checkData()

                    bannerPerDayPriceAdapter?.notifyDataSetChanged()

                isObserver = false
            }
        }

        viewModel.updateBannerPerDayResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                when (type){
                    1-> showToast("Top Banner Price Update successfully")
                    2-> showToast("Bottom Banner Price Update successfully")
                    3-> showToast("Sub-Category Banner Price Update successfully")
                }

                bannerPerDayPriceList[selectedPos] = response.data

                bannerPerDayPriceAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1

                isObserver = false
            }
        }
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = bannerPerDayPriceList.isEmpty()
    }

    private fun getBanner(){
        viewModel.getBannerPerDay(type)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


    private fun updatePrice(price:String) {
        val map = HashMap<String,Any>()
        map["country_id"] = bannerPerDayPriceList[selectedPos].countryId
        map["type"] = type
        map["per_day_price"] = price
        viewModel.updateBannerPerDayPrice(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun addPriceDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddPerDayPriceDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            etAmount.setText(bannerPerDayPriceList[selectedPos].perDayPrice)

            etAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editable: Editable?) {
                    val text = editable.toString()

                    if (text.length > 1 && text.startsWith("0") && !text.startsWith("0.")) {
                        val newValue = text.trimStart('0')

                        etAmount.removeTextChangedListener(this)
                        etAmount.setText(newValue)
                        etAmount.setSelection(newValue.length)
                        etAmount.addTextChangedListener(this)
                    }
                }
            })

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(isInvalid(etAmount, "Amount")) {
                    return@setOnClickListener
                }
                else{
                    updatePrice(etAmount.text?.trim().toString())
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

    fun isInvalid(et: AppCompatEditText, fieldName: String): Boolean {
        val value = et.text.toString().trim()
        if (value.isEmpty()) {
            showToast("Please enter $fieldName")
            return true
        }
        val num = value.toIntOrNull() ?: 0
        if (num <= 0) {
            showToast("$fieldName should be greater than 0")
            return true
        }
        return false
    }

}