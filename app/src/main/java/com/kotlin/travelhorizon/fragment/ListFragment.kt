package com.kotlin.travelhorizon.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.kotlin.travelhorizon.MainActivity
import com.kotlin.travelhorizon.R
import com.kotlin.travelhorizon.databinding.FragmentListBinding
import com.kotlin.travelhorizon.repository.DataBaseManager
import com.kotlin.travelhorizon.util.Util
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception

/**
 * This page is made of tab and pager.
 */
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null

    private val binding get() = _binding!!

    private val REQ_CREATE_FILE = 100
    private val REQ_OPEN_FILE = 101

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
                    R.id.menu_export -> {
                        backup()
                        true
                    }
                    R.id.menu_import -> {
                        restore()
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

    /**
     * backup (export)
     */
    private fun backup() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, "travel_horizon")
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)))
        }
        startActivityForResult(intent, REQ_CREATE_FILE)
    }

    /**
     * restore (import)
      */
    private fun restore() {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setMessage(getResources().getString(R.string.confirm_restore))
            .setPositiveButton(getResources().getString(R.string.ok), { dialog, id ->  // ok button
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_TITLE, "travel_horizon")
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)))
            }
            startActivityForResult(intent, REQ_OPEN_FILE)
            })
            .setNegativeButton(getResources().getString(R.string.cancel), { dialog, id ->  // cancel button
            })

        builder.show()
    }

    /**
     * backup(export) and restore(import)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQ_CREATE_FILE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                try {
                    File(Util.URL_APP_DB).inputStream().use {  inputStream ->
                        requireActivity().contentResolver.openOutputStream(uri)?.use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.backup_complete),
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else if (requestCode == REQ_OPEN_FILE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                try {
                    File(Util.URL_APP_DB).outputStream().use { outputStream ->
                        requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.restore_complete),
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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