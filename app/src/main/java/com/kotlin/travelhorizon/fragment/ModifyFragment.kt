package com.kotlin.travelhorizon.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import com.kotlin.travelhorizon.MainActivity
import com.kotlin.travelhorizon.R
import com.kotlin.travelhorizon.databinding.FragmentModifyBinding
import com.kotlin.travelhorizon.dto.Dto
import com.kotlin.travelhorizon.repository.DataBaseManager
import com.kotlin.travelhorizon.util.GpsTracker
import com.kotlin.travelhorizon.util.Util

class ModifyFragment : Fragment() {

    private var _binding: FragmentModifyBinding? = null

    private val binding get() = _binding!!

    lateinit private var db: DataBaseManager
    lateinit private var id: String  // data record id (pk)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentModifyBinding.inflate(inflater, container, false)

        this.id = arguments?.getString("id") ?: ""

        this.db = DataBaseManager(requireContext())

        setMenu()
        setCalendar()
        setSpinner()
        setData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgModUptGPSInfo.setOnClickListener( {
            (context as MainActivity).showHideProgressBar(true)

            Handler(Looper.getMainLooper()).postDelayed({
                setLocation()                //실행할 코드
            }, 0)
        })

        binding.imgModEarth.setOnClickListener({
            val latitude = binding.inputLatitude2.text.toString()
            val longitude = binding.inputLongitude2.text.toString()

            Util.openMapApp(requireContext(), latitude, longitude)
        })

        binding.btnCancel2.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave2.setOnClickListener {
            val dto = Dto(
                id = this.id.toLong(),
                date = binding.inputDate2.text.toString(),
                year = binding.inputDate2.text.toString().split("-")[0],
                hour = binding.spinnerHour2.selectedItemId.toInt(),
                min = binding.spinnerMin2.selectedItemId.toInt(),
                latitude = binding.inputLatitude2.text.toString(),
                longitude = binding.inputLongitude2.text.toString(),
                subject = binding.inputSubject2.text.toString().trim(),
                revisitFlag = (if (binding.checkBox2.isChecked) true else false),
                content = binding.inputContent2.text.toString()
            )

            //Log.i("Dto: ", dto.toString())

            db.update(dto)

            findNavController().navigate(R.id.action_modifyFragment_to_ListFragment)
        }
    }  // override fun onViewCreated(view: View, savedInstanceState: Bundle?)

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
                menu.findItem(R.id.menu_import).setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner)
    }

    private fun setCalendar() {
        binding.imageCalendar2.setOnClickListener {
            val dateSplitList: List<String> = binding.inputDate2.getText().split("-")

            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
                binding.inputDate2.text = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day)
            }, dateSplitList[0].toInt(), dateSplitList[1].toInt()-1, dateSplitList[2].toInt())
            datePickerDialog.show()
        }
    }

    private fun setSpinner() {
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
        val spinnerHour = binding.spinnerHour2
        spinnerHour.adapter = adapterHour
//        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
//            override fun onNothingSelected(arg0: AdapterView<*>?) {}
//        })

        // Min Spinner (select combo)
        val minList: ArrayList<String> = arrayListOf<String>()
        for (i in 0 .. 59)
            minList.add(String.format("%02d", i))
        val adapterMin: ArrayAdapter<String> = ArrayAdapter<String>(activity?.applicationContext!!, android.R.layout.simple_spinner_item, minList)
        adapterMin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinnerMin = binding.spinnerMin2
        spinnerMin.adapter = adapterMin
//        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
//            override fun onNothingSelected(arg0: AdapterView<*>?) {}
//        })
    }

    private fun setData() {
        val dto: Dto = db.select(this.id)

        binding.inputDate2.text = dto.date
        binding.spinnerHour2.setSelection(dto.hour)
        binding.spinnerMin2.setSelection(dto.min)
        binding.checkBox2.isChecked = dto.revisitFlag
        binding.inputLatitude2.setText(dto.latitude)
        binding.inputLongitude2.setText(dto.longitude)
        binding.inputSubject2.setText(dto.subject)
        binding.inputContent2.setText(dto.content)
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

        binding.inputLatitude2.setText(latitude.toString())
        binding.inputLongitude2.setText(longitude.toString())

        (context as MainActivity).showHideProgressBar(false)

        gpsTracker = null
    }

    /*private fun setLocation() {
        val locationMap: Map<String, String> = Util.getLocation(requireContext())

        binding.inputLatitude2.setText(locationMap.get("latitude"))
        binding.inputLongitude2.setText(locationMap.get("longitude"))

        //println("############### latitude : ${locationMap.get("latitude")} ###############  longitude: ${locationMap.get("longitude")}")
    }*/
}