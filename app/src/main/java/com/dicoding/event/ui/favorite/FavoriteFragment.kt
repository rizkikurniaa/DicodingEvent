package com.dicoding.event.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.event.data.response.ListEventsItem
import com.dicoding.event.databinding.FragmentFavoriteBinding
import com.dicoding.event.ui.EventAdapter
import com.dicoding.event.ui.ViewModelFactory
import com.dicoding.event.ui.detail.DetailActivity

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val factory = ViewModelFactory.getInstance(requireContext())
            val viewModel = ViewModelProvider(this@FavoriteFragment, factory)[FavoriteViewModel::class.java]

            val layoutManager = LinearLayoutManager(requireContext())
            rvFavorite.layoutManager = layoutManager
            val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
            rvFavorite.addItemDecoration(itemDecoration)

            val adapter = EventAdapter { event ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
                startActivity(intent)
            }
            rvFavorite.adapter = adapter

            viewModel.getAllFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
                val items = favoriteEvents.map {
                    ListEventsItem(
                        id = it.id.toInt(),
                        name = it.name ?: "",
                        mediaCover = it.mediaCover ?: "",
                        summary = "",
                        registrants = 0,
                        imageLogo = "",
                        link = "",
                        description = "",
                        ownerName = "",
                        cityName = "",
                        quota = 0,
                        beginTime = "",
                        endTime = "",
                        category = ""
                    )
                }
                adapter.submitList(items)
                
                if (items.isEmpty()) {
                    viewError.errorLayoutRoot.visibility = View.VISIBLE
                    viewError.tvErrorTitle.text = "No Favorite Found"
                    viewError.tvErrorDesc.text = "You haven't added any events to favorite yet."
                    viewError.btnRetry.visibility = View.GONE
                } else {
                    viewError.errorLayoutRoot.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
