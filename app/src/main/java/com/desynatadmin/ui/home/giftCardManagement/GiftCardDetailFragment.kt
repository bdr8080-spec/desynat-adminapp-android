package com.desynatadmin.ui.home.giftCardManagement

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentGiftCardDetailBinding
import com.desynatadmin.databinding.FragmentGiftCardManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.giftCardManagement.response.GetAllGiftCardResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiftCardDetailFragment : BaseFragment() {
    private var binding: FragmentGiftCardDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var giftData: GetAllGiftCardResponse.Data.Data?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGiftCardDetailBinding.inflate(layoutInflater,container,false)
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
        val itemJson = arguments?.getString("giftCard")
        if (!itemJson.isNullOrEmpty()) {
            giftData = Gson().fromJson(itemJson, GetAllGiftCardResponse.Data.Data::class.java)
            setData()
            updateStatus()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Gift Card Detail"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDelete.setOnClickListener {
                CommonMethod.deleteAlertDialog(
                    requireActivity(),
                    "Are you sure you want to delete the gift card?"
                ) { action ->
                    when (action) {
                        CommonMethod.DialogAction.DELETE -> {
                            deleteGiftCard()
                        }

                        CommonMethod.DialogAction.DISCARD -> {

                        }
                    }
                }

            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("giftData", Gson().toJson(giftData))
                }
                findNavController().navigate(R.id.action_giftCardDetailDetail_to_addGiftCardFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                updateGiftCardStatus()
            }

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }


        viewModel.updateGiftCardStatusLiveData.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                giftData = response.data
                if (response.data.status==1){
                    showToast("Gift Card Activated Successfully")
                }
                else{
                    showToast("Gift Card Deactivated Successfully")

                }
                updateStatus()
                isObserver = false

            }
        }

        viewModel.deleteGiftCardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Gift Card deleted successfully"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun deleteGiftCard(){
        val map = HashMap<String,Any>()
        map["giftCard_id"]=giftData?.id.toString()
        viewModel.deleteGiftCard(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateGiftCardStatus(){
        val map = HashMap<String,Any>()
        map["giftCard_id"]=giftData?.id.toString()
        viewModel.updateGiftCardStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateStatus(){
        binding?.apply {
            if(giftData?.status==1){
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }

        }
    }

    private fun setData(){

        binding?.apply {

            tvGiftCardName.text = giftData?.nameEnglish
            if(giftData?.status==1){
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }

            ivGiftCard.load(giftData?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

    }

}