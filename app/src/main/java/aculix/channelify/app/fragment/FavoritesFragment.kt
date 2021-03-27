package aculix.channelify.app.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View

import aculix.channelify.app.R
import aculix.channelify.app.activity.VideoPlayerActivity
import aculix.channelify.app.fastadapteritems.FavoriteItem
import aculix.channelify.app.viewmodel.FavoritesViewModel
import aculix.core.extensions.openUrl
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.itemanimators.AlphaInAnimator
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.item_favorite.view.*
import kotlinx.android.synthetic.main.widget_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel by viewModel<FavoritesViewModel>() // Lazy inject ViewModel

    private lateinit var favoritesAdapter: FastItemAdapter<FavoriteItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        setupRecyclerView(savedInstanceState)
        setupObservables()
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        outState = favoritesAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun setupToolbar() {
        ablFavorites.toolbarMain.apply {
            inflateMenu(R.menu.main_menu)

            // Store and Search configuration
            menu.findItem(R.id.miStoreMainMenu).isVisible = resources.getBoolean(R.bool.enable_store)
            menu.findItem(R.id.miSearchMainMenu).isVisible = resources.getBoolean(R.bool.enable_search)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.miStoreMainMenu -> {
                        context.openUrl(getString(R.string.store_url), R.color.defaultBgColor)
                    }
                    R.id.miSearchMainMenu -> {
                        findNavController().navigate(R.id.action_favoritesFragment_to_searchFragment)
                    }
                }
                false
            }
        }
    }

    private fun setupRecyclerView(savedInstanceState: Bundle?) {
        favoritesAdapter = FastItemAdapter()
        favoritesAdapter.setHasStableIds(true)
        favoritesAdapter.withSavedInstanceState(savedInstanceState)

        rvFavorites.layoutManager = LinearLayoutManager(context)
        rvFavorites.itemAnimator = AlphaInAnimator()
        rvFavorites.adapter = favoritesAdapter
        rvFavorites.itemAnimator = AlphaInAnimator()

        onFavoriteClick()
        onItemClick()
    }

    private fun setupObservables() {
        viewModel.favoriteVideosLiveData.observe(viewLifecycleOwner, Observer { favoriteVideoList ->
            val favoriteItemsList = ArrayList<FavoriteItem>()
            for (favoriteVideo in favoriteVideoList) {
                favoriteItemsList.add(FavoriteItem(favoriteVideo))
            }

            favoritesAdapter.add(favoriteItemsList)
            showEmptyState(favoritesAdapter.itemCount)
        })
    }

    private fun showEmptyState(itemCount: Int) {
        groupEmptyFavorites.isVisible = itemCount < 1
    }

    /**
     * Called when the Heart Icon is clicked of a RecyclerView Item
     */
    private fun onFavoriteClick() {
        favoritesAdapter.addEventHook(object : ClickEventHook<FavoriteItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return if (viewHolder is FavoriteItem.FavoriteViewHolder) {
                    viewHolder.itemView.ivHeartFavoriteItem
                } else {
                    null
                }
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<FavoriteItem>,
                item: FavoriteItem
            ) {
                val favoriteIcon = v as AppCompatImageView

                if (item.favoriteVideo.isChecked) {
                    // Icon unchecked
                    item.favoriteVideo.isChecked = false
                    favoriteIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_favorite_border
                        )
                    )
                    viewModel.removeVideoFromFavorites(item.favoriteVideo)
                } else {
                    // Icon checked
                    item.favoriteVideo.isChecked = true
                    favoriteIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_favorite_filled_border
                        )
                    )
                    viewModel.addVideoToFavorites(item.favoriteVideo)
                }
            }
        })
    }

    /**
     * Called when an item of the RecyclerView is clicked
     */
    private fun onItemClick() {
        favoritesAdapter.onClickListener = { view, adapter, item, position ->
            VideoPlayerActivity.startActivity(context, item.favoriteVideo.id)
            false
        }
    }
}
