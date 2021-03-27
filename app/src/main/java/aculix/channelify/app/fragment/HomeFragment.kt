package aculix.channelify.app.fragment

import aculix.channelify.app.R
import aculix.channelify.app.activity.VideoPlayerActivity
import aculix.channelify.app.fastadapteritems.HomeItem
import aculix.channelify.app.fastadapteritems.ProgressIndicatorItem
import aculix.channelify.app.model.PlaylistItemInfo
import aculix.channelify.app.paging.Status
import aculix.channelify.app.utils.DividerItemDecorator
import aculix.channelify.app.viewmodel.HomeViewModel
import aculix.core.extensions.*
import aculix.core.helper.ResultWrapper
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.paged.ExperimentalPagedSupport
import com.mikepenz.fastadapter.paged.PagedModelAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.widget_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagedSupport
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel by viewModel<HomeViewModel>() // Lazy inject ViewModel

    private var homeAdapter: GenericFastAdapter? = null
    private lateinit var homePagedModelAdapter: PagedModelAdapter<PlaylistItemInfo.Item, HomeItem>
    private lateinit var footerAdapter: GenericItemAdapter
    private var isFirstPageLoading = true
    private var retrySnackbar: Snackbar? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        if (requireContext().isInternetAvailable()) {
            viewModel.getLatestVideos()
        } else {
            showErrorState()
        }

        setupUploadsPlaylistIdObservables()
        setupRecyclerView(savedInstanceState)
        onRetryButtonClick()
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        homeAdapter?.let {
            var outState = _outState
            outState = it.saveInstanceState(outState)
            super.onSaveInstanceState(outState)
        }

    }

    override fun onPause() {
        super.onPause()
        retrySnackbar?.dismiss() // Dismiss the retrySnackbar if already present
    }

    private fun setupToolbar() {
        ablHome.toolbarMain.apply {
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
                        findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                    }
                }
                false
            }
        }
    }

    private fun setupRecyclerView(savedInstanceState: Bundle?) {
        val asyncDifferConfig = AsyncDifferConfig.Builder<PlaylistItemInfo.Item>(object :
            DiffUtil.ItemCallback<PlaylistItemInfo.Item>() {
            override fun areItemsTheSame(
                oldItem: PlaylistItemInfo.Item,
                newItem: PlaylistItemInfo.Item
            ): Boolean {
                return oldItem.contentDetails.videoId == newItem.contentDetails.videoId
            }

            override fun areContentsTheSame(
                oldItem: PlaylistItemInfo.Item,
                newItem: PlaylistItemInfo.Item
            ): Boolean {
                return oldItem == newItem
            }
        }).build()

        homePagedModelAdapter =
            PagedModelAdapter<PlaylistItemInfo.Item, HomeItem>(asyncDifferConfig) {
                HomeItem(it)
            }

        footerAdapter = ItemAdapter.items()

        homeAdapter = FastAdapter.with(listOf(homePagedModelAdapter, footerAdapter))
        homeAdapter?.registerTypeInstance(HomeItem(null))
        homeAdapter?.withSavedInstanceState(savedInstanceState)

        rvHome.layoutManager = LinearLayoutManager(context)
        rvHome.adapter = homeAdapter
        rvHome.addItemDecoration(
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.view_divider_item_decorator
                )!!
            )
        )
        onItemClick()
    }

    private fun setupUploadsPlaylistIdObservables() {
        viewModel.uploadsPlaylistIdLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ResultWrapper.Loading -> {
                    // Data is always fetched from the next page and hence loadBefore is never needed
                }
                is ResultWrapper.Error -> {
                    // Error occurred while fetching the uploads playlist id
                    showErrorState(it.errorMessage)
                }
                is ResultWrapper.Success<*> -> {
                    // Success in fetching the uploads playlist id
                    hideErrorState()
                    setupLatestVideosObservables()
                }
            }
        })
    }

    private fun setupLatestVideosObservables() {
        // Observe network live data
        viewModel.networkStateLiveData?.observe(viewLifecycleOwner, Observer { networkState ->
            when (networkState?.status) {
                Status.FAILED -> {
                    footerAdapter.clear()
                    pbHome.makeGone()
                    createRetrySnackbar()
                    retrySnackbar?.show()
                }
                Status.SUCCESS -> {
                    footerAdapter.clear()
                    pbHome.makeGone()
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
        viewModel.latestVideoLiveData?.observe(
            viewLifecycleOwner,
            Observer<PagedList<PlaylistItemInfo.Item>> { latestVideoList ->
                homePagedModelAdapter.submitList(latestVideoList)
            })
    }

    private fun showRecyclerViewProgressIndicator() {
        footerAdapter.clear()
        val progressIndicatorItem = ProgressIndicatorItem()
        footerAdapter.add(progressIndicatorItem)
    }

    private fun showErrorState(errorMsg: String = getString(R.string.error_internet_connectivity)) {
        rvHome.makeGone()
        pbHome.makeGone()
        groupErrorHome.makeVisible()
        tvErrorHome.text = errorMsg
    }

    private fun hideErrorState() {
        groupErrorHome.makeGone()
        rvHome.makeVisible()
    }

    /**
     * Called when the Retry button of the error state is clicked
     */
    private fun onRetryButtonClick() {
        btnRetryHome.setOnClickListener {
            if (requireContext().isInternetAvailable()) viewModel.getLatestVideos()
        }
    }

    /**
     * Called when an item of the RecyclerView is clicked
     */
    private fun onItemClick() {
        homeAdapter?.onClickListener = { view, adapter, item, position ->
            if (item is HomeItem) {
                VideoPlayerActivity.startActivity(
                    context,
                    item.playlistItem?.contentDetails?.videoId!!
                )
            }

            false
        }
    }

    private fun createRetrySnackbar() {
        retrySnackbar =
            Snackbar.make(clHome, R.string.error_load_more_videos, Snackbar.LENGTH_INDEFINITE)
                .setAnchorView(activity?.findViewById(R.id.bottomNavView) as BottomNavigationView)
                .setAction(R.string.btn_retry) {
                    viewModel.refreshFailedRequest()
                }
    }
}
