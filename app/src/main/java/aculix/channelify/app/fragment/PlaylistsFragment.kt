package aculix.channelify.app.fragment


import aculix.channelify.app.R
import aculix.channelify.app.fastadapteritems.PlaylistItem
import aculix.channelify.app.fastadapteritems.ProgressIndicatorItem
import aculix.channelify.app.model.Playlist
import aculix.channelify.app.paging.Status
import aculix.channelify.app.utils.DividerItemDecorator
import aculix.channelify.app.viewmodel.PlaylistsViewModel
import aculix.core.extensions.makeGone
import aculix.core.extensions.makeVisible
import aculix.core.extensions.openUrl
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.paged.PagedModelAdapter
import kotlinx.android.synthetic.main.fragment_playlists.*
import kotlinx.android.synthetic.main.widget_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass to show the list of
 * Playlists of a channel.
 */
class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    private val viewModel by viewModel<PlaylistsViewModel>() // Lazy inject ViewModel

    private lateinit var playlistsAdapter: GenericFastAdapter
    private lateinit var playlistsPagedModelAdapter: PagedModelAdapter<Playlist.Item, PlaylistItem>
    private lateinit var footerAdapter: GenericItemAdapter
    private var isFirstPageLoading = true
    private var retrySnackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        setupRecyclerView(savedInstanceState)
        fetchPlaylists()
        setupObservables()
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        outState = playlistsAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        retrySnackbar?.dismiss() // Dismiss the retrySnackbar if already present
    }

    private fun setupToolbar() {
        ablPlaylists.toolbarMain.apply {
            inflateMenu(R.menu.main_menu)

            // Store and Search configuration
            menu.findItem(R.id.miStoreMainMenu).isVisible =
                resources.getBoolean(R.bool.enable_store)
            menu.findItem(R.id.miSearchMainMenu).isVisible =
                resources.getBoolean(R.bool.enable_search)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.miStoreMainMenu -> {
                        context.openUrl(getString(R.string.store_url), R.color.defaultBgColor)
                    }
                    R.id.miSearchMainMenu -> {
                        findNavController().navigate(R.id.action_playlistsFragment_to_searchFragment)
                    }
                }
                false
            }
        }
    }

    private fun setupRecyclerView(savedInstanceState: Bundle?) {
        val asyncDifferConfig = AsyncDifferConfig.Builder<Playlist.Item>(object :
            DiffUtil.ItemCallback<Playlist.Item>() {
            override fun areItemsTheSame(
                oldItem: Playlist.Item,
                newItem: Playlist.Item
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Playlist.Item,
                newItem: Playlist.Item
            ): Boolean {
                return oldItem == newItem
            }
        }).build()

        playlistsPagedModelAdapter =
            PagedModelAdapter<Playlist.Item, PlaylistItem>(asyncDifferConfig) {
                PlaylistItem(it)
            }

        footerAdapter = ItemAdapter.items()

        playlistsAdapter = FastAdapter.with(listOf(playlistsPagedModelAdapter, footerAdapter))
        playlistsAdapter.registerTypeInstance(PlaylistItem(null))
        playlistsAdapter.withSavedInstanceState(savedInstanceState)

        rvPlaylists.layoutManager = LinearLayoutManager(context)
        rvPlaylists.addItemDecoration(
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.view_divider_item_decorator
                )!!
            )
        )
        rvPlaylists.adapter = playlistsAdapter
        onItemClick()
    }

    private fun setupObservables() {
        // Observe Empty State LiveData
        viewModel.emptyStateLiveData.observe(viewLifecycleOwner, Observer { isResultEmpty ->
            if (isResultEmpty) {
                showEmptyState()
            } else {
                hideEmptyState()
            }
        })

        // Observe network live data
        viewModel.networkStateLiveData?.observe(viewLifecycleOwner, Observer { networkState ->
            when (networkState?.status) {
                Status.FAILED -> {
                    footerAdapter.clear()
                    pbPlaylists.makeGone()
                    createRetrySnackbar()
                    retrySnackbar?.show()
                }
                Status.SUCCESS -> {
                    footerAdapter.clear()
                    pbPlaylists.makeGone()
                }
                Status.LOADING -> {
                    if (!isFirstPageLoading) {
                        showRecyclerViewProgressIndicator()
                    } else {
                        isFirstPageLoading = false
                    }
                }
            }
        })

        // Observe latest video live data
        viewModel.playlistsLiveData?.observe(
            viewLifecycleOwner,
            Observer<PagedList<Playlist.Item>> { playlistsList ->
                playlistsPagedModelAdapter.submitList(playlistsList)
            })
    }

    private fun showRecyclerViewProgressIndicator() {
        footerAdapter.clear()
        val progressIndicatorItem = ProgressIndicatorItem()
        footerAdapter.add(progressIndicatorItem)
    }

    private fun showEmptyState() {
        groupEmptyPlaylists.makeVisible()
    }

    private fun hideEmptyState() {
        groupEmptyPlaylists.makeGone()
    }

    private fun fetchPlaylists() {
        viewModel.getPlaylists()
    }

    private fun createRetrySnackbar() {
        retrySnackbar =
            Snackbar.make(clPlaylists, R.string.error_fetch_playlists, Snackbar.LENGTH_INDEFINITE)
                .setAnchorView(activity?.findViewById(R.id.bottomNavView) as BottomNavigationView)
                .setAction(R.string.btn_retry) {
                    viewModel.refreshFailedRequest()
                }
    }

    private fun onItemClick() {
        playlistsAdapter.onClickListener = { view, adapter, item, position ->
            if (item is PlaylistItem) {
                val action =
                    PlaylistsFragmentDirections.actionPlaylistsFragmentToPlaylistVideosFragment(
                        item.playlistItem?.snippet?.title!!,
                        item.playlistItem.id,
                        item.playlistItem.snippet.description,
                        item.playlistItem.contentDetails.itemCount.toFloat(),
                        item.playlistItem.snippet.thumbnails.standard?.url
                            ?: item.playlistItem.snippet.thumbnails.high.url,
                        item.playlistItem.snippet.publishedAt
                    )
                findNavController().navigate(action)
            }
            false
        }
    }
}
