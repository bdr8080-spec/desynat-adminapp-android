package com.desynatadmin.ui.home.subAdminManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.AccessModuleAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSubAdminDetailBinding
import com.desynatadmin.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SubAdminDetailFragment : BaseFragment() {
    private var binding : FragmentSubAdminDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()

    private var accessModuleAdapter: AccessModuleAdapter?=null
    private var accessModuleList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubAdminDetailBinding.inflate(layoutInflater, container,false)
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

            toolbar.tvToolbar.text = "Detail"

            accessModuleAdapter = AccessModuleAdapter(requireActivity(),accessModuleList,object :AccessModuleAdapter.OnClick{
                override fun ClickOnItem(position: Int, type: String) {

                }

            })
            rvAccessModule.adapter = accessModuleAdapter

        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDelete.setOnClickListener {

            }

            btnEdit.setOnClickListener {
               findNavController().navigate(R.id.action_subAdminDetailFragment_to_addSubAdminFragment)
            }
        }
    }

    override fun observe() {
    }

}