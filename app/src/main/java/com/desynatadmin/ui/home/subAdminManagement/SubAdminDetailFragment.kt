package com.desynatadmin.ui.home.subAdminManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.adapter.AccessModuleAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSubAdminDetailBinding
import com.desynatadmin.ui.home.subAdminManagement.response.GetSubAdminDetailResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SubAdminDetailFragment : BaseFragment() {
    private var binding : FragmentSubAdminDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false

    private var accessModuleAdapter: AccessModuleAdapter?=null
    private var accessModuleList = ArrayList<GetSubAdminDetailResponse.Data.PermissionDetail>()
    private var id: String=""
    private var subAdminData: GetSubAdminDetailResponse.Data?=null
    var actionAccess: Boolean=true

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
        id = arguments?.getString("id").toString()
        binding?.apply {

            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("sub_admin_management", true)
                    }

                actionAccess = module?.canAction == 1

                btnEdit.isVisible = actionAccess
                btnDelete.isVisible = actionAccess
                tvSwitch.isClickable = actionAccess
            }

            accessModuleAdapter = AccessModuleAdapter(requireActivity(),accessModuleList,object :AccessModuleAdapter.OnClick{
                override fun ClickOnItem(position: Int, type: String) {

                }

            })
            rvAccessModule.adapter = accessModuleAdapter

            getSubAdminDetail()
        }
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvShare.setOnClickListener {
                shareSubAdminCredential()
            }

            btnDelete.setOnClickListener {
                CommonMethod.deleteAlertDialog(
                    requireActivity(),
                    getString(R.string.are_you_sure_you_want_to_delete_the_sub_admin)
                ) { action ->
                    when (action) {
                        CommonMethod.DialogAction.DELETE -> {
                            deleteSubAdmin()
                        }

                        CommonMethod.DialogAction.DISCARD -> {

                        }
                    }
                }

            }

            tvSwitch.setOnClickListener {
                if (actionAccess) {
                    updateSubAdminStatus()
                }
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("subAdminData", Gson().toJson(subAdminData))
                }
               findNavController().navigate(R.id.action_subAdminDetailFragment_to_addSubAdminFragment,bundle)
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

        viewModel.getSubAdminDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                subAdminData = response.data
                accessModuleList.clear()
                accessModuleList.addAll(response.data.permissionDetails)
                accessModuleAdapter?.notifyDataSetChanged()
                setData()
                isObserver = false
            }
        }

        viewModel.updateSubAdminStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message)
                subAdminData?.status = response.data.status
                updateStatus()
                isObserver = false
            }
        }

        viewModel.deleteSubAdminResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.sendSubAdminCredentialResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message){
                }
                isObserver = false
            }
        }
    }

    private fun setData() {
        binding?.apply {
            tvName.text = if (Constants.language=="ar") subAdminData?.fullNameAr else subAdminData?.fullName

            tvCountryValue.text = if (Constants.language=="ar") subAdminData?.countryInfo?.nameAr else subAdminData?.countryInfo?.name
            tvPost.text = subAdminData?.profilePic
            tvPasswordValue.text = subAdminData?.unhashedPassword
            tvEmail.text = subAdminData?.email
            tvMobile.text = subAdminData?.countryCode+"-"+subAdminData?.mobile
            Glide.with(requireActivity()).load(subAdminData?.profilePic).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).transform(CircleCrop()).into(ivProfile)

           updateStatus()
        }
    }

    fun updateStatus() {
        binding?.apply {
            if(subAdminData?.status=="1"){
                tvSwitch.text = getString(R.string.active)
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = getString(R.string.inactive)
                tvSwitch.setTextColor(Color.parseColor("#D53B36"))
             //   tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }
        }
    }

    private fun getSubAdminDetail(){
        val map = HashMap<String,Any>()
        map["id"] = id
        viewModel.getSubAdminDetail(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(),true)

    }

    private fun updateSubAdminStatus(){
        val map = HashMap<String,Any>()
        map["id"] = id
        viewModel.updateSubAdminStatus(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(),true)

    }
    private fun deleteSubAdmin(){
        val map = HashMap<String,Any>()
        map["id"] = id
        viewModel.deleteSubAdmin(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(),true)

    }

    private fun shareSubAdminCredential(){
        val map = HashMap<String,Any>()
        map["id"] = id
        viewModel.sendSubAdminCredential(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(),true)

    }

}