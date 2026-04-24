package com.desynatadmin.ui.home.ticketManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentTicketDetailBinding
import com.desynatadmin.databinding.UpdateTicketStatusDialogBinding
import com.desynatadmin.ui.home.ticketManagement.response.GetNewTicketResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class TicketDetailFragment : BaseFragment() {
    private var binding: FragmentTicketDetailBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var ticketData: GetNewTicketResponse.Data.Data?=null
    private var statusType = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTicketDetailBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        val itemJson = arguments?.getString("ticketData")
        if (!itemJson.isNullOrEmpty()) {
            ticketData = Gson().fromJson(itemJson, GetNewTicketResponse.Data.Data::class.java)
            setData()
        }
        binding?.apply {
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("ticket_management", true)
                    }

                val actionAccess = module?.canAction == 1
                tvStatusDropDown.isVisible = actionAccess
                btnUpdateStatus.isVisible = actionAccess
            }

        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvStatusDropDown.setOnClickListener {
                updateTicketStatusDialog()
            }

            btnUpdateStatus.setOnClickListener {
                updateTicketStatus()
            }

           ivAttachment.setOnClickListener {
               CommonMethod.imagePreviewDialog(requireActivity(),ticketData?.file.toString()){

               }
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

        viewModel.updateTicketStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message)

                if (response.data.status == 1) {
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("openPastTab", true)

                    findNavController().popBackStack()
                }
                else{
                    findNavController().popBackStack()
                }
//                ticketData?.status = response.data.status
//                updateTicketStatusData()
                isObserver = false
            }
        }
    }

    private fun setData(){
        binding?.apply {
            tvTitleValue.text = ticketData?.title
            tvDescriptionValue.text = ticketData?.description
            tvTitleValue.text = ticketData?.title

            when(ticketData?.senderType){
                1-> tvSubmittedBy.text = "Customer"
                2-> tvSubmittedBy.text = "Seller"
                3-> tvSubmittedBy.text = "Affiliate"
                4-> tvSubmittedBy.text = "Driver"
            }
            tvRequestByName.text = ticketData?.senderDetails?.fullName
            tvTicketIdValue.text = ticketData?.ticketId
            tvTicketTypeValue.text = ticketData?.ticketTypeDetails?.name
            tvRaisedOn.text = CommonMethod.customizeChangeDateFormat(ticketData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")

            if (ticketData?.file!=null){
                ivAttachment.isVisible = true
                tvAttachments.isVisible = true
                Glide.with(requireActivity()).load(ticketData?.file).placeholder(R.drawable.ic_placeholder).into(ivAttachment)
            }

            updateTicketStatusData()
        }
    }

    private fun updateTicketStatusData() {
        binding?.apply {
            when(ticketData?.status){
                0->{
                    btnUpdateStatus.isVisible = true
                    tvStatusDropDown.isVisible = true
                    tvStatus.isVisible = true
                    tvStatusValue.text = "In Progress"
                    tvStatusValue.setTextColor(Color.parseColor("#D72E04"))

                }
                1->{
                    btnUpdateStatus.isVisible = true
                    tvStatusDropDown.isVisible = true
                    tvStatus.isVisible = true
                    tvStatusValue.text = "Resolved"
                    tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                }
            }
        }
    }

    private fun updateTicketStatus(){
        val map = HashMap<String, Any>()
        map["ticket_id"] = ticketData?.id.toString()
        map["status"] = statusType
        viewModel.updateTicketStatus(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun updateTicketStatusDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = UpdateTicketStatusDialogBinding.inflate(LayoutInflater.from(context))
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

            ivBack.setOnClickListener {
                dialog.dismiss()
            }

            if(ticketData?.status==1){
                rbPending.isVisible = true
            }
            else{
                rbPending.isVisible = false
            }

            dialogBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbPending ->{
                        statusType = 0
                        binding?.tvStatusDropDown?.text = "Pending"
                        dialog.dismiss()
                    }
                    R.id.rbResolved ->{
                        statusType = 1
                        binding?.tvStatusDropDown?.text = "Resolved"
                        dialog.dismiss()
                    }

                }

            }

        }

        dialog.show()
    }

}