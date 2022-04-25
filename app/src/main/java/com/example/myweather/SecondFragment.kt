package com.example.myweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.adapter.DateViewAdapter
import com.example.myweather.viewModels.DateViewModel

private var lat: String = ""
private var lon: String = ""

class SecondFragment : Fragment() {
    private lateinit var viewModel: DateViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DateViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Getting String values from the FirstFragment
        val cityString = requireArguments().getString("cityName")
        val latString = requireArguments().getString("latString")
        val lonString = requireArguments().getString("lonString")

        // View title text
        requireActivity().findViewById<TextView>(R.id.title_text).text =
            resources.getString(R.string.weather_in_city, cityString)

        lat = latString.toString()
        lon = lonString.toString()

        viewModel = ViewModelProvider(this)[DateViewModel::class.java]
        recyclerView = view.findViewById(R.id.recycler_view)

        viewModel.getDateWeatherApi(requireContext(), lat, lon)
        viewModel.getAllByDate(requireContext())
            .observe(viewLifecycleOwner) {
                    it.let {
                        adapter = DateViewAdapter(it)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    }
                }
        viewModel.deleteAllDateList(requireContext())
    }
}