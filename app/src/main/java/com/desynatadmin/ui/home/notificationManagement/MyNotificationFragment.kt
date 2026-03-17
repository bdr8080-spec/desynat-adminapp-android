package com.desynatadmin.ui.home.notificationManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.MyNotificationAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentMyNotificationBinding
import com.desynatadmin.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class MyNotificationFragment : BaseFragment() {
    private var binding: FragmentMyNotificationBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isFromSwipe=false

    private var myNotificationList = ArrayList<String>()
    private var myNotificationAdapter: MyNotificationAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyNotificationBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "My Notifications"

            myNotificationAdapter = MyNotificationAdapter(requireActivity(), myNotificationList, object :
                MyNotificationAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {

                }
            })
            rvMyNotification.adapter = myNotificationAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getNotification(false)
            }
        }
        getMyNotification()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun getNotification(isLoading: Boolean=true){
        if (isLoading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
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

                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getMyNotificationResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                    myNotificationList.clear()


              //  myNotificationList.addAll(response.data)
                checkData()
                myNotificationAdapter?.notifyDataSetChanged()
                isFromSwipe = false
                isObserver = false
            }
        }

    }

private fun getMyNotification(loading:Boolean=true){
    val map = HashMap<String,Any>()

    viewModel.getMyNotification(map)
    if (loading){
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }
    isObserver = true
}

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = myNotificationList.isEmpty()
    }

}