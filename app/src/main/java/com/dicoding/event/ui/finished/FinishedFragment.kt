package com.dicoding.event.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.event.databinding.FragmentEventListBinding
import com.dicoding.event.ui.EventAdapter
import com.dicoding.event.ui.EventViewModel
import com.dicoding.event.ui.ViewModelFactory
import com.dicoding.event.ui.detail.DetailActivity

class FinishedFragment : Fragment() {

    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel by viewModels<EventViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvEvents.addItemDecoration(itemDecoration)

        val adapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
            startActivity(intent)
        }
        binding.rvEvents.adapter = adapter

        eventViewModel.listEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.viewError.errorLayoutRoot.visibility = View.VISIBLE
            } else {
                binding.viewError.errorLayoutRoot.visibility = View.GONE
                adapter.submitList(events)
            }
        }

        eventViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
            if (it) binding.viewError.errorLayoutRoot.visibility = View.GONE
        }

        eventViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                binding.viewError.errorLayoutRoot.visibility = View.VISIBLE
            } else {
                binding.viewError.errorLayoutRoot.visibility = View.GONE
            }
        }

        binding.viewError.btnRetry.setOnClickListener {
            eventViewModel.findEvents(0)
        }

        if (eventViewModel.listEvents.value == null) {
            eventViewModel.findEvents(0)
        }

        binding.edSearch.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString()
                eventViewModel.findEvents(0, query)
                true
            } else {
                false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
