package com.desynatadmin.ui.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentHomeBinding
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notify

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    private var isObserver = true
    var user : LoginResponse.Data?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        user = SharedPreference.get(requireActivity()).getUser()
       binding?.apply {
           tvName.text = "Hi ${user?.fullName}"
       }
    }

    override fun initControl() {
        binding?.apply {
            ivProfileImage.setOnClickListener {
                (requireActivity() as? HomeActivity)?.openDrawer()
            }
        }
    }

    override fun observe() {
    }


}