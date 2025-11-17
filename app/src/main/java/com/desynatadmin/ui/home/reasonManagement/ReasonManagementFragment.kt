package com.desynatadmin.ui.home.reasonManagement

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
import com.desynatadmin.adapter.ReasonAdapter
import com.desynatadmin.adapter.UserAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddReasonDialogBinding
import com.desynatadmin.databinding.FragmentReasonManagementBinding
import com.desynatadmin.ui.home.reasonManagement.response.GetReasonResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReasonManagementFragment : BaseFragment() {
    private var binding: FragmentReasonManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var reasonList = ArrayList<GetReasonResponse.Data.Data>()
    private var reasonAdapter : ReasonAdapter?=null

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private var search: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10
    private var type = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReasonManagementBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Reason Management"


            reasonList.clear()
            reasonAdapter = ReasonAdapter(requireActivity(),reasonList,object : ReasonAdapter.ReasonClick{
                override fun onReasonClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("reasonData", Gson().toJson(reasonList[position]))
                    }
                    findNavController().navigate(R.id.action_userFragment_to_userDetailFragment,bundle)
                }

            })

            rvReason.adapter = reasonAdapter

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
                        currentPage = 1
                        pagination = false
                        isFromSwipe = false
                        if (!isObserver)
                            getAllReason(search =  query.ifEmpty { null })
                    }

                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getAllReason(false)
            }

            when(type){
                1-> {
                    type = 1
                    getAllReason()
                    tvExchange.setTextColor(requireActivity().getColor(R.color.white))
                    tvReturn.setTextColor(requireActivity().getColor(R.color.black))
                    tvCancel.setTextColor(requireActivity().getColor(R.color.black))
                    tvExchange.setBackgroundResource(R.drawable.selected_left_border)
                    tvReturn.background = null
                    tvCancel.background = null
                }

                2-> {
                    type = 2
                    getAllReason()
                    tvReturn.setTextColor(requireActivity().getColor(R.color.white))
                    tvExchange.setTextColor(requireActivity().getColor(R.color.black))
                    tvCancel.setTextColor(requireActivity().getColor(R.color.black))
                    tvReturn.setBackgroundResource(R.drawable.selected_mid_border)
                    tvExchange.background = null
                    tvCancel.background = null
                }

                3-> {
                    type = 3
                    getAllReason()
                    tvCancel.setTextColor(requireActivity().getColor(R.color.white))
                    tvExchange.setTextColor(requireActivity().getColor(R.color.black))
                    tvReturn.setTextColor(requireActivity().getColor(R.color.black))
                    tvCancel.setBackgroundResource(R.drawable.selected_right_border)
                    tvExchange.background = null
                    tvReturn.background = null
                }
            }
        }

    }


    override fun initControl() {
        binding?.apply {
            tvExchange.setOnClickListener {
                type = 1
                getAllReason()
                tvExchange.setTextColor(requireActivity().getColor(R.color.white))
                tvReturn.setTextColor(requireActivity().getColor(R.color.black))
                tvCancel.setTextColor(requireActivity().getColor(R.color.black))
                tvExchange.setBackgroundResource(R.drawable.selected_left_border)
                tvReturn.background = null
                tvCancel.background = null
            }

            tvReturn.setOnClickListener {
                type = 2
                getAllReason()
                tvReturn.setTextColor(requireActivity().getColor(R.color.white))
                tvExchange.setTextColor(requireActivity().getColor(R.color.black))
                tvCancel.setTextColor(requireActivity().getColor(R.color.black))
                tvReturn.setBackgroundResource(R.drawable.selected_mid_border)
                tvExchange.background = null
                tvCancel.background = null
            }

            tvCancel.setOnClickListener {
                type = 3
                getAllReason()
                tvCancel.setTextColor(requireActivity().getColor(R.color.white))
                tvExchange.setTextColor(requireActivity().getColor(R.color.black))
                tvReturn.setTextColor(requireActivity().getColor(R.color.black))
                tvCancel.setBackgroundResource(R.drawable.selected_right_border)
                tvExchange.background = null
                tvReturn.background = null
            }

            ivAdd.setOnClickListener {
                addReasonDialog()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getAllReason(false)
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getAllReason(false)
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
                errorMessage?.let { showToast(it) }
                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getReasonResult.observe(viewLifecycleOwner) { response ->
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

                binding?.tvCount?.text = "$currentStr of $lastStr"

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


                val oldSize = reasonList.size
                if (!pagination){
                    reasonList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                reasonList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        reasonAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    reasonAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun getAllReason(loading:Boolean=true,search:String?=lastQuery){
        viewModel.getReason(search = search, page = currentPage)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun addReason(reasonEnglish:String,reasonArabic:String){
        val map = HashMap<String,Any>()
        map["reason_english"] = reasonEnglish
        map["reason_arabic"] = reasonArabic
        map["type"] = type
        viewModel.addReason(map)

            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateReason(reasonEnglish:String,reasonArabic:String,reasonId:String){
        val map = HashMap<String,Any>()
        map["reason_english"] = reasonEnglish
            map["reason_arabic"] = reasonArabic
            map["reason_id"] = reasonId
            map["type"] = type
            viewModel.updateReason(map)

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateReasonStatus(reasonId:String){
        val map = HashMap<String,Any>()
            map["reason_id"] = reasonId
            viewModel.blockUnblockReason(map)

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun deleteReason(reasonId:String){
        val map = HashMap<String,Any>()
            map["reason_id"] = reasonId
            viewModel.deleteReason(map)

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = reasonList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, reasonList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = reasonList.subList(start, end)
                reasonAdapter?.setData(ArrayList(pageData))
            }
        }

    }

    private fun addReasonDialog(isEdit:Boolean = false,reasonId:String?=null) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddReasonDialogBinding.inflate(LayoutInflater.from(context))
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

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(etEnglishReason.text?.isEmpty() == true){
                    showToast("Please enter reason in english")
                }
                else if(etArabicReason.text?.isEmpty() == true){
                    showToast("Please enter reason in arabic")
                }
                else{
                    if(isEdit){
                        if (reasonId != null) {
                            updateReason(etEnglishReason.text?.trim().toString(),etArabicReason.text?.trim().toString(),reasonId)
                        }

                    }
                    else{
                        addReason(etEnglishReason.text?.trim().toString(),etArabicReason.text?.trim().toString())
                    }
                }
            }

        }

        dialog.show()
    }


}