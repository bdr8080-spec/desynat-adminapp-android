package com.desynatadmin.ui.home.subAdminManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatadmin.R
import com.desynatadmin.adapter.SubAdminAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSubAdminManagementBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlin.getValue

@AndroidEntryPoint
class SubAdminManagementFragment : BaseFragment() {
    private var binding: FragmentSubAdminManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var isCountryObserver = true
    private var adminList = ArrayList<GetUserResponse.Data.Data>()
    private var adminAdapter : SubAdminAdapter?=null
    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private var searchUser: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10
    var selectedPos = -1
    private var countryList = ArrayList<GetCountryResponse.Data>()
    var countryId:String?=null
    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp=0L
    var toTimestamp=0L
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubAdminManagementBinding.inflate(layoutInflater, container, false)
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

        }
    }

    override fun initControl() {

    }

    override fun observe() {

    }


}