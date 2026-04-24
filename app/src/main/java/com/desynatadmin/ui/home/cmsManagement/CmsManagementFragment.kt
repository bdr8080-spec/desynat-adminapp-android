package com.desynatadmin.ui.home.cmsManagement

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddCmsContentDialogBinding
import com.desynatadmin.databinding.FragmentCmsManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.cmsManagement.response.GetAboutUsResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetCancellationPolicyResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetExchangePolicyResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetReturnPolicyResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetTermsAndConditionResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.utils.SharedPreference
import kotlin.getValue

@AndroidEntryPoint
class CmsManagementFragment : BaseFragment() {
    private var binding: FragmentCmsManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var userType = 1
    private var optionType = 1
    private var termsAndConditionResponse: GetTermsAndConditionResponse.Data? = null
    private var aboutUsResponse: GetAboutUsResponse.Data? = null
    private var cancellationPolicyResponse: GetCancellationPolicyResponse.Data? = null
    private var returnPolicyResponse: GetReturnPolicyResponse.Data? = null
    private var exchangePolicyResponse: GetExchangePolicyResponse.Data? = null
    private var dialog: Dialog?=null

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
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val actionAccess = subAdminInfo?.permissionDetails
                    ?.any {
                        it.moduleDetails.name.equals("cms_management", ignoreCase = true)&&it.canAction==1
                    } == true

                ivEnglish.isVisible = actionAccess
                ivArabic.isVisible = actionAccess
            }
            toolbar.tvToolbar.text = "CMS"
            getTermsAndCondition()

            when(userType){
                1->user()
                2->seller()
                3->deliveryBoy()
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                 when (val activity = requireActivity()) {
        is HomeActivity -> activity.onBackPressed()
        is SubAdminHomeActivity -> activity.onBackPressed()
        else -> activity.onBackPressed()
    }
            }

            tvSelect.setOnClickListener {
                llOtherOption.isVisible = !llOtherOption.isVisible
            }

            tvFAQs.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("userType", userType)
                }
                findNavController().navigate(
                    R.id.action_cmsManagementManagement_to_faqFragment,
                    bundle
                )
                llOtherOption.isVisible = false
            }

            tvTerms.setOnClickListener {
                optionType = 1
                getTermsAndCondition()
                llOtherOption.isVisible = false
            }

            tvCancellationPolicy.setOnClickListener {
                optionType = 3
                getCancellationPolicy()
                llOtherOption.isVisible = false
            }

            tvReturnPolicy.setOnClickListener {
                optionType = 4
                getReturnPolicy()
                llOtherOption.isVisible = false
            }

            tvAboutUs.setOnClickListener {
                optionType = 2
                getAboutUs()
                llOtherOption.isVisible = false
            }

            tvExchangePolicy.setOnClickListener {
                optionType = 5
                getExchangePolicy()
                llOtherOption.isVisible = false
            }

            tvContactUs.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("userType", userType)
                }
                findNavController().navigate(
                    R.id.action_cmsManagementManagement_to_contactUsFragment,
                    bundle
                )
                llOtherOption.isVisible = false
            }

            tvUser.setOnClickListener {
                llOtherOption.isVisible = false
                user()
            }

            tvSeller.setOnClickListener {
                llOtherOption.isVisible = false
                seller()
            }

            tvDeliveryBoy.setOnClickListener {
                llOtherOption.isVisible = false
                deliveryBoy()
            }

            ivEnglish.setOnClickListener {
                addCmsContentDialog(false)
            }

            ivArabic.setOnClickListener {
                addCmsContentDialog(true)
            }

            tvViewMore.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("title", tvSelect.text.toString())
                    putString("content", tvEnglishTerms.text.toString())
                }
                findNavController().navigate(
                    R.id.action_cmsManagementManagement_to_commonCmsContentFragment,
                    bundle
                )
//                if (tvViewMore.text == "View More") {
//                    tvEnglishTerms.maxLines = Int.MAX_VALUE
//                    tvViewMore.text = "View Less"
//                } else {
//                    tvEnglishTerms.maxLines = 5
//                    tvViewMore.text = "View More"
//                }
            }

            tvViewMoreArabic.setOnClickListener {

                val bundle = Bundle().apply {
                    putString("title", tvSelect.text.toString())
                    putString("content", tvArabicTerms.text.toString())
                }
                findNavController().navigate(
                    R.id.action_cmsManagementManagement_to_commonCmsContentFragment,
                    bundle
                )

//                if (tvViewMoreArabic.text == "View More") {
//                    tvArabicTerms.maxLines = Int.MAX_VALUE
//                    tvViewMoreArabic.text = "View Less"
//                } else {
//                    tvArabicTerms.maxLines = 5
//                    tvViewMoreArabic.text = "View More"
//                }
            }

            tvEnglishTerms.setOnClickListener {
//                val bundle = Bundle().apply {
//                    putString("title", tvSelect.text.toString())
//                    putString("content", tvEnglishTerms.text.toString())
//                }
//                findNavController().navigate(
//                    R.id.action_cmsManagementManagement_to_commonCmsContentFragment,
//                    bundle
//                )
            }

            tvArabicTerms.setOnClickListener {
//                val bundle = Bundle().apply {
//                    putString("title", tvSelect.text.toString())
//                    putString("content", tvArabicTerms.text.toString())
//                }
//                findNavController().navigate(
//                    R.id.action_cmsManagementManagement_to_commonCmsContentFragment,
//                    bundle
//                )
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(errorMessage)
                isObserver = false
            }
        }

        viewModel.getTermsAndConditionResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                termsAndConditionResponse = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.createUpdateTermsAndConditionResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dialog?.dismiss()
                termsAndConditionResponse = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.getAboutUsResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                aboutUsResponse = response.data
                setData()
                isObserver = false
            }
        }
        viewModel.createUpdateAboutUsResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dialog?.dismiss()
                aboutUsResponse = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.getCancellationPolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                cancellationPolicyResponse = response.data
                setData()
                isObserver = false
            }
        }
        viewModel.createUpdateCancellationPolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dialog?.dismiss()
                cancellationPolicyResponse = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.getReturnPolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                returnPolicyResponse = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.createUpdateReturnPolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dialog?.dismiss()
                returnPolicyResponse = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.getExchangePolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                exchangePolicyResponse = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.createUpdateExchangePolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dialog?.dismiss()
                exchangePolicyResponse = response.data
                setData()
                isObserver = false
            }
        }
    }

    private fun user() {
        binding?.apply {
            userType = 1
            hitApi()
            tvUser.setTextColor(requireActivity().getColor(R.color.white))
            tvSeller.setTextColor(requireActivity().getColor(R.color.black))
            tvDeliveryBoy.setTextColor(requireActivity().getColor(R.color.black))
            tvUser.setBackgroundResource(R.drawable.selected_left_border)
            tvSeller.background = null
            tvDeliveryBoy.background = null
        }
    }

    private fun seller() {
        binding?.apply {
            userType = 2
            hitApi()
            tvSeller.setTextColor(requireActivity().getColor(R.color.white))
            tvUser.setTextColor(requireActivity().getColor(R.color.black))
            tvDeliveryBoy.setTextColor(requireActivity().getColor(R.color.black))
            tvSeller.setBackgroundResource(R.drawable.selected_mid_border)
            tvUser.background = null
            tvDeliveryBoy.background = null
        }
    }

    private fun deliveryBoy() {
        binding?.apply {
            userType = 3
            hitApi()
            tvDeliveryBoy.setTextColor(requireActivity().getColor(R.color.white))
            tvUser.setTextColor(requireActivity().getColor(R.color.black))
            tvSeller.setTextColor(requireActivity().getColor(R.color.black))
            tvDeliveryBoy.setBackgroundResource(R.drawable.selected_right_border)
            tvUser.background = null
            tvSeller.background = null
        }
    }

    private fun addCmsContentDialog(isArabic: Boolean=false) {
        dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddCmsContentDialogBinding.inflate(LayoutInflater.from(context))
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

        dialogBinding?.apply {

            CommonMethod.applyArabicOnlyFilter(etArabicDescription)
            CommonMethod.applyEnglishOnlyFilter(etEnglishDescription)
         //   CommonMethod.removeSpace(etEnglishDescription)

            if (isArabic){
                relEnglish.isVisible = false
                etEnglishDescription.isVisible = false
                relArabic.isVisible = true
                etArabicDescription.isVisible = true
            }
            else{
                relEnglish.isVisible = true
                etEnglishDescription.isVisible = true
                relArabic.isVisible = false
                etArabicDescription.isVisible = false
            }

            when (optionType) {
                1 -> {
                    tvToolbar.text = "Terms & Conditions"
                    tvTitleEnglish.text = "Terms & Conditions"
                    tvTitleArabic.text = "Terms & Conditions"
                    etEnglishDescription.setText(CommonMethod.setHtmlText(termsAndConditionResponse?.contentEnglish.toString()))
                    etArabicDescription.setText(CommonMethod.setHtmlText(termsAndConditionResponse?.contentArabic.toString()))
                }

                2 -> {
                    tvToolbar.text = "About Us"
                    tvTitleEnglish.text = "About Us"
                    tvTitleArabic.text = "About Us"
                    etEnglishDescription.setText(CommonMethod.setHtmlText(aboutUsResponse?.contentEnglish.toString()))
                    etArabicDescription.setText(CommonMethod.setHtmlText(aboutUsResponse?.contentArabic.toString()))
                }

                3 -> {
                    tvToolbar.text = "Cancellation Policy"
                    tvTitleEnglish.text = "Cancellation Policy"
                    tvTitleArabic.text = "Cancellation Policy"
                    etEnglishDescription.setText(CommonMethod.setHtmlText(cancellationPolicyResponse?.contentEnglish.toString()))
                    etArabicDescription.setText(CommonMethod.setHtmlText(cancellationPolicyResponse?.contentArabic.toString()))
                }

                4 -> {
                    tvToolbar.text = "Return Policy"
                    tvTitleEnglish.text = "Return Policy"
                    tvTitleArabic.text = "Return Policy"
                    etEnglishDescription.setText(CommonMethod.setHtmlText(returnPolicyResponse?.contentEnglish.toString()))
                    etArabicDescription.setText(CommonMethod.setHtmlText(returnPolicyResponse?.contentArabic.toString()))
                }

                5 -> {
                    tvToolbar.text = "Exchange Policy"
                    tvTitleEnglish.text = "Exchange Policy"
                    tvTitleArabic.text = "Exchange Policy"
                    etEnglishDescription.setText(CommonMethod.setHtmlText(exchangePolicyResponse?.contentEnglish.toString()))
                    etArabicDescription.setText(CommonMethod.setHtmlText(exchangePolicyResponse?.contentArabic.toString()))
                }
            }

            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }

            btnSave.setOnClickListener {
                if (etEnglishDescription.text?.trim().toString().isEmpty() && !isArabic) {
                    showToast("Please enter content")
                } else if (etArabicDescription.text?.trim().toString().isEmpty() && isArabic) {
                    showToast("Please enter content")
                } else {
                    when(optionType){
                        1->{
                            createUpdateTermsAndCondition(if(etEnglishDescription.text?.trim().toString()!="") etEnglishDescription.text?.trim().toString() else termsAndConditionResponse?.contentEnglish?:"",if(etArabicDescription.text?.trim().toString()!="") etArabicDescription.text?.trim().toString() else termsAndConditionResponse?.contentArabic?:"")
                        }
                        2->{
                            createUpdateAboutUs(if(etEnglishDescription.text?.trim().toString()!="") etEnglishDescription.text?.trim().toString() else aboutUsResponse?.contentEnglish?:"",if(etArabicDescription.text?.trim().toString()!="") etArabicDescription.text?.trim().toString() else aboutUsResponse?.contentArabic?:"")

                        }
                        3->{
                            createUpdateCancellationPolicy(if(etEnglishDescription.text?.trim().toString()!="") etEnglishDescription.text?.trim().toString() else cancellationPolicyResponse?.contentEnglish?:"",if(etArabicDescription.text?.trim().toString()!="") etArabicDescription.text?.trim().toString() else cancellationPolicyResponse?.contentArabic?:"")

                        }
                        4->{
                            createUpdateReturnPolicy(if(etEnglishDescription.text?.trim().toString()!="") etEnglishDescription.text?.trim().toString() else returnPolicyResponse?.contentEnglish?:"",if(etArabicDescription.text?.trim().toString()!="") etArabicDescription.text?.trim().toString() else returnPolicyResponse?.contentArabic?:"")

                        }
                        5->{
                            createUpdateExchangePolicy(if(etEnglishDescription.text?.trim().toString()!="") etEnglishDescription.text?.trim().toString() else exchangePolicyResponse?.contentEnglish?:"",if(etArabicDescription.text?.trim().toString()!="") etArabicDescription.text?.trim().toString() else exchangePolicyResponse?.contentArabic?:"")

                        }
                    }
                }
            }
        }
        dialog?.show()
    }

    private fun getTermsAndCondition() {
        val map = HashMap<String, Any>()
        map["type"] = userType
        viewModel.getTermsAndCondition(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun createUpdateTermsAndCondition(contentEnglish: String,contentArabic: String) {
        val map = HashMap<String, Any>()
        map["type"] = userType
        map["content_english"] = contentEnglish
        map["content_arabic"] = contentArabic
        viewModel.createUpdateTermsAndCondition(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getAboutUs() {
        val map = HashMap<String, Any>()
        map["type"] = userType
        viewModel.getAboutUs(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun createUpdateAboutUs(contentEnglish: String,contentArabic: String) {
        val map = HashMap<String, Any>()
        map["type"] = userType
        map["content_english"] = contentEnglish
        map["content_arabic"] = contentArabic
        viewModel.createUpdateAboutUs(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getReturnPolicy() {
        val map = HashMap<String, Any>()
        map["type"] = userType
        viewModel.getReturnPolicy(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun createUpdateCancellationPolicy(contentEnglish: String,contentArabic: String) {
        val map = HashMap<String, Any>()
        map["type"] = userType
        map["content_english"] = contentEnglish
        map["content_arabic"] = contentArabic
        viewModel.createUpdateCancellationPolicy(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getCancellationPolicy() {
        val map = HashMap<String, Any>()
        map["type"] = userType
        viewModel.getCancellationPolicy(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun createUpdateReturnPolicy(contentEnglish: String,contentArabic: String) {
        val map = HashMap<String, Any>()
        map["type"] = userType
        map["content_english"] = contentEnglish
        map["content_arabic"] = contentArabic
        viewModel.createUpdateReturnPolicy(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getExchangePolicy() {
        val map = HashMap<String, Any>()
        map["type"] = userType
        viewModel.getExchangePolicy(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun createUpdateExchangePolicy(contentEnglish: String,contentArabic: String) {
        val map = HashMap<String, Any>()
        map["type"] = userType
        map["content_english"] = contentEnglish
        map["content_arabic"] = contentArabic
        viewModel.createUpdateExchangePolicy(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun hitApi() {
        when (optionType) {
            1 -> {
                getTermsAndCondition()
            }

            2 -> {
                getAboutUs()
            }

            3 -> {
                getCancellationPolicy()
            }

            4 -> {
                getReturnPolicy()
            }

            5 -> {
                getExchangePolicy()
            }
        }
    }

    private fun setData() {
        binding?.apply {
            when (optionType) {
                1 -> {
                    tvSelect.text = "Terms & Conditions"

                    tvEnglishTerms.text =
                        CommonMethod.setHtmlText(termsAndConditionResponse?.contentEnglish.toString())
                    tvArabicTerms.text =
                        CommonMethod.setHtmlText(termsAndConditionResponse?.contentArabic.toString())
                }

                2 -> {
                    tvSelect.text = "About Us"
                    tvEnglishTerms.text =
                        CommonMethod.setHtmlText(aboutUsResponse?.contentEnglish.toString())
                    tvArabicTerms.text =
                        CommonMethod.setHtmlText(aboutUsResponse?.contentArabic.toString())
                }

                3 -> {
                    tvSelect.text = "Cancellation Policy"
                    tvEnglishTerms.text =
                        CommonMethod.setHtmlText(cancellationPolicyResponse?.contentEnglish.toString())
                    tvArabicTerms.text =
                        CommonMethod.setHtmlText(cancellationPolicyResponse?.contentArabic.toString())
                }

                4 -> {
                    tvSelect.text = "Return Policy"
                    tvEnglishTerms.text =
                        CommonMethod.setHtmlText(returnPolicyResponse?.contentEnglish.toString())
                    tvArabicTerms.text =
                        CommonMethod.setHtmlText(returnPolicyResponse?.contentArabic.toString())
                }

                5 -> {
                    tvSelect.text = "Exchange Policy"
                    tvEnglishTerms.text =
                        CommonMethod.setHtmlText(exchangePolicyResponse?.contentEnglish.toString())
                    tvArabicTerms.text =
                        CommonMethod.setHtmlText(exchangePolicyResponse?.contentArabic.toString())
                }
            }
//            tvEnglishTerms.post {
//                tvViewMore.isVisible = tvEnglishTerms.lineCount > 5
//            }
//
//            tvArabicTerms.post {
//                tvViewMoreArabic.isVisible = tvArabicTerms.lineCount > 5
//            }

        }
    }

    fun makeExpandable(textView: AppCompatTextView, maxLines: Int = 5) {

        textView.post {

            if (textView.lineCount > maxLines) {

                val text = textView.text.toString()
                val truncated = text.substring(0, text.length - 20)

                val spannable = SpannableString("$truncated... View More")

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        textView.maxLines = Int.MAX_VALUE
                        textView.text = "$text View Less"
                    }
                }

                spannable.setSpan(
                    clickableSpan,
                    spannable.length - 9,
                    spannable.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                textView.text = spannable
                textView.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }
}