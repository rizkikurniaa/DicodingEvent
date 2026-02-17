package com.dicoding.event.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.event.R
import com.dicoding.event.data.response.ListEventsItem
import com.dicoding.event.databinding.ActivityDetailBinding
import com.dicoding.event.ui.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isFavorite = false
    private var currentEvent: ListEventsItem? = null

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        if (eventId != null) {
            detailViewModel.getDetailEvent(eventId)
            detailViewModel.getFavoriteById(eventId).observe(this) { favorite ->
                isFavorite = favorite != null
                invalidateOptionsMenu()
            }
        }

        detailViewModel.eventDetail.observe(this) { event ->
            currentEvent = event
            setEventData(event)
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        detailViewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                val errorText = getString(R.string.error_message, message)
                Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        val favoriteItem = menu?.findItem(R.id.action_favorite)
        if (isFavorite) {
            favoriteItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite)
            favoriteItem?.icon?.setTint(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            favoriteItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite)
            favoriteItem?.icon?.setTint(ContextCompat.getColor(this, R.color.navy_blue))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_favorite -> {
                currentEvent?.let {
                    if (isFavorite) {
                        detailViewModel.deleteFavorite(it)
                        Toast.makeText(this, "Removed from Favorite", Toast.LENGTH_SHORT).show()
                    } else {
                        detailViewModel.setFavorite(it)
                        Toast.makeText(this, "Added to Favorite", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setEventData(event: ListEventsItem) {
        binding.tvDetailName.text = event.name
        binding.tvDetailOwner.text = event.ownerName
        binding.tvDetailTime.text = event.beginTime

        val remainingQuota = event.quota - event.registrants
        binding.tvDetailQuota.text = getString(R.string.quota_format, remainingQuota)

        binding.tvDetailDescription.text = HtmlCompat.fromHtml(
            event.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        Glide.with(this)
            .load(event.mediaCover)
            .into(binding.imgDetailPhoto)

        binding.btnOpenLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = event.link.toUri()
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
