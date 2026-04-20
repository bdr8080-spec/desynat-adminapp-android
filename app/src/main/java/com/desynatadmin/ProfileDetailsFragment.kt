package com.desynatadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.desynatadmin.databinding.FragmentProfileDetailsBinding

class ProfileDetailsFragment : Fragment() {
    private val binding: FragmentProfileDetailsBinding by lazy {
        FragmentProfileDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbar.tvToolbar.text = "Profile Details"
    }
}