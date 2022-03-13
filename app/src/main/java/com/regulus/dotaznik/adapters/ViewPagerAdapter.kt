package com.regulus.dotaznik.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.regulus.dotaznik.fragments.*

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> KontaktyFragment()
            1 -> DetailObjektuFragment()
            2 -> SystemFragment()
            3 -> BazenFragment()
            4 -> ZdrojeFragment()
            else -> KontaktyFragment()
        }
    }

}
