package com.desynatadmin.ui.home.notificationManagement

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.AssignUserAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSendNewNotificationBinding
import com.desynatadmin.databinding.SelectNotificationUserDialogBinding
import com.desynatadmin.utils.CommonMethod
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

            }
        }

    }

    override fun observe() {
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

}