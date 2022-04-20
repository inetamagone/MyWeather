package com.example.myweather

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.adapter.HistoryViewAdapter
import com.example.myweather.viewModels.CurrentWeatherViewModel

private const val TAG = "HistoryFragment"

class HistoryFragment : Fragment() {

    private lateinit var currentViewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        currentViewModel = ViewModelProvider(this)[CurrentWeatherViewModel::class.java]
        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recycler_view)
        val adapter = HistoryViewAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        currentViewModel.getAllHistory(requireContext())
            .observe(viewLifecycleOwner) { history ->
                if (history == null) {
                    Log.d(TAG, "History data was not found!")
                } else {
                    history.let {
                        adapter.differ.submitList(history)
                        Log.d(TAG, "Data submitted to adapter!")
                    }
                }
            }

        // Add menu
        setHasOptionsMenu(true)

        return view
    }

    // Delete all method
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            deleteAllHistory(requireContext())
        }
        return super.onOptionsItemSelected(item)
    }
    private fun deleteAllHistory(context: Context) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("OK") { _, _ ->
            currentViewModel.deleteAllHistory(context)
            Toast.makeText(
                requireContext(),
                "All Weather History is removed",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete All")
        builder.setMessage("Do you want to delete all history?")
        builder.create().show()
    }
}