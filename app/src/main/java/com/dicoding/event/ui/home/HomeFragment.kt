package com.dicoding.event.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.event.databinding.FragmentHomeBinding
import com.dicoding.event.ui.EventAdapter
import com.dicoding.event.ui.EventViewModel
import com.dicoding.event.ui.detail.DetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter horizontal untuk Upcoming
        val upcomingAdapter = EventAdapter(EventAdapter.VIEW_TYPE_HORIZONTAL) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
            startActivity(intent)
        }
        binding.rvUpcomingHome.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingHome.adapter = upcomingAdapter

        // Adapter vertikal untuk Finished
        val finishedAdapter = EventAdapter(EventAdapter.VIEW_TYPE_VERTICAL) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
            startActivity(intent)
        }
        binding.rvFinishedHome.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinishedHome.adapter = finishedAdapter

        val upcomingViewModel by viewModels<EventViewModel>()
        val finishedViewModel by viewModels<EventViewModel>()

        upcomingViewModel.listEvents.observe(viewLifecycleOwner) { events ->
            upcomingAdapter.submitList(events.take(5))
        }

        finishedViewModel.listEvents.observe(viewLifecycleOwner) { events ->
            finishedAdapter.submitList(events.take(5))
        }

        upcomingViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        
        finishedViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        if (upcomingViewModel.listEvents.value == null) upcomingViewModel.findEvents(1)
        if (finishedViewModel.listEvents.value == null) finishedViewModel.findEvents(0)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
