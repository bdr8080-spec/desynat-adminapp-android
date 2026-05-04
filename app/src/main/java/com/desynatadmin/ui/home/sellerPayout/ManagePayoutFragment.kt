package com.desynatadmin.ui.home.sellerPayout

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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.SellerPayoutHistoryAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddPayoutDialogBinding
import com.desynatadmin.databinding.FragmentManagePayoutBinding
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ManagePayoutFragment : BaseFragment() {
    private var binding: FragmentManagePayoutBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private val itemsPerPage = 10

    private var sellerPayoutHistoryAdapter: SellerPayoutHistoryAdapter?=null
    private var payoutHistoryList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManagePayoutBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = getString(R.string.seller_payout)

            sellerPayoutHistoryAdapter = SellerPayoutHistoryAdapter(requireActivity(),payoutHistoryList,object :
                SellerPayoutHistoryAdapter.OnClick{
                override fun OnItemClick(position: Int) {
                }

            })
            rvPayoutHistory.adapter = sellerPayoutHistoryAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getPayoutHistoryList(false)
            }
        }
        }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAddPayout.setOnClickListener {
                addPayoutDialog()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getPayoutHistoryList(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getPayoutHistoryList(false)
                    binding?.progressBar?.isVisible = true
                }
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

//        viewModel.addPayoutResult.observe(viewLifecycleOwner) { response ->
//            if (isObserver) {
//                ProgressDialogUtils.getInstance().hideProgress()
//                CommonMethod.showSuccessDialog(requireActivity(),"Payout added successfully"){
//
//                }
//                isObserver = false
//            }
//        }

//                viewModel.getPayoutHistoryListResult.observe(viewLifecycleOwner) { response ->
//            if (isObserver) {
//                ProgressDialogUtils.getInstance().hideProgress()
//                binding?.progressBar?.isVisible = false
//                if (isFromSwipe){
//                    binding?.swipeRefreshLayout?.isRefreshing = false
//                }
//
//                currentPage = response.data.currentPage
//                totalItem = response.data.total
//
//                lastPage = response.data.lastPage
//
//                val currentStr = if (currentPage < 10) "0$currentPage" else "$currentPage"
//                val lastStr = if (lastPage < 10) "0$lastPage" else "$lastPage"
//
//                binding?.tvCount?.text = "$currentStr of $lastStr"
//
//                if (currentPage<lastPage){
//                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand)
//                }
//                else{
//                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand_disable)
//                }
//
//                if (currentPage>1){
//                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand)
//                }
//                else{
//                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand_disable)
//                }
//
//
//                val oldSize = payoutHistoryList.size
//                if (!pagination){
//                    payoutHistoryList.clear()
//                }
//                updatePage()
//                val newItems = response.data.data ?: emptyList()
//
//                payoutHistoryList.addAll(newItems)
//                checkData()
//
//                if (pagination){
//
//                    response.data.data?.size?.let {
//                        sellerPayoutHistoryAdapter?.notifyItemRangeInserted(oldSize,
//                            it
//                        )
//                    }
//                }
//                else{
//                    sellerPayoutHistoryAdapter?.notifyDataSetChanged()
//                }
//
//                pagination = false
//                isFromSwipe = false
//                isObserver = false
//            }
//        }
    }

    private fun getPayoutHistoryList(loading:Boolean=true){
        val map = HashMap<String,Any>()
        map["page"] = currentPage
        map["limit"] = 10
        //     viewModel.getPayoutHistoryList(map)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }


    private fun checkData() {
        binding?.tvNotData?.isVisible = payoutHistoryList.isEmpty()

    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, payoutHistoryList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = payoutHistoryList.subList(start, end)
                sellerPayoutHistoryAdapter?.setData(ArrayList(pageData))
            }
        }

    }


    private fun addPayout(amount:String,transactionId: String){
        val map = HashMap<String,Any>()
        map["amount"] = amount
        map["transaction_id"] = transactionId
        //viewModel.addPayout(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun addPayoutDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddPayoutDialogBinding.inflate(LayoutInflater.from(context))
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

        dialogBinding?.apply {

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
                else if(etTransactionId.text?.trim().toString().isEmpty()) {
                    showToast(getString(R.string.please_enter_transaction_id))
                    return@setOnClickListener
                }
                else{
                    addPayout(etAmount.text?.trim().toString(),etTransactionId.text?.trim().toString())
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

    fun isInvalid(et: AppCompatEditText, fieldName: String): Boolean {
        val value = et.text.toString().trim()
        if (value.isEmpty()) {
            showToast("${getString(R.string.please_enter)} $fieldName")
            return true
        }
        val num = value.toIntOrNull() ?: 0
        if (num <= 0) {
            showToast("$fieldName ${getString(R.string.should_be_greater_than_zero)}")
            return true
        }
        return false
    }

}