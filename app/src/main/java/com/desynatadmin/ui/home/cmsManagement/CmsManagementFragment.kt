package com.desynatadmin.ui.home.cmsManagement

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
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddCmsContentDialogBinding
import com.desynatadmin.databinding.AddFaqDialogBinding
import com.desynatadmin.databinding.FragmentCmsManagementBinding
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CmsManagementFragment : BaseFragment() {
    private var binding: FragmentCmsManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var type = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentCmsManagementBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "CMS"
            }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvSelect.setOnClickListener {
                llOtherOption.isVisible = !llOtherOption.isVisible
            }

            tvFAQs.setOnClickListener {

            }

            tvCancellationPolicy.setOnClickListener {

            }

            tvReturnPolicy.setOnClickListener {

            }

            tvAboutUs.setOnClickListener {

            }

            tvExchangePolicy.setOnClickListener {

            }

            tvContactUs.setOnClickListener {

            }

            tvUser.setOnClickListener {
               user()
            }

            tvSeller.setOnClickListener {
              seller()
            }

            tvDeliveryBoy.setOnClickListener {
              deliveryBoy()
            }
        }
    }

    override fun observe() {
    }

    private fun user(){
        binding?.apply {
            type = 1
            tvUser.setTextColor(requireActivity().getColor(R.color.white))
            tvSeller.setTextColor(requireActivity().getColor(R.color.black))
            tvDeliveryBoy.setTextColor(requireActivity().getColor(R.color.black))
            tvUser.setBackgroundResource(R.drawable.selected_left_border)
            tvSeller.background = null
            tvDeliveryBoy.background = null
        }
    }

    private fun seller(){
        binding?.apply {
            type = 2
            tvSeller.setTextColor(requireActivity().getColor(R.color.white))
            tvUser.setTextColor(requireActivity().getColor(R.color.black))
            tvDeliveryBoy.setTextColor(requireActivity().getColor(R.color.black))
            tvSeller.setBackgroundResource(R.drawable.selected_mid_border)
            tvUser.background = null
            tvDeliveryBoy.background = null
        }
    }

    private fun deliveryBoy(){
        binding?.apply {
            type = 3
            tvDeliveryBoy.setTextColor(requireActivity().getColor(R.color.white))
            tvUser.setTextColor(requireActivity().getColor(R.color.black))
            tvSeller.setTextColor(requireActivity().getColor(R.color.black))
            tvDeliveryBoy.setBackgroundResource(R.drawable.selected_right_border)
            tvUser.background = null
            tvSeller.background = null

        }
    }

    private fun AddCmsContentDialog(content:String) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddCmsContentDialogBinding.inflate(LayoutInflater.from(context))
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

            etDescription.setText(content)

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSave.setOnClickListener {
                if (etDescription.text?.trim().toString().isEmpty()){
                    showToast("Please enter")
                }
                else{

                }
            }

        }

        dialog.show()
    }

}