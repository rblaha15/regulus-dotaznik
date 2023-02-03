package com.regulus.dotaznik.dotaznik

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.regulus.dotaznik.dotaznik.pages.BazenFragment
import com.regulus.dotaznik.dotaznik.pages.DetailObjektuFragment
import com.regulus.dotaznik.dotaznik.pages.KontaktyFragment
import com.regulus.dotaznik.dotaznik.pages.PrislusenstviFragment
import com.regulus.dotaznik.dotaznik.pages.SystemFragment
import com.regulus.dotaznik.dotaznik.pages.ZdrojeFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> KontaktyFragment()
            1 -> DetailObjektuFragment()
            2 -> SystemFragment()
            3 -> BazenFragment()
            4 -> ZdrojeFragment()
            5 -> PrislusenstviFragment()
            else -> KontaktyFragment()
        }
    }

}
