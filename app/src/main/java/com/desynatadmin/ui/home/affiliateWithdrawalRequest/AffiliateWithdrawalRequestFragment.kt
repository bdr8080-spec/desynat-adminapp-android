package com.desynatadmin.ui.home.affiliateWithdrawalRequest

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.desynatadmin.R
import com.desynatadmin.adapter.AffiliateWithdrawalRequestAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAffiliateWithdrawalRequestBinding
import com.desynatadmin.databinding.RejectReasonLayoutBinding
import com.desynatadmin.payoutRetrofit.PayoutRetrofit
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.affiliateWithdrawalRequest.response.GetWithdrawalRequestResponse
import com.desynatadmin.ui.home.affiliateWithdrawalRequest.response.PayoutRequest
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AffiliateWithdrawalRequestFragment : BaseFragment() {
    private var binding: FragmentAffiliateWithdrawalRequestBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isTransferObserver = true
    private var affiliateWithdrawalRequestAdapter: AffiliateWithdrawalRequestAdapter? = null
    private var withdrawalList = ArrayList<GetWithdrawalRequestResponse.Data.Data>()
    private var selectedPos = -1
    private var type = "0"
    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private val itemsPerPage = 10
    private var isFromSwipe=false
    private var pagination=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentAffiliateWithdrawalRequestBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.affiliate_withdrawal_requests)

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getWithdrawalRequest(false)
            }

            affiliateWithdrawalRequestAdapter =
                AffiliateWithdrawalRequestAdapter(requireActivity(), withdrawalList, object :
                    AffiliateWithdrawalRequestAdapter.OnClick {
                    override fun click(position: Int, type: Int) {
                        selectedPos = position
                        when (type) {
                            1 -> {
                               // callPayoutApi()
                                acceptAffiliateUser()
                            }
                            2 -> {
                                rejectReasonDialog()
                            }
                        }

                    }

                })
            rvAffiliateWithdrawalRequest.adapter = affiliateWithdrawalRequestAdapter
        }

        getWithdrawalRequest()

    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                 when (val activity = requireActivity()) {
        is HomeActivity -> activity.onBackPressed()
        is SubAdminHomeActivity -> activity.onBackPressed()
        else -> activity.onBackPressed()
    }
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getWithdrawalRequest(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getWithdrawalRequest(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            tvNew.setOnClickListener {
                newRequest()
            }

            tvApproved.setOnClickListener {
               approvedRequest()
            }

            tvRejected.setOnClickListener {
               rejectRequest()
            }

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.acceptWithdrawalRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                withdrawalList.removeAt(selectedPos)
                affiliateWithdrawalRequestAdapter?.notifyItemRemoved(selectedPos)
                affiliateWithdrawalRequestAdapter?.notifyItemRangeChanged(selectedPos, withdrawalList.size)
                checkData()
                CommonMethod.showSuccessDialog(requireActivity(),
                    getString(R.string.withdrawal_request_approved_successfully)){

                }
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.rejectWithdrawalRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                withdrawalList.removeAt(selectedPos)
                affiliateWithdrawalRequestAdapter?.notifyItemRemoved(selectedPos)
                affiliateWithdrawalRequestAdapter?.notifyItemRangeChanged(selectedPos, withdrawalList.size)
                checkData()
                CommonMethod.showSuccessDialog(requireActivity(),
                    getString(R.string.withdrawal_request_rejected_successfully)){

                }
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.WithdrawalTransferResult.observe(viewLifecycleOwner) { response ->
            if (isTransferObserver) {
                isTransferObserver = false
            }
        }

        viewModel.getWithdrawalRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

                val currentStr = if (currentPage < 10) "0$currentPage" else "$currentPage"
                val lastStr = if (lastPage < 10) "0$lastPage" else "$lastPage"

                binding?.tvCount?.text = "$currentStr ${getString(R.string.of)} $lastStr"

                if (currentPage<lastPage){
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand)
                }
                else{
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand_disable)
                }

                if (currentPage>1){
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand)
                }
                else{
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand_disable)
                }


                val oldSize = withdrawalList.size
                if (!pagination){
                    withdrawalList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                withdrawalList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        affiliateWithdrawalRequestAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    affiliateWithdrawalRequestAdapter?.notifyDataSetChanged()
                }
                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, withdrawalList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = withdrawalList.subList(start, end)
                affiliateWithdrawalRequestAdapter?.setData(ArrayList(pageData))
            }
        }

    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = withdrawalList.isEmpty()
    }

    private fun rejectReasonDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RejectReasonLayoutBinding.inflate(LayoutInflater.from(context))
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

            rbComplete.isVisible = false
            rbReject.isVisible = false
            etReason.hint = getString(R.string.enter_here)

            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            btnAccept.setOnClickListener {
                if (etReason.text?.trim().toString().isEmpty()) {
                    showToast(getString(R.string.please_enter_reason))
                } else {
                    rejectWithdrawalRequest(etReason.text?.trim().toString())
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

    private fun newRequest(){
        binding?.apply {
            type = "0"
            getWithdrawalRequest()
            tvNew.setTextColor(Color.parseColor("#875C3A"))
            viewNew.setBackgroundColor(Color.parseColor("#875C3A"))
            tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
            viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
            tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
            viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
        }
    }

    private fun approvedRequest(){
        binding?.apply {
            type = "1"
            getWithdrawalRequest()
            tvNew.setTextColor(Color.parseColor("#C0BBBB"))
            viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
            tvApproved.setTextColor(Color.parseColor("#875C3A"))
            viewApproved.setBackgroundColor(Color.parseColor("#875C3A"))
            tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
            viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
        }
    }

    private fun rejectRequest(){
        binding?.apply {
            type = "2"
            getWithdrawalRequest()
            tvNew.setTextColor(Color.parseColor("#C0BBBB"))
            viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
            tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
            viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
            tvRejected.setTextColor(Color.parseColor("#875C3A"))
            viewRejected.setBackgroundColor(Color.parseColor("#875C3A"))
        }
    }

    private fun getWithdrawalRequest(loading:Boolean=true){
        val map = HashMap<String,Any>()
        map["req_type"] = type //0-pending,1-approved,2-rejected
//        map["from_date"] = currentPage
//        map["to_date"] = currentPage
//        map["search"] = currentPage
        map["page"] = currentPage
        map["limit"] = 10
        viewModel.getWithdrawalRequest(map)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun acceptAffiliateUser(){
        val map = HashMap<String,Any>()
        map["req_id"] = withdrawalList[selectedPos].id
        map["transaction_id"] = "TXN${withdrawalList[selectedPos].requestId}"
        map["payment_status"] = 1
        viewModel.acceptWithdrawalRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun rejectWithdrawalRequest(reason: String) {
        val map = HashMap<String,Any>()
        map["req_id"] = withdrawalList[selectedPos].id
        map["reject_reason"] = reason
        viewModel.rejectWithdrawalRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun WithdrawalTransfer() {
        val map = HashMap<String,Any>()
        map["withdraw_request_id"] = withdrawalList[selectedPos].id
        viewModel.WithdrawalTransfer(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isTransferObserver = true
    }

    fun callPayoutApi() {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        val request = PayoutRequest(
            CustomerName = withdrawalList[selectedPos].affiliateDetails?.fullName.toString(),
            Amount = withdrawalList[selectedPos].amount.toDoubleOrNull()?:0.0,
            CurrencyCode = "KWD",
            IBAN = withdrawalList[selectedPos].bankDetails?.iban.toString(),
         //   BankCode = withdrawalList[selectedPos].bankDetails?.bankName.toString(),
            BankCode = "NBK",
            AccountNumber = withdrawalList[selectedPos].bankDetails?.accountNo.toString()
        )
        lifecycleScope.launch {
            try {
                val response = PayoutRetrofit.api.sendTransfer(
//                    "Bearer SK_KWT_z05HvVtA7meJtUkqI1EyvRBdhADWLu61vb0qfLibYSFdIGEntxDwJrWNE84ktpZY",
                    "Bearer SK_KWT_vVZlnnAqu8jRByOWaRPNId4ShzEDNt256dvnjebuyzo52dXjAfRx2ixW5umjWSUx",
//                    "https://api.myfatoorah.com/v2/SendTransfer",
                    "https://apitest.myfatoorah.com/v3/SendTransfer",
                    request
                )
                ProgressDialogUtils.getInstance().hideProgress()
                if (response.isSuccessful) {

                    val body = response.body()
                    Log.d("PAYOUT_SUCCESS", body.toString())

                } else {

                    val errorBody = response.errorBody()?.string()
                    val errorCode = response.code()

                    Log.d("PAYOUT_FAILED", "Code: $errorCode")
                    Log.d("PAYOUT_FAILED", "Error: $errorBody")
                }

            } catch (e: Exception) {
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("Payout_ERROR", e.message ?: "Error")
            }
        }
    }

}