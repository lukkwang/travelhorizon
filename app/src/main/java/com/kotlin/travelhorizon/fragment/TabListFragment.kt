package com.kotlin.travelhorizon.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.travelhorizon.adapter.RecordAdapter
import com.kotlin.travelhorizon.databinding.TabListFragmentBinding
import com.kotlin.travelhorizon.dto.Dto
import com.kotlin.travelhorizon.repository.DataBaseManager

/**
 * record list in one tab list page
 * list is made of RecyclerView.
 */
class TabListFragment(private val year: String): Fragment() {

    private var _binding: TabListFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = TabListFragmentBinding.inflate(inflater, container, false)

        val db = DataBaseManager(requireContext())

        val recordList: List<Dto> = db.selectList(this.year)

        val adapter = RecordAdapter(recordList)

        val layoutManager = LinearLayoutManager(requireContext())

        binding.recordListRecyclerView.layoutManager = layoutManager
        binding.recordListRecyclerView.itemAnimator = DefaultItemAnimator()

        // Add a neat dividing line between items in the list
        binding.recordListRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        // set the adapter
        binding.recordListRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}