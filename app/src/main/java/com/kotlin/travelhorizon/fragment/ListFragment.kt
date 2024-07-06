package com.kotlin.travelhorizon.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.kotlin.travelhorizon.MainActivity
import com.kotlin.travelhorizon.repository.DataBaseManager
import com.kotlin.travelhorizon.R
import com.kotlin.travelhorizon.databinding.FragmentListBinding

/**
 * This page is made of tab and pager.
 */
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)

        setMenu()
        setTabAndPager()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.menu_modify).setVisible(false)
                menu.findItem(R.id.menu_del).setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_add -> {
                        (context as MainActivity).showHideProgressBar(true)
                        Navigation.findNavController(requireView()).navigate(R.id.action_ListFragment_to_AddFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun setTabAndPager() {
        val db = DataBaseManager(requireContext())

        val tabList: List<String> = db.selectGroupbyYearList()

//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(tabList.get(0)))

        if (tabList.size > 0) {
            val adapter = PagerFragmentStateAdapter(requireActivity())

            for (i in 0..tabList.size - 1)
                adapter.addFragment(TabListFragment(tabList[i]))

            //binding.viewPager.adapter = adapter
            binding.viewPager.setAdapter(adapter)

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
                tab.text = tabList[pos]
                //tab.setIcon(tabIconList[pos])
            }.attach()
        } else {
            binding.tabLayout.visibility = View.GONE
            binding.viewPager.visibility = View.GONE
            binding.labelNoData.visibility = View.VISIBLE
        }
    }

    private inner class PagerFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        val fragments : ArrayList<Fragment> = ArrayList()

        override fun getItemCount(): Int {
            return this.fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]

//            return when (position) {
//                        0 -> TabFragment()
//                        1 -> TabFragment2()
//                        2 -> TabFragment3()
//                        else -> throw IndexOutOfBoundsException()
//            }
        }

        fun addFragment(fragment: Fragment) {
            fragments.add(fragment)
        }

        fun removeFragment() {
            fragments.removeLast()
        }
    }
}