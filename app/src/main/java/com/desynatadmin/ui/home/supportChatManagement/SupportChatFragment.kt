package com.desynatadmin.ui.home.supportChatManagement

import com.desynatadmin.ui.home.SubAdminHomeActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.ChatListAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSupportChatBinding
import com.desynatadmin.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SupportChatFragment : BaseFragment() {
    private var binding: FragmentSupportChatBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var chatListAdapter: ChatListAdapter?=null
    private var chatList = ArrayList<String>()
    private var type = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentSupportChatBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = getString(R.string.chats)

            chatList.clear()
            chatList.add("")
            chatList.add("")
            chatList.add("")
            chatList.add("")
            chatListAdapter = ChatListAdapter(requireActivity(),chatList,object: ChatListAdapter.ChatClick{
                override fun onChatClick(position: Int) {
                 //   findNavController().navigate(R.id.action_supportChatManagement_to_chatMessageFragment)

             /*       val name = "Harsh"
                    val email = "harsh@gmail.com"
                    val role = "admin"
                    val uniqueId = "${role}_$email"
                    val url = "https://tidiowebsite.com?name=$name&email=$email"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
*/
                    findNavController().navigate(R.id.action_supportChatManagement_to_tidioChatFragment)
                }

            } )
            rvChat.adapter = chatListAdapter
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

            ivFilter.setOnClickListener {

            }

            tvCustomer.setOnClickListener {
                customer()
            }

            tvSeller.setOnClickListener {
                seller()
            }

            tvDriver.setOnClickListener {
                driver()
            }
        }
    }

    override fun observe() {
    }

    private fun customer(){
        binding?.apply {
            type = 1
            tvCustomer.setTextColor(requireActivity().getColor(R.color.white))
            tvSeller.setTextColor(requireActivity().getColor(R.color.black))
            tvDriver.setTextColor(requireActivity().getColor(R.color.black))
            tvCustomer.setBackgroundResource(R.drawable.selected_left_border)
            tvSeller.background = null
            tvDriver.background = null
        }
    }

    private fun seller(){
        binding?.apply {
            type = 2
            tvSeller.setTextColor(requireActivity().getColor(R.color.white))
            tvCustomer.setTextColor(requireActivity().getColor(R.color.black))
            tvDriver.setTextColor(requireActivity().getColor(R.color.black))
            tvSeller.setBackgroundResource(R.drawable.selected_mid_border)
            tvCustomer.background = null
            tvDriver.background = null
        }
    }

    private fun driver(){
        binding?.apply {
            type = 3
            tvDriver.setTextColor(requireActivity().getColor(R.color.white))
            tvCustomer.setTextColor(requireActivity().getColor(R.color.black))
            tvSeller.setTextColor(requireActivity().getColor(R.color.black))
            tvDriver.setBackgroundResource(R.drawable.selected_right_border)
            tvCustomer.background = null
            tvSeller.background = null

        }
    }
}