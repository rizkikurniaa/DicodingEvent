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
import com.dicoding.event.ui.ViewModelFactory
import com.dicoding.event.ui.detail.DetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

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

        with(binding) {
            val upcomingAdapter = EventAdapter(EventAdapter.VIEW_TYPE_HORIZONTAL) { event ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
                startActivity(intent)
            }
            rvUpcomingHome.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvUpcomingHome.adapter = upcomingAdapter

            val finishedAdapter = EventAdapter(EventAdapter.VIEW_TYPE_VERTICAL) { event ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
                startActivity(intent)
            }
            rvFinishedHome.layoutManager = LinearLayoutManager(requireContext())
            rvFinishedHome.adapter = finishedAdapter

            homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
                if (events.isEmpty()) {
                    // Logic jika upcoming kosong bisa ditambahkan di sini, misal hide label
                    tvUpcomingLabel.visibility = View.GONE
                    rvUpcomingHome.visibility = View.GONE
                } else {
                    tvUpcomingLabel.visibility = View.VISIBLE
                    rvUpcomingHome.visibility = View.VISIBLE
                    upcomingAdapter.submitList(events.take(5))
                }
            }

            homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
                finishedAdapter.submitList(events.take(5))
            }

            homeViewModel.isLoadingUpcoming.observe(viewLifecycleOwner) {
                showLoading(it)
            }
            
            homeViewModel.isLoadingFinished.observe(viewLifecycleOwner) {
                showLoading(it)
            }

            homeViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
                if (message != null) {
                    viewError.errorLayoutRoot.visibility = View.VISIBLE
                } else {
                    viewError.errorLayoutRoot.visibility = View.GONE
                }
            }

            viewError.btnRetry.setOnClickListener {
                homeViewModel.fetchUpcomingEvents()
                homeViewModel.fetchFinishedEvents()
            }
        }

        if (homeViewModel.upcomingEvents.value == null) homeViewModel.fetchUpcomingEvents()
        if (homeViewModel.finishedEvents.value == null) homeViewModel.fetchFinishedEvents()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
