package com.desynatadmin.ui.home.giftCardManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.BrandAdapter
import com.desynatadmin.adapter.GiftCardAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentGiftCardManagementBinding
import com.desynatadmin.databinding.FragmentPromoCodeManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.giftCardManagement.response.GetAllGiftCardResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GiftCardManagementFragment : BaseFragment() {
    private var binding: FragmentGiftCardManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var giftCardList = ArrayList<GetAllGiftCardResponse.Data.Data>()
    private var giftCardAdapter : GiftCardAdapter?=null
    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private var searchBrand: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGiftCardManagementBinding.inflate(layoutInflater,container,false)
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
            ProgressDialogUtils.getInstance().hideProgress()
            CommonMethod.removeSpace(etSearch)
            toolbar.tvToolbar.text = "Gift Cards"
            giftCardList.clear()
            giftCardAdapter = GiftCardAdapter(requireActivity(),giftCardList,object :GiftCardAdapter.GiftCardClick{
                override fun onGiftCardClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("giftCard", Gson().toJson(giftCardList[position]))
                    }
                    findNavController().navigate(R.id.action_giftCardFragment_to_giftCardDetailFragment,bundle)
                }

            })

            rvGiftCard.adapter = giftCardAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    searchBrand?.cancel()
                    searchBrand = lifecycleScope.launch {
                        delay(600)
                        currentPage = 1
                        pagination = false
                        isFromSwipe = false
                        if (!isObserver)
                            getGiftCard(search =  query.ifEmpty { null })
                    }

                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getGiftCard(false)
            }
        }

        getGiftCard()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            ivAddBrand.setOnClickListener {
                findNavController().navigate(R.id.action_giftCardFragment_to_addGiftCardFragment)
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getGiftCard(false)
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getGiftCard(false)
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

        viewModel.getGiftCardResult.observe(viewLifecycleOwner) { response ->
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


                val oldSize = giftCardList.size
                if (!pagination){
                    giftCardList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                giftCardList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        giftCardAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    giftCardAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun getGiftCard(loading:Boolean=true,search:String?=lastQuery){
        viewModel.getGiftCard(search = search, page = currentPage)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = giftCardList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, giftCardList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = giftCardList.subList(start, end)
                giftCardAdapter?.setData(ArrayList(pageData))
            }
        }

    }

}