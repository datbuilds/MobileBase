package com.mobile.base.screens.home

import android.view.View
import com.mobile.base.activity.MainActivity
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.databinding.FragmentHomeBinding

class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun initView(view: View) {
        (activity as? MainActivity)?.startSessionTimer()
    }

    override fun initListener() {
        binding.btnLogout.setOnClickListener { logout() }
    }

    override fun initObserve() = Unit
}
