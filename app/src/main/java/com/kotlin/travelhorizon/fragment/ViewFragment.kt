package com.kotlin.travelhorizon.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.kotlin.travelhorizon.R
import com.kotlin.travelhorizon.databinding.FragmentViewBinding
import com.kotlin.travelhorizon.dto.Dto
import com.kotlin.travelhorizon.repository.DataBaseManager

class ViewFragment : Fragment() {

    private var _binding: FragmentViewBinding? = null
    private val binding get() = _binding!!

    lateinit var db: DataBaseManager
    lateinit private var id: String  // data record id (pk)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentViewBinding.inflate(inflater, container, false)

        this.id = arguments?.getString("id") ?: ""

        this.db = DataBaseManager(requireContext())

        setMenu()
        setData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgViewEarth.setOnClickListener({
            val latitude = binding.viewLatitude.text.toString()
            val longitude = binding.viewLongitude.text.toString()

            val uri = getString(R.string.app_mapp_url, latitude, longitude, latitude, longitude)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        })

        binding.btnPrev.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMenu() {
        // menu
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.menu_add).setVisible(false)
                menu.findItem(R.id.menu_modify).setVisible(true)
                menu.findItem(R.id.menu_search).setVisible(false)
                menu.findItem(R.id.menu_del).setVisible(true)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_modify -> {
                        val bundle = bundleOf("id" to this@ViewFragment.id)
                        Navigation.findNavController(requireView()).navigate(R.id.action_viewFragment_to_modifyFragment, bundle)

                        true
                    }
                    R.id.menu_del -> {
                        val builder = AlertDialog.Builder(requireActivity())

                        builder.setMessage(getResources().getString(R.string.confirm_del_data))
                                .setPositiveButton("OK", { dialog, id ->
                                    val paramId = arguments?.getString("id")

                                    if (paramId != null)
                                        db.delete(paramId)

                                    dialog.dismiss()

                                    Navigation.findNavController(requireView()).navigate(R.id.action_viewFragment_to_ListFragment)
                                })
                                .setNegativeButton("Cancel", { dialog, id ->
                                })

                        builder.show()

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }  // private fun setMenu()

    private fun setData() {
        val dto: Dto = db.select(this.id)

        binding.viewDate.text = dto.date
        binding.viewHour.text = String.format("%02d", dto.hour)
        binding.viewMin.text = String.format("%02d", dto.min)
        binding.viewLatitude.text = dto.latitude
        binding.viewLongitude.text = dto.longitude
        binding.viewCheck.text = if (dto.revisitFlag) "\u2611" else "\u2610"
        binding.viewSubject.text = dto.subject
        binding.viewContent.text = dto.content
    }
}