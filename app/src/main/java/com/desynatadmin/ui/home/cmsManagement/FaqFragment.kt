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
import com.desynatadmin.adapter.FaqAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddFaqDialogBinding
import com.desynatadmin.databinding.FragmentFaqBinding
import com.desynatadmin.ui.home.cmsManagement.response.GetFaqResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import kotlin.getValue

@AndroidEntryPoint
class FaqFragment : BaseFragment() {
    private var binding: FragmentFaqBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var faqAdapter: FaqAdapter?=null
    private var faqList = ArrayList<GetFaqResponse.Data>()
    private var selectedPos : Int= -1
    private var userType = 1

    private var faqDialog: Dialog?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFaqBinding.inflate(layoutInflater, container, false)
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
            userType = arguments?.getInt("userType")?:1
            toolbar.tvToolbar.text = getString(R.string.faq_s)

            faqAdapter = FaqAdapter(requireActivity(),faqList,object : FaqAdapter.ClickOnItem{
                override fun onClickItem(position: Int,type:Int) {
                    selectedPos = position
                    when(type){
                        1->{
                           addFaqDialog(true)
                        }
                        2->{
                            deleteFaq()
                        }
                    }
                }

            })
            rvFaq.adapter = faqAdapter

            getFaq()
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAddFaq.setOnClickListener {
                addFaqDialog()
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

        viewModel.getFaqResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                faqList.clear()
                faqList.addAll(response.data)
                faqAdapter?.notifyDataSetChanged()
                checkData()
                isObserver = false
            }
        }

        viewModel.addFaqResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                faqDialog?.dismiss()
                CommonMethod.showSuccessDialog(requireActivity(),
                    getString(R.string.faq_added_successfully))
                faqList.add(response.data)
                faqAdapter?.notifyDataSetChanged()
                checkData()
                isObserver = false
            }
        }

        viewModel.updateFaqResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                faqDialog?.dismiss()
                CommonMethod.showSuccessDialog(requireActivity(),
                    getString(R.string.faq_updated_successfully))
                faqList[selectedPos] = response.data
                faqAdapter?.notifyDataSetChanged()
                checkData()
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.deleteFaqResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),
                    getString(R.string.faq_deleted_successfully))
                faqList.removeAt(selectedPos)
                faqAdapter?.notifyItemRemoved(selectedPos)
                faqAdapter?.notifyItemRangeChanged(selectedPos,faqList.size)
                selectedPos = -1
                checkData()
                isObserver = false
            }
        }

    }

    private fun addFaqDialog(isEdit: Boolean=false) {
        faqDialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddFaqDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { faqDialog?.setContentView(it) }

        val window: Window = faqDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        faqDialog?.setCancelable(false)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding?.apply {
//            CommonMethod.removeSpace(etEnglishQuestion)
//            CommonMethod.removeSpace(etEnglishAnswer)
           /* CommonMethod.applyEnglishOnlyFilter(etEnglishQuestion)
            CommonMethod.applyEnglishOnlyFilter(etEnglishAnswer)
            CommonMethod.applyArabicOnlyFilter(etArabicQuestion)
            CommonMethod.applyArabicOnlyFilter(etArabicAnswer)*/

            CommonMethod.removeSpace(etEnglishQuestion)
            CommonMethod.removeSpace(etEnglishAnswer)

            CommonMethod.removeSpace(etArabicQuestion)
            CommonMethod.removeSpace(etArabicAnswer)
            if (isEdit){
                val faqData = faqList[selectedPos]
                tvToolbar.text = getString(R.string.edit_faq)
                btnAdd.text = getString(R.string.edit)
                etEnglishQuestion.setText(faqData.questionEnglish)
                etArabicQuestion.setText(faqData.questionArabic)
                etEnglishAnswer.setText(faqData.answerEnglish)
                etArabicAnswer.setText(faqData.answerArabic)

            }
            else{
                tvToolbar.text = getString(R.string.add_faq)
                btnAdd.text = getString(R.string.add)
            }

            tvToolbar.setOnClickListener {
                faqDialog?.dismiss()
            }

            btnAdd.setOnClickListener {
                if (etEnglishAnswer.text?.trim().toString().isEmpty()){
                    showToast(getString(R.string.please_enter_question_in_english))
                }
                else if (etArabicQuestion.text?.trim().toString().isEmpty()){
                    showToast(getString(R.string.please_enter_question_in_arabic))
                }
                else if (etEnglishAnswer.text?.trim().toString().isEmpty()){
                    showToast(getString(R.string.please_enter_answer_in_english))
                }
                else if (etArabicAnswer.text?.trim().toString().isEmpty()){
                    showToast(getString(R.string.please_enter_answer_in_arabic))
                }
              else{
                    val map = HashMap<String,Any>()
                    map["type"] = userType
                    map["question_english"] = etEnglishQuestion.text?.trim().toString()
                    map["answer_english"] = etEnglishAnswer.text?.trim().toString()
                    map["question_arabic"] = etArabicQuestion.text?.trim().toString()
                    map["answer_arabic"] = etArabicAnswer.text?.trim().toString()
                    if (isEdit){
                        map["faq_id"] = faqList[selectedPos].id
                        updateFaq(map)
                    }
                    else{
                        addFaq(map)
                    }
                }
            }

        }

        faqDialog?.show()
    }

    private fun getFaq(loading:Boolean=true){
        val map = HashMap<String,Any>()
        map["type"] = userType
        viewModel.getFaq(map)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun addFaq(map: HashMap<String, Any>) {
        viewModel.addFaq(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateFaq(map: HashMap<String, Any>) {
        viewModel.updateFaq(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

   private fun deleteFaq() {
       val map = HashMap<String,Any>()
       map["faq_id"] = faqList[selectedPos].id
        viewModel.deleteFaq(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = faqList.isEmpty()
    }
}