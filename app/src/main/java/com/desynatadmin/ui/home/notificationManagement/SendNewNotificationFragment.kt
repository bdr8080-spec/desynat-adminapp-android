package com.desynatadmin.ui.home.notificationManagement

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
import com.desynatadmin.adapter.AssignUserAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSendNewNotificationBinding
import com.desynatadmin.databinding.SelectNotificationUserDialogBinding
import com.desynatadmin.ui.home.notificationManagement.response.SendNotificationRequest
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SendNewNotificationFragment : BaseFragment(){
    private var binding: FragmentSendNewNotificationBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var assignUserAdapter: AssignUserAdapter?=null
    private var userList = ArrayList<String>()
    private var dialog: Dialog?=null
    private var senderType = 0 //1-admin,2-user,3-seller,4-affiliate,5-driver
    private var selectedReceiverList = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendNewNotificationBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Send New"

            CommonMethod.applyArabicOnlyFilter(etArabicTitle)
            CommonMethod.applyArabicOnlyFilter(etArabicDescription)
            CommonMethod.removeSpace(etEnglishTitle)
            CommonMethod.removeSpace(etArabicDescription)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSend.setOnClickListener {
                if (validation()){
                    sendNotification()
                }
            }

            tvSendType.setOnClickListener {

            }

            radioSendTo.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbAll -> {
                        senderType = 1
                        tvSendType.isVisible = true
                    }
                    R.id.rbCustomer -> {
                        senderType = 2
                        tvSendType.isVisible = true
                    }
                    R.id.rbSeller -> {
                        senderType = 3
                        tvSendType.isVisible = true
                    }
                    R.id.rbDriver -> {
                        senderType = 5
                        tvSendType.isVisible = true
                    }
                }
            }
        }
    }

    private fun sendNotification(){
       val request = SendNotificationRequest(
           titleEnglish = binding?.etEnglishTitle?.text?.trim().toString(),
           titleArabic = binding?.etArabicTitle?.text?.trim().toString(),
           descriptionEnglish = binding?.etEnglishDescription?.text?.trim().toString(),
           descriptionArabic = binding?.etArabicDescription?.text?.trim().toString(),
           senderType = senderType
       )
        viewModel.sendNotification(request)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.sendNotificationResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }
    }

    private fun selectUserDialog() {
        dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = SelectNotificationUserDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            assignUserAdapter = AssignUserAdapter(requireActivity(), userList, object :
                AssignUserAdapter.OnUserClick {
                override fun onClick(position: Int) {

                }

            })
            rvUser.adapter = assignUserAdapter


            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }

            btnSubmit.setOnClickListener {

            }

        }

        dialog?.show()
    }

    private fun validation(): Boolean {
        binding?.apply {

            if (etEnglishTitle.text.toString().trim().isEmpty()) {
                showToast("Please enter title in English")
                return false
            }

            if (etArabicTitle.text.toString().trim().isEmpty()) {
                showToast("Please enter title in Arabic")
                return false
            }

            if (etEnglishDescription.text.toString().trim().isEmpty()) {
                showToast("Please enter description in English")
                return false
            }

            if (etArabicDescription.text.toString().trim().isEmpty()) {
                showToast("Please enter description in Arabic")
                return false
            }

            if (radioSendTo.checkedRadioButtonId == -1) {
                showToast("Please select receiver type")
                return false
            }

            if (selectedReceiverList.isEmpty()) {
                showToast("Please select at least one receiver")
                return false
            }
        }
        return true
    }

}