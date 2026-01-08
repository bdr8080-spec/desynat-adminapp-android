package com.desynatadmin.ui.home.shippingAndTaxManagement

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.adapter.ShippingChargeAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddShippingChargeDialogBinding
import com.desynatadmin.databinding.AddTaxDialogBinding
import com.desynatadmin.databinding.FragmentShippingChargesBinding
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.GetShippingTaxResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingChargesFragment : BaseFragment() {
    private var addShippingDialog: Dialog?=null
    private var binding: FragmentShippingChargesBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isAddObserver = true
    private var shippingList = ArrayList<GetShippingTaxResponse.Data.ShippingInfo>()
    private var shippingChargeAdapter : ShippingChargeAdapter?=null
    private var shippingTaxData : GetShippingTaxResponse.Data?=null

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var pagination=false
    private val itemsPerPage = 10
    var selectedPos = -1
    var add = false
    val rangeList = (1..100).map { it.toString() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShippingChargesBinding.inflate(layoutInflater,container,false)
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
            val itemJson = arguments?.getString("shippingData")
            if (!itemJson.isNullOrEmpty()) {
                shippingTaxData = Gson().fromJson(itemJson, GetShippingTaxResponse.Data::class.java)
                setTaxData()
            }
            toolbar.tvToolbar.text = "Delivery Charges & Other Taxs"


            shippingList.clear()
            //shippingTaxData?.shippingInfo?.let { shippingList.addAll(it) }
            shippingChargeAdapter = ShippingChargeAdapter(requireActivity(),shippingList,object : ShippingChargeAdapter.ClickOnItem{
                override fun onClickItem(position: Int,type:Int) {
                    selectedPos = position
                  when(type){
                      1->{
                          addShippingChargeDialog(true)
                      }
                      2->{
                          updateShippingChargeStatus()
                      }
                      3->{
                          deleteShippingCharge()
                      }
                  }
                }

            })

            rvShippingFee.adapter = shippingChargeAdapter
        }

        getShippingCharge()
    }

    private fun setTaxData() {
        binding?.apply {
            tvCountry.text = "${shippingTaxData?.flag}  ${shippingTaxData?.name}"
            tvTax.text = "${CommonMethod.formatDoubleValue(shippingTaxData?.taxInfo?.tax?.toDouble())}%"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivEdit.setOnClickListener {
                addTaxDialog()
            }

            ivAdd.setOnClickListener {
                addShippingChargeDialog()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getShippingCharge(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getShippingCharge(false)
                    binding?.progressBar?.isVisible = true
                }
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false

                pagination = false
                isObserver = false
            }
        }

        viewModel.deleteShippingChargeResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast("Delivery Charge delete Successfully ")
                shippingList.removeAt(selectedPos)
                shippingChargeAdapter?.notifyItemRemoved(selectedPos)
                shippingChargeAdapter?.notifyItemRangeChanged(selectedPos,shippingList.size)
                selectedPos = -1
                checkData()
                isObserver = false
            }
        }

        viewModel.updateShippingChargeStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                shippingList[selectedPos] = response.data
                shippingChargeAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
                checkData()
                isObserver = false
            }
        }

        viewModel.updateShippingChargeResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                addShippingDialog?.dismiss()
                ProgressDialogUtils.getInstance().hideProgress()
                showToast("Delivery Charge Updated Successfully ")
                shippingList[selectedPos] = response.data
                shippingChargeAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.addShippingChargeResult.observe(viewLifecycleOwner) { response ->
            if (isAddObserver) {
                if (response.status=="error"){
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(response.message)
                }
                else {
                    add = true
                    getShippingCharge()
//                shippingList.add(0, response.data)
//                shippingChargeAdapter?.notifyItemInserted(0)
                }
                isAddObserver = false
            }
        }

        viewModel.addTaxResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                shippingTaxData?.taxInfo = response.data
                setTaxData()
            }
        }

        viewModel.getShippingChargeResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (add) {
                    addShippingDialog?.dismiss()
                    showToast("Delivery Charge Added Successfully ")
                    add = false
                }
                binding?.progressBar?.isVisible = false
//                currentPage = response.data.currentPage
//                totalItem = response.data.total

               // lastPage = response.data.lastPage

                val currentStr = if (currentPage < 10) "0$currentPage" else "$currentPage"
                val lastStr = if (lastPage < 10) "0$lastPage" else "$lastPage"

                binding?.tvCount?.text = "$currentStr of $lastStr"

                if (currentPage<lastPage){
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand)
                }
                else{
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand_disable)
                }

                if (currentPage>1){
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand)
                }
                else{
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand_disable)
                }


                val oldSize = shippingList.size
                if (!pagination){
                    shippingList.clear()
                }
                updatePage()
               // val newItems = response.data.data ?: emptyList()

                response.data?.let { shippingList.addAll(it) }
                checkData()
                shippingChargeAdapter?.notifyDataSetChanged()
                /*if (pagination){

                    response.data.data?.size?.let {
                        shippingChargeAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    shippingChargeAdapter?.notifyDataSetChanged()
                }*/

                pagination = false
                isObserver = false
            }
        }
    }

    private fun addTax(tax:String) {
        val map = HashMap<String,Any>()
        map["country_id"] = shippingTaxData?.id.toString()
        map["tax"] = tax
            viewModel.addTax(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun addShippingCharge(fee:String,rangFrom:String,rangeTo:String) {
        val map = HashMap<String,Any>()
        map["country_id"] = shippingTaxData?.id.toString()
        map["range_from"] = rangFrom
        map["range_to"] = rangeTo
        map["shipping_fee"] = fee
            viewModel.addShippingCharge(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isAddObserver = true
    }


    private fun updateShippingCharge(fee:String,rangFrom:String,rangeTo:String) {
        val map = HashMap<String,Any>()
        map["country_id"] = shippingList[selectedPos].countryId
        map["range_from"] = rangFrom
        map["range_to"] = rangeTo
        map["shipping_fee"] = fee
        map["shipping_charge_id"] = shippingList[selectedPos].id
            viewModel.updateShippingCharge(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateShippingChargeStatus() {
        val map = HashMap<String,Any>()
        map["shipping_charge_id"] = shippingList[selectedPos].id
            viewModel.updateShippingChargeStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun deleteShippingCharge() {
        val map = HashMap<String,Any>()
        map["shipping_charge_id"] = shippingList[selectedPos].id
        viewModel.deleteShippingCharge(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }



    private fun getShippingCharge(loading:Boolean=true){
        viewModel.getShippingCharge( shippingTaxData?.taxInfo?.countryId.toString(),page = currentPage)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = shippingList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, shippingList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = shippingList.subList(start, end)
                shippingChargeAdapter?.setData(ArrayList(pageData))
            }
        }

    }

    private fun addTaxDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddTaxDialogBinding.inflate(LayoutInflater.from(context))
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

            etAmount.setText(CommonMethod.formatPrice(shippingTaxData?.taxInfo?.tax?.toDouble()?:0.0))
            var isEditing = false
            var previousValue = ""

            etAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    previousValue = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    val value = s.toString()

                    if (value.isNotEmpty()) {
                        val num = value.toFloatOrNull() ?: 0f

                        if (num > 100) {
                            etAmount.setText(previousValue)

                            // IMPORTANT: Prevent IndexOutOfBounds
                            etAmount.setSelection(previousValue.length.coerceAtMost(etAmount.text?.length ?: 0))
                        }
                    }

                    isEditing = false
                }
            })


            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(isInvalid(etAmount, "Tax")) {
                    return@setOnClickListener
                }
                else{
                    addTax(etAmount.text.toString())
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

    private fun addShippingChargeDialog(isEdit: Boolean =false) {
        addShippingDialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddShippingChargeDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { addShippingDialog?.setContentView(it) }

        val window: Window = addShippingDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        addShippingDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            if (isEdit){
//                etFrom.text = CommonMethod.formatDoubleValue(shippingList[selectedPos]?.rangeFrom?.toDouble())
//                etTo.text = CommonMethod.formatDoubleValue(shippingList[selectedPos]?.rangeTo?.toDouble())
                etFrom.setText(CommonMethod.formatDoubleValue(shippingList[selectedPos]?.rangeFrom?.toDouble()))
                etTo.setText(CommonMethod.formatDoubleValue(shippingList[selectedPos]?.rangeTo?.toDouble()))
                etAmount.setText(CommonMethod.formatDoubleValue(shippingList[selectedPos]?.shippingFee?.toDouble()))
                tvToolbar.text = "Edit"
                btnSubmit.text = "Save Changes"
            }

            etAmount.addTextChangedListener(object : TextWatcher {
                private var isEditing = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    s?.let {
                        if (it.startsWith("0")) {
                            it.replace(0, 1, "")
                        }
                    }

                    isEditing = false
                }
            })

            etFrom.setOnClickListener{
                showRangeList(etFrom)
            }

            etTo.setOnClickListener{
                showRangeList(etTo)
            }

            tvToolbar.setOnClickListener {
                addShippingDialog?.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(etFrom.text?.isEmpty() == true){
                    showToast("Please Enter Range ")
                }
                else if(etTo.text?.isEmpty() == true){
                    showToast("Please Enter Range ")
                }
               else if(isInvalid(etAmount, "Delivery Price")) {
                    return@setOnClickListener
                }
                else{
                    if(isEdit){
                        updateShippingCharge(etAmount.text.toString(),etFrom.text.toString(),etTo.text.toString())
                    }
                    else{
                        addShippingCharge(etAmount.text.toString(),etFrom.text.toString(),etTo.text.toString())
                    }
                }
            }

        }

        addShippingDialog?.show()
    }

    fun isInvalid(et: AppCompatEditText, fieldName: String): Boolean {
        val value = et.text.toString().trim()
        if (value.isEmpty()) {
            showToast("Please enter $fieldName")
            return true
        }
        val num = value.toDoubleOrNull() ?: 0.0
        if (num <= 0) {
            showToast("$fieldName should be greater than 0")
            return true
        }
        return false
    }

    private fun showRangeList(editText: TextView) {
        val popup = ListPopupWindow(requireContext())
        popup.anchorView = editText
        popup.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, rangeList)
        )

        popup.setOnItemClickListener { _, _, pos, _ ->
            editText.text = rangeList[pos]
            popup.dismiss()
        }

        popup.width = editText.width
        popup.isModal = true
        popup.show()
    }

}