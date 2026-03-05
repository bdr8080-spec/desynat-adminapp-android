package com.desynatadmin.ui.home.supportChatManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.ChatMessageAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentChatMessageBinding
import com.desynatadmin.ui.home.supportChatManagement.response.ChatMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ChatMessageFragment : BaseFragment() {
    private var binding: FragmentChatMessageBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var type = 0
    private var chatList = ArrayList<ChatMessage>()
    private var chatMessageAdapter: ChatMessageAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentChatMessageBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Chats"

            chatMessageAdapter = ChatMessageAdapter(requireActivity(),chatList,object :
                ChatMessageAdapter.ChatClick{
                override fun onChatClick(position: Int) {

                }

            })
            rvChat.adapter = chatMessageAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

        }
    }

    override fun observe() {
    }
}

fun Any?.toSafeString(): String {
    return when (this) {
        is String -> this
        is Long -> this.toString()
        is Int -> this.toString()
        is Double -> this.toString()
        else -> ""
    }
}

fun Any?.toSafeInt(): Int {
    return when (this) {
        is Int -> this
        is Long -> this.toInt()
        is String -> this.toIntOrNull() ?: 0
        else -> 0
    }
}

fun Any?.toSafeLong(): Long {
    return when (this) {
        is Long -> this
        is Int -> this.toLong()
        is String -> this.toLongOrNull() ?: 0L
        else -> 0L
    }
}