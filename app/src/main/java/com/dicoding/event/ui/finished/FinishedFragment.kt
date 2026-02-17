package com.dicoding.event.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.event.R
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

        with(binding) {
            val layoutManager = LinearLayoutManager(requireContext())
            rvEvents.layoutManager = layoutManager
            val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
            rvEvents.addItemDecoration(itemDecoration)

            val adapter = EventAdapter { event ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
                startActivity(intent)
            }
            rvEvents.adapter = adapter

            eventViewModel.listEvents.observe(viewLifecycleOwner) { events ->
                if (events.isNullOrEmpty()) {
                    adapter.submitList(emptyList())
                    showError(getString(R.string.no_data_found), getString(R.string.no_data_found_desc), false)
                } else {
                    viewError.errorLayoutRoot.visibility = View.GONE
                    adapter.submitList(events)
                }
            }

            eventViewModel.isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
                if (it) viewError.errorLayoutRoot.visibility = View.GONE
            }

            eventViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
                if (message != null) {
                    adapter.submitList(emptyList())
                    showError(getString(R.string.error_title), getString(R.string.error_desc), true)
                }
            }

            viewError.btnRetry.setOnClickListener {
                eventViewModel.findEvents(0)
            }

            eventViewModel.findEvents(0)

            edSearch.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = textView.text.toString()
                    eventViewModel.findEvents(0, query)
                    true
                } else {
                    false
                }
            }

            edSearch.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    eventViewModel.findEvents(0)
                }
            }
        }
    }

    private fun showError(title: String, desc: String, showRetry: Boolean) {
        with(binding.viewError) {
            errorLayoutRoot.visibility = View.VISIBLE
            tvErrorTitle.text = title
            tvErrorDesc.text = desc
            btnRetry.visibility = if (showRetry) View.VISIBLE else View.GONE
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
