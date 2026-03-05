package com.desynatadmin.ui.home.ticketManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.TicketAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentTicketManagementBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class TicketManagementFragment : BaseFragment() {
    private var binding: FragmentTicketManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var ticketAdapter: TicketAdapter?=null
    private var ticketList = ArrayList<String>()
    private var type = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentTicketManagementBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Ticket Management"

            ticketAdapter =
                TicketAdapter(requireActivity(), ticketList, object : TicketAdapter.TicketClick {
                    override fun onTicketClick(position: Int,type: Int) {
                        findNavController().navigate(R.id.action_ticketManagement_to_ticketDetailFragment)
                    }

                })
            rvTicket.adapter = ticketAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivFilter.setOnClickListener {

            }

            tvNew.setOnClickListener {
                new()
            }

            tvOngoing.setOnClickListener {
                ongoing()
            }

            tvPast.setOnClickListener {
                past()
            }
        }
    }

    override fun observe() {
    }

    private fun new(){
        binding?.apply {
            type = 1
            tvNew.setTextColor(requireActivity().getColor(R.color.white))
            tvOngoing.setTextColor(requireActivity().getColor(R.color.black))
            tvPast.setTextColor(requireActivity().getColor(R.color.black))
            tvNew.setBackgroundResource(R.drawable.selected_left_border)
            tvOngoing.background = null
            tvPast.background = null
        }
    }

    private fun ongoing(){
        binding?.apply {
            type = 2
            tvOngoing.setTextColor(requireActivity().getColor(R.color.white))
            tvNew.setTextColor(requireActivity().getColor(R.color.black))
            tvPast.setTextColor(requireActivity().getColor(R.color.black))
            tvOngoing.setBackgroundResource(R.drawable.selected_mid_border)
            tvNew.background = null
            tvPast.background = null
        }
    }

    private fun past(){
        binding?.apply {
            type = 3
            tvPast.setTextColor(requireActivity().getColor(R.color.white))
            tvNew.setTextColor(requireActivity().getColor(R.color.black))
            tvOngoing.setTextColor(requireActivity().getColor(R.color.black))
            tvPast.setBackgroundResource(R.drawable.selected_right_border)
            tvNew.background = null
            tvOngoing.background = null

        }
    }
}