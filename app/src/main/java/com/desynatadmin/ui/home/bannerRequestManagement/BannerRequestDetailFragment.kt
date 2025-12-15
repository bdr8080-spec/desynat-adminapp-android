package com.desynatadmin.ui.home.bannerRequestManagement

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
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentBannerRequestDetailBinding
import com.desynatadmin.databinding.RejectBannerReasonDialogBinding
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerRequestDetailFragment : BaseFragment() {
    private var binding: FragmentBannerRequestDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var type = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerRequestDetailBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Details"

            when(type){
                1->{
                    btnAccept.isVisible = true
                    btnReject.isVisible = true
                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                    tvStatus.text = "New"
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                }
                2->{
                    btnAccept.isVisible = false
                    btnReject.isVisible = false
                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                    tvStatus.text = "Active"
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                }
                3->{
                    btnAccept.isVisible = false
                    btnReject.isVisible = false
                    tvReason.isVisible = true
                    tvReasonValue.isVisible = true
                    tvStatus.text = "Rejected"
                    tvStatus.setTextColor(Color.parseColor("#D53B36"))
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnReject.setOnClickListener {
                rejectReasonDialog()
            }

            btnAccept.setOnClickListener {

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
    }

    private fun rejectReasonDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RejectBannerReasonDialogBinding.inflate(LayoutInflater.from(context))
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
                if(etReason.text?.trim().toString().isEmpty()) {
                    showToast("Please enter reason")
                }
                else{
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }


}