package com.kotlin.travelhorizon.fragment

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kotlin.travelhorizon.R
import com.kotlin.travelhorizon.databinding.FragmentAddBinding
import com.kotlin.travelhorizon.dto.Dto
import com.kotlin.travelhorizon.repository.DataBaseManager
import com.kotlin.travelhorizon.util.GpsTracker
import com.kotlin.travelhorizon.util.Util


class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBinding.inflate(inflater, container, false)

        val calendar = Calendar.getInstance()

        setMenu()
        setCalendar(calendar)
        setSpinner(calendar)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLocation()

        binding.imgAddUptGPSInfo.setOnClickListener( {
            setLocation()
        })
        
        binding.imgAddEarth.setOnClickListener({
            val latitude = binding.inputLatitude.text.toString()
            val longitude = binding.inputLongitude.text.toString()

            Util.openMapApp(requireContext(), latitude, longitude)
        })

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val db = DataBaseManager(requireContext())

            val dto: Dto = Dto(
                id = System.currentTimeMillis(),
                date = binding.inputDate.text.toString(),
                year = binding.inputDate.text.toString().split("-")[0],
                hour = binding.spinnerHour.selectedItemPosition.toInt(),
                min = binding.spinnerMin.selectedItemPosition.toInt(),
                latitude = binding.inputLatitude.text.toString(),
                longitude = binding.inputLongitude.text.toString(),
                subject = binding.inputSubject.text.toString().trim(),
                revisitFlag = (if (binding.checkBox.isChecked) true else false),
                content = binding.inputContent.text.toString()
            )

            //Log.i("Dto: ", dto.toString())

            db.insert(dto)

            db.close()

            findNavController().navigate(R.id.action_AddFragment_to_ListFragment)
        }
    }  // override fun onViewCreated(view: View, savedInstanceState: Bundle?)

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
                menu.findItem(R.id.menu_modify).setVisible(false)
                menu.findItem(R.id.menu_search).setVisible(false)
                menu.findItem(R.id.menu_del).setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner)
    }

    private fun setCalendar(calendar: Calendar) {
        // calendar
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.inputDate.setText(String.format("%s-%s-%s", year.toString(), String.format("%02d", month+1), String.format("%02d", day)))

        binding.imageCalendar.setOnClickListener {
            val dateSplitList: List<String> = binding.inputDate.getText().split("-")

            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
                binding.inputDate.text = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day)
            }, dateSplitList[0].toInt(), dateSplitList[1].toInt()-1, dateSplitList[2].toInt())
            datePickerDialog.show()
        }
    }

    private fun setSpinner(calendar: Calendar) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val min = calendar.get(Calendar.MINUTE)

        // Hour spiner (select combo)
        val hourList: ArrayList<String> = arrayListOf<String>()
        for (i in 0..23)
            hourList.add(String.format("%02d", i))

        val adapterHour: ArrayAdapter<String> = ArrayAdapter<String>(
            activity?.applicationContext!!,
            android.R.layout.simple_spinner_item,
            hourList
        )
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinnerHour = binding.spinnerHour
        spinnerHour.adapter = adapterHour
//        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
//            override fun onNothingSelected(arg0: AdapterView<*>?) {}
//        })
        spinnerHour.setSelection(hour)

        // Min Spinner (select combo)
        val minList: ArrayList<String> = arrayListOf<String>()
        for (i in 0 .. 59)
            minList.add(String.format("%02d", i))
        val adapterMin: ArrayAdapter<String> = ArrayAdapter<String>(activity?.applicationContext!!, android.R.layout.simple_spinner_item, minList)
        adapterMin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinnerMin = binding.spinnerMin
        spinnerMin.adapter = adapterMin
//        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
//            override fun onNothingSelected(arg0: AdapterView<*>?) {}
//        })
        spinnerMin.setSelection(min)
    }

    private fun setLocation() {
        var gpsTracker: GpsTracker? = GpsTracker(requireContext())

        var latitude: Double = gpsTracker!!.getLatitude()
        var longitude: Double = gpsTracker!!.getLongtitude()

        if (latitude != 0.0 || longitude != 0.0) {
            var prevLatitude: Double = latitude
            var prevLongitude: Double = longitude

            var count = 0
            while (latitude == prevLatitude && longitude == prevLongitude) {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                gpsTracker = GpsTracker(requireContext())

                latitude = gpsTracker!!.getLatitude()
                longitude = gpsTracker!!.getLongtitude()

                if (count > 9)
                    break
                else
                    count++
            }
        }

        //println("############### latitude : " + latitude + "\n" + "###############  longitude: " + longitude)

        binding.inputLatitude.setText(latitude.toString())
        binding.inputLongitude.setText(longitude.toString())

        gpsTracker = null
    }

    /*private fun setLocation() {
        val locationMap: Map<String, String> = Util.getLocation(requireContext())

        binding.inputLatitude.setText(locationMap.get("latitude"))
        binding.inputLongitude.setText(locationMap.get("longitude"))

        //println("############### latitude : ${locationMap.get("latitude")} ###############  longitude: ${locationMap.get("longitude")}")
    }*/
}