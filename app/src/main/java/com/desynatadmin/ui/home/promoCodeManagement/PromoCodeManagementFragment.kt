package com.desynatadmin.ui.home.promoCodeManagement

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.DeliveryBoyAdapter
import com.desynatadmin.adapter.PromoCodeAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentDeliveryBoyManagementBinding
import com.desynatadmin.databinding.FragmentPromoCodeManagementBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.ui.home.promoCodeManagement.response.GetPromoCodeResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PromoCodeManagementFragment : BaseFragment() {
    private var binding: FragmentPromoCodeManagementBinding?=null

    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var promoCodeList = ArrayList<GetPromoCodeResponse.Data>()
    private var promoCodeAdapter : PromoCodeAdapter?=null
    private var isFromSwipe=false
    private var search: Job? = null
    private var lastQuery: String? = null
    var selectedPos = -1

    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp = 0L
    var toTimestamp = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromoCodeManagementBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()

        binding?.apply {
            CommonMethod.removeSpace(etSearch)
            toolbar.tvToolbar.text = "Promocode Management"
            promoCodeList.clear()
            promoCodeAdapter = PromoCodeAdapter(requireActivity(),promoCodeList,object : PromoCodeAdapter.PromoCodeClick{
                override fun onPromoCodeClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("promoCodeData", Gson().toJson(promoCodeList[position]))
                    }
                    findNavController().navigate(R.id.action_promoCodeFragment_to_addPromoCodeFragment,bundle)
                }

            })

            rvPromoCode.adapter = promoCodeAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        isFromSwipe = false
                        if (!isObserver)
                            getPromoCode(search =  query.ifEmpty { null })
                    }

                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getPromoCode(false)
            }
        }

        getPromoCode()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_promoCodeFragment_to_addPromoCodeFragment)
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (isFromSwipe) {
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                errorMessage?.let { showToast(it) }
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getPromoCodeListResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (isFromSwipe) {
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                    promoCodeList.clear()
                promoCodeList.addAll(response.data)
                checkData()

                    promoCodeAdapter?.notifyDataSetChanged()
                isFromSwipe = false
                isObserver = false
            }
        }
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = promoCodeList.isEmpty()
    }

    private fun getPromoCode(loading:Boolean=true,search:String?=null){
        viewModel.getPromoCode(search = search, fromDate = startDate, toDate = endDate)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun filterDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = UserFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            tvCountry.isVisible = false
            etCountry.isVisible = false

            if (startDate!=null){
                etFrom.text=CommonMethod.formatDate(startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (endDate!=null){
                etTo.text=CommonMethod.formatDate(endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()){date,timestamp->
                    etFrom.text=date
                    fromTimestamp=timestamp
                }

            }
            etTo.setOnClickListener {
                openCustomDatePicker(requireActivity(), minDate = fromTimestamp){date,timestamp->
                    etTo.text=date
                    toTimestamp=timestamp
                }

            }

            tvClear.setOnClickListener {
                startDate = null
                endDate = null
                toTimestamp = 0L
                fromTimestamp = 0L
                getPromoCode()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                if (etFrom.text.isNotEmpty()){
                    startDate = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }

                if (etTo.text.isNotEmpty()){
                    endDate = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }
                getPromoCode()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

}