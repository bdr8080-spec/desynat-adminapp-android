package com.desynatadmin.ui.m2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.desynatadmin.R
import com.desynatadmin.adapter.NewAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentNewScreenBinding
import com.desynatadmin.databinding.RejectReasonLayoutBinding
import com.desynatadmin.ui.home.brandManagement.response.NewSellerResponse
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewScreenFragment : BaseFragment() {

    private var binding: FragmentNewScreenBinding? = null
    private lateinit var navController: NavController
    private val viewModel: HomeViewModel by viewModels()
    var pageNo = 1
    var lastPage = 1
    var list=ArrayList<NewSellerResponse.Data.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewScreenBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

    }

    override fun onResume() {
        super.onResume()
        apiHit()
    }

    fun apiHit(){
        viewModel.hitNewSellerList(
            token = SharedPreference.get(requireContext()).accessToken,
            from_date = "",
            country_id = "",
            to_date = "",
            limit = "5",
            page = pageNo.toString(),
            search = ""
        )
    }

    override fun initControl() {
        binding?.imForward?.setOnClickListener{
            if(lastPage > pageNo){
                pageNo++
                apiHit()
            }
        }

        binding?.imBackward?.setOnClickListener{
            if(pageNo > 1){
                pageNo--
                apiHit()
            }
        }
    }

    override fun observe() {

        viewModel.getAcceptSeller.observe(viewLifecycleOwner){
            showToast("Seller Accepted")
            pageNo= 1
            apiHit()
        }
        viewModel.getRejectSeller.observe(viewLifecycleOwner){
            showToast("Seller Rejected")
            pageNo = 1
            apiHit()
            deleteBottomDialog?.dismiss()
        }

        viewModel.getNewSellerList.observe(viewLifecycleOwner){
            lastPage = it.data.last_page
            binding?.tvPages?.text ="$pageNo/$lastPage"
            if(!it.data.data.isNullOrEmpty()){
                list=it.data.data
                binding?.noSeller?.isVisible = false
                binding?.newRecycler?.isVisible = true
                binding?.newRecycler?.adapter = NewAdapter(requireActivity(),list, object : NewAdapter.OnClick{
                    override fun onCLick(position: Int) {
                        startActivity(Intent(requireContext(), NewDetailActivity::class.java)
                            .putExtra("id", list.get(position).id)
                        )
                    }

                    override fun onAccept(position: Int) {
                        val map = HashMap<String, Any>()
                        map["seller_id"] = list.get(position).id?: ""
                        viewModel.hitAcceptSeller(
                            SharedPreference.get(requireContext()).accessToken,
                          map
                        )
                    }

                    override fun onDelete(position: Int) {
                        deleteBottomSheet( list.get(position).id?: "")
                    }

                })
            }
            else{
                binding?.noSeller?.isVisible = true
                binding?.newRecycler?.isVisible = false
            }
        }
    }

    var deleteBottomDialog: BottomSheetDialog? = null
    private fun deleteBottomSheet(id: String) {
        deleteBottomDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val deleteBinding = RejectReasonLayoutBinding.inflate(layoutInflater)
        var type = ""

        deleteBottomDialog?.apply {
            setContentView(deleteBinding.root)
            setCancelable(true)
        }

        deleteBinding.rbComplete.setOnClickListener {
            type = "complete"
            deleteBinding.rbComplete.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_selected,0,0,0)
            deleteBinding.rbReject.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_unselected,0,0,0)
        }
        deleteBinding.rbReject.setOnClickListener {
            type = "document"
            deleteBinding.rbComplete.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_unselected,0,0,0)
            deleteBinding.rbReject.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_selected,0,0,0)
        }

        deleteBinding.btnAccept.setOnClickListener {
            if(!deleteBinding.etReason.text.isNullOrEmpty()){
                val map = HashMap<String, Any>()
                map["seller_id"] = id
                map["reject_type"] = type
                map["reject_reason"] = deleteBinding.etReason.text.toString()
                viewModel.hitRejectSeller(
                    SharedPreference.get(requireContext()).accessToken,
                    map
                )
            }
            else{
                showToast("Please enter reason")
            }

        }

        deleteBinding.closeBtn.setOnClickListener {
            deleteBottomDialog?.dismiss()
        }


        deleteBottomDialog?.show()
    }


    fun filterData(s:String){
        var arr=list.filter { it.first_name.contains(s,true) }
        (binding?.newRecycler?.adapter as NewAdapter).list=arr
        if(arr.isNullOrEmpty()){
            binding?.noSeller?.isVisible = true
            binding?.newRecycler?.isVisible = false
        }
        else{
            binding?.noSeller?.isVisible = false
            binding?.newRecycler?.isVisible = true
        }
        (binding?.newRecycler?.adapter as NewAdapter).notifyDataSetChanged()
    }

}