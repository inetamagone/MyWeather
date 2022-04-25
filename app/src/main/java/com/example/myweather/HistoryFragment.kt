package com.example.myweather

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.adapter.HistoryViewAdapter
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.HistoryWeatherRepository
import com.example.myweather.viewModels.factories.HistoryModelFactory
import com.example.myweather.viewModels.HistoryViewModel

private const val TAG = "HistoryFragment"

class HistoryFragment : Fragment() {

    private lateinit var viewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Add menu
        setHasOptionsMenu(true)
        Log.d(TAG, "onCreateView")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        val repository = HistoryWeatherRepository(CurrentWeatherDatabase(requireContext()))
        val factory = HistoryModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recycler_view)
        val adapter = HistoryViewAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getAllHistory()
            .observe(viewLifecycleOwner) {
                observeData(it, adapter)
            }
        swipeDelete(recyclerView, adapter)
    }

    // Swipe delete method
    private fun swipeDelete(recyclerView: RecyclerView, adapter: HistoryViewAdapter) {
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.differ.currentList[position]
                val count = adapter.differ.currentList.size
                viewModel.deleteEntry(item)
//                adapter.notifyItemRemoved(position)
//                adapter.notifyItemChanged(position)
                adapter.notifyItemRangeChanged(position, count)

                Toast.makeText(
                    requireContext(),
                    "Weather entry deleted!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    // Delete all and filtering methods
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val recyclerView = requireActivity().findViewById<RecyclerView>(R.id.history_recycler_view)
        val adapter = HistoryViewAdapter()
        recyclerView.adapter = adapter
        when (item.itemId) {
            R.id.menu_delete -> deleteAllHistory()
            R.id.filter_name1 -> viewModel.filterItems(1)
                .observe(viewLifecycleOwner) {
                    observeData(it, adapter)
                }
            R.id.filter_name2 -> viewModel.filterItems(2)
                .observe(viewLifecycleOwner) {
                    observeData(it, adapter)
                }
            R.id.filter_temp1 -> viewModel.filterItems(3)
                .observe(viewLifecycleOwner) {
                    observeData(it, adapter)
                }
            R.id.filter_temp2 -> viewModel.filterItems(4)
                .observe(viewLifecycleOwner) {
                    observeData(it, adapter)
                }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeData(it: List<CurrentWeatherData>, adapter: HistoryViewAdapter) {
        if (it == null) {
            Log.d(TAG, "Data was not found!")
        } else {
            it.let {
                adapter.differ.submitList(it)
                Log.d(TAG, "Data submitted to adapter!")
            }
        }
    }

    private fun deleteAllHistory() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("OK") { _, _ ->
            viewModel.deleteAllHistory()
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