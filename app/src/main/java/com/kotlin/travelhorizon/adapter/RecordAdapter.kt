package com.kotlin.travelhorizon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.travelhorizon.R
import com.kotlin.travelhorizon.databinding.ListItemBinding
import com.kotlin.travelhorizon.dto.Dto

class RecordAdapter(private val recordList: List<Dto>): RecyclerView.Adapter<RecordAdapter.ListItemHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {

        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ListItemHolder(binding)
    }

    override fun getItemCount(): Int {
        return recordList?.size ?: -1
    }

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        holder.bind(recordList[position])
    }

    inner class ListItemHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.recordListItem.setOnClickListener(View.OnClickListener {
                //val pos = adapterPosition
                //Log.d("click", pos.toString() + " : click!")

                val bundle = bundleOf("id" to binding.id.text.toString())

                //Log.d("click id: ", binding.id.text.toString())

                findNavController(it).navigate(R.id.action_ListFragment_to_ViewFragment, bundle)
            })

//            binding.recordListItem.setOnLongClickListener(View.OnLongClickListener {
//                true
//            })
        }

        // item data set in the list
        fun bind(dto: Dto) {
            binding.id.text = dto.id.toString()
            binding.dateTime.text = dto.date + " " + String.format("%02d", dto.hour) + ":" + String.format("%02d", dto.min)
            binding.revisit.text = if (dto.revisitFlag) "\u2714" else ""
            binding.subject.text = dto.subject
        }
    }
}