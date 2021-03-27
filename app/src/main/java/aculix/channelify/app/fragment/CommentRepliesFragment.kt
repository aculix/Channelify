package aculix.channelify.app.fragment


import aculix.channelify.app.Channelify
import aculix.channelify.app.R
import aculix.channelify.app.fastadapteritems.CommentReplyItem
import aculix.channelify.app.fastadapteritems.ProgressIndicatorItem
import aculix.channelify.app.model.CommentReply
import aculix.channelify.app.paging.Status
import aculix.channelify.app.utils.DividerItemDecorator
import aculix.channelify.app.utils.getAdaptiveBannerAdSize
import aculix.channelify.app.viewmodel.CommentRepliesViewModel
import aculix.core.extensions.makeGone
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.paged.PagedModelAdapter
import kotlinx.android.synthetic.main.fragment_comment_replies.*
import kotlinx.android.synthetic.main.fragment_comments.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommentRepliesFragment : Fragment() {

    private val viewModel by viewModel<CommentRepliesViewModel>() // Lazy inject ViewModel
    private val args by navArgs<CommentRepliesFragmentArgs>()

    private lateinit var commentId: String
    private lateinit var commentRepliesAdapter: GenericFastAdapter
    private lateinit var commentRepliesPagedModelAdapter: PagedModelAdapter<CommentReply.Item, CommentReplyItem>
    private var retrySnackbar: Snackbar? = null
    private lateinit var footerAdapter: GenericItemAdapter
    private var isFirstPageLoading = true

    private lateinit var adView: AdView
    private var initialLayoutComplete = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_replies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentId = args.commentId

        setupRecyclerView(savedInstanceState)
        fetchCommentReplies()
        setupObservables()

        ivCloseCommentReplies.setOnClickListener { onCloseClick() }

        if (Channelify.isAdEnabled) setupAd() else adViewContainerCommentReplies.makeGone()
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        outState = commentRepliesAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        retrySnackbar?.dismiss() // Dismiss the retrySnackbar if already present
        if (Channelify.isAdEnabled) adView.pause()
        super.onPause()
    }

    override fun onResume() {
        if (Channelify.isAdEnabled) adView.resume()
        super.onResume()
    }

    override fun onDestroy() {
        if (Channelify.isAdEnabled) adView.destroy()
        super.onDestroy()
    }

    private fun setupRecyclerView(savedInstanceState: Bundle?) {
        val asyncDifferConfig = AsyncDifferConfig.Builder<CommentReply.Item>(object :
            DiffUtil.ItemCallback<CommentReply.Item>() {
            override fun areItemsTheSame(
                oldItem: CommentReply.Item,
                newItem: CommentReply.Item
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CommentReply.Item,
                newItem: CommentReply.Item
            ): Boolean {
                return oldItem == newItem
            }
        }).build()

        commentRepliesPagedModelAdapter =
            PagedModelAdapter<CommentReply.Item, CommentReplyItem>(asyncDifferConfig) {
                CommentReplyItem(it)
            }

        footerAdapter = ItemAdapter.items()

        commentRepliesAdapter =
            FastAdapter.with(listOf(commentRepliesPagedModelAdapter, footerAdapter))
        commentRepliesAdapter.registerTypeInstance(CommentReplyItem(null))
        commentRepliesAdapter.withSavedInstanceState(savedInstanceState)

        rvCommentReplies.layoutManager = LinearLayoutManager(context)
        rvCommentReplies.addItemDecoration(
            DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.view_divider_item_decorator)!!)
        )
        rvCommentReplies.adapter = commentRepliesAdapter
    }

    private fun setupObservables() {
        // Observe network live data
        viewModel.networkStateLiveData?.observe(viewLifecycleOwner, Observer { networkState ->
            when (networkState?.status) {
                Status.FAILED -> {
                    footerAdapter.clear()
                    createRetrySnackbar()
                    retrySnackbar?.show()
                }
                Status.SUCCESS -> {
                    footerAdapter.clear()
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
        viewModel.commentRepliesLiveData?.observe(
            viewLifecycleOwner,
            Observer<PagedList<CommentReply.Item>> { commentRepliesList ->
                commentRepliesPagedModelAdapter.submitList(commentRepliesList)
            })
    }

    private fun showRecyclerViewProgressIndicator() {
        footerAdapter.clear()
        val progressIndicatorItem = ProgressIndicatorItem()
        footerAdapter.add(progressIndicatorItem)
    }

    private fun fetchCommentReplies() {
        viewModel.getCommentReplies(commentId)
    }

    private fun createRetrySnackbar() {
        retrySnackbar =
            Snackbar.make(
                clCommentReplies,
                R.string.error_fetch_comment_replies,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.btn_retry) {
                    viewModel.refreshFailedRequest()
                }
    }

    private fun onCloseClick() {
        findNavController().popBackStack()
    }

    private fun setupAd() {
        adView = AdView(context)
        adViewContainerCommentReplies.addView(adView)
        adViewContainerCommentReplies.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true

                adView.adUnitId = getString(R.string.comment_replies_banner_ad_id)
                adView.adSize = activity?.getAdaptiveBannerAdSize(adViewContainerCommentReplies)
                adView.loadAd(AdRequest.Builder().build())
            }
        }
    }
}
