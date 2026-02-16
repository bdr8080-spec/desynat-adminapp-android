package com.desynatadmin.ui.home.affiliateWithdrawalRequest

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.desynatadmin.R
import com.desynatadmin.adapter.AffiliateWithdrawalRequestAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAffiliateWithdrawalRequestBinding
import com.desynatadmin.databinding.RejectReasonLayoutBinding
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetLiveRequestResponse
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AffiliateWithdrawalRequestFragment : BaseFragment() {
    private var binding: FragmentAffiliateWithdrawalRequestBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var affiliateWithdrawalRequestAdapter: AffiliateWithdrawalRequestAdapter?=null
    private var withdrawalList = ArrayList<GetLiveRequestResponse.Data.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateWithdrawalRequestBinding.inflate(layoutInflater, container, false)
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
            affiliateWithdrawalRequestAdapter = AffiliateWithdrawalRequestAdapter(requireActivity(),withdrawalList,object :
                AffiliateWithdrawalRequestAdapter.OnClick{
                override fun click(position: Int, type: Int) {
                    when(type){
                        1->{

                        }
                        2->{
                            rejectReasonDialog()
                        }
                    }

                }

            })
            rvAffiliateWithdrawalRequest.adapter = affiliateWithdrawalRequestAdapter
        }

    }

    override fun initControl() {
        binding?.apply {

        }
    }

    override fun observe() {
    }

    private fun rejectReasonDialog() {
        var rejectionType = ""
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
            etReason.hint = "Enter Here"

            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            btnAccept.setOnClickListener {
                if(etReason.text?.trim().toString().isEmpty()) {
                    showToast("Please enter reason")
                }
                else{
                    rejectWithdrawalRequest(etReason.text?.trim().toString())
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

    private fun rejectWithdrawalRequest(reason:String){

    }

}