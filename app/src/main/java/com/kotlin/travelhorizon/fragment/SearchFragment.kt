package com.kotlin.travelhorizon.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.MenuHost
import com.kotlin.travelhorizon.R
import com.kotlin.travelhorizon.databinding.FragmentSearchBinding
import com.kotlin.travelhorizon.repository.DataBaseManager
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.travelhorizon.adapter.SearchRecordAdapter
import com.kotlin.travelhorizon.dto.Dto

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    lateinit private var db: DataBaseManager

    private lateinit var adapter: SearchRecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        this.db = DataBaseManager(requireContext())

        setMenu()

        val layoutManager = LinearLayoutManager(requireContext())

        binding.searchListRecyclerView.layoutManager = layoutManager
        binding.searchListRecyclerView.itemAnimator = DefaultItemAnimator()

        // Add a neat dividing line between items in the list
        binding.searchListRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        adapter = SearchRecordAdapter(mutableListOf<Dto>())
        binding.searchListRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSearch.setOnClickListener {
            exeSearch()
        }

        binding.searchText.setOnEditorActionListener { textView, actionId, event ->
            //Log.d("EditTextHandler", "actionId: $actionId")
            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                val userInput = textView.text.toString()
//                Log.d("EditTextHandler", "input value: $userInput")

                // hide keyboard
                val imm = requireContext().getSystemService(InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(textView.windowToken, 0)

                exeSearch()
            }

            return@setOnEditorActionListener true
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        db.close()
        _binding = null
    }

    private fun setMenu() {
        // menu
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.menu_add).setVisible(false)
                menu.findItem(R.id.menu_modify).setVisible(false)
                menu.findItem(R.id.menu_search).setVisible(false)
                menu.findItem(R.id.menu_del).setVisible(false)
                menu.findItem(R.id.menu_export).setVisible(false)
                menu.findItem(R.id.menu_export).setVisible(false)
                menu.findItem(R.id.menu_import).setVisible(false)
                menu.findItem(R.id.menu_vertion).setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner)
    }  // private fun setMenu()


    private fun exeSearch() {
        val db = DataBaseManager(requireContext())

        val searchList: MutableList<Dto> = db.searchtList(binding.searchText.text.toString())

        adapter.updateList(searchList)

        db.close()
    }

}