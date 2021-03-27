package aculix.channelify.app.fragment


import aculix.channelify.app.R
import aculix.channelify.app.model.FavoriteVideo
import aculix.channelify.app.model.Video
import aculix.channelify.app.utils.DateTimeUtils
import aculix.channelify.app.viewmodel.VideoDetailsViewModel
import aculix.core.extensions.*
import aculix.core.helper.ResultWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_video_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoDetailsFragment : Fragment() {

    companion object {
        const val VIDEO_ID = "videoId"
    }

    private val viewModel by viewModel<VideoDetailsViewModel>() // Lazy inject ViewModel
    private val args by navArgs<VideoDetailsFragmentArgs>()

    private lateinit var videoId: String
    private lateinit var videoItem: Video.Item
    private var isVideoAddedToFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservables()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoId = args.videoId

        fetchVideoInfo()
        fetchVideoFavoriteStatus()

        setupBottomAppBar()
        tvCommentsVideoDetails.setOnClickListener { onCommentsClick() }
        btnRetryVideoDetails.setOnClickListener { onRetryClick() }
    }


    /**
     * Fetches the info of video
     */
    private fun fetchVideoInfo() {
        if (isInternetAvailable(requireContext())) {
            viewModel.getVideoInfo(videoId)
        } else {
            showVideoInfoErrorState()
        }
    }

    /**
     * Checks whether the current playing video is already added to favorites or not
     */
    private fun fetchVideoFavoriteStatus() {
        viewModel.getVideoFavoriteStatus(videoId)
    }

    private fun setupObservables() {
        viewModel.videoInfoLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ResultWrapper.Error -> {
                    showVideoInfoErrorState()
                }
                is ResultWrapper.Success<*> -> {
                    hideVideoInfoErrorState()
                    videoItem = (it.data as Video).items[0]
                    setVideoInfo()
                }
            }
        })

        viewModel.favoriteVideoLiveData.observe(viewLifecycleOwner, Observer { isFavorite ->
            isVideoAddedToFavorite = isFavorite

            if (isVideoAddedToFavorite) {
                babVideoDetails.menu.findItem(R.id.miFavoriteBabVideoDetails)
                    .setIcon(R.drawable.ic_favorite_filled)
            } else {
                babVideoDetails.menu.findItem(R.id.miFavoriteBabVideoDetails)
                    .setIcon(R.drawable.ic_favorite_border)
            }
        })
    }

    private fun setVideoInfo() {
        with(videoItem) {
            tvVideoTitleVideoDetails.text = snippet.title
            tvViewCountVideoDetails.text =
                statistics.viewCount?.toLong()?.getFormattedNumberInString() ?: getString(
                    R.string.text_count_unavailable
                )
            tvLikeCountVideoDetails.text =
                statistics.likeCount?.toLong()?.getFormattedNumberInString() ?: getString(
                    R.string.text_count_unavailable
                )
            tvDislikeCountVideoDetails.text =
                statistics.dislikeCount?.toLong()?.getFormattedNumberInString() ?: getString(
                    R.string.text_count_unavailable
                )
            tvCommentCountVideoDetails.text =
                statistics.commentCount.toLong().getFormattedNumberInString()
            tvVideoDescVideoDetails.text = getString(
                R.string.text_video_description,
                DateTimeUtils.getPublishedDate(snippet.publishedAt),
                snippet.description
            )
        }
    }

    private fun showVideoInfoErrorState() {
        groupInfoVideoDetails.makeGone()
        groupErrorVideoDetails.makeVisible()
    }

    private fun hideVideoInfoErrorState() {
        groupErrorVideoDetails.makeGone()
        groupInfoVideoDetails.makeVisible()
    }


    private fun setupBottomAppBar() {
        babVideoDetails.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.miFavoriteBabVideoDetails -> {
                    if (this::videoItem.isInitialized) {
                        // Add or remove from favorites only after videoItem details are fetched
                        isVideoAddedToFavorite = if (isVideoAddedToFavorite) {
                            item.setIcon(R.drawable.ic_favorite_border)
                            removeVideoFromFavorites()
                            false
                        } else {
                            // Add video to favorites
                            item.setIcon(R.drawable.ic_favorite_filled)
                            addVideoToFavorites()
                            true
                        }
                    } else {
                        context?.toast(getString(R.string.text_fetch_video_details_wait_msg))
                    }
                    true
                }
                R.id.miShareBabVideoDetails -> {
                    shareVideoUrl()
                    true
                }
                else -> false
            }

        }
    }

    private fun addVideoToFavorites() {
        val favoriteVideo = FavoriteVideo(
            videoId,
            videoItem.snippet.title,
            videoItem.snippet.thumbnails.standard?.url ?: videoItem.snippet.thumbnails.high.url,
            System.currentTimeMillis(),
            true
        )
        viewModel.addVideoToFavorites(favoriteVideo)
    }

    private fun removeVideoFromFavorites() {
        val favoriteVideo = FavoriteVideo(
            videoId,
            videoItem.snippet.title,
            videoItem.snippet.thumbnails.standard?.url ?: videoItem.snippet.thumbnails.high.url,
            System.currentTimeMillis(),
            true
        )
        viewModel.removeVideoFromFavorites(favoriteVideo)
    }

    private fun shareVideoUrl() {
        context?.startShareTextIntent(
            getString(R.string.text_share_video),
            getString(R.string.text_video_share_url, videoId)
        )
    }

    private fun onCommentsClick() {
        findNavController().navigate(
            VideoDetailsFragmentDirections.actionVideoDetailsFragmentToCommentsFragment(
                videoId
            )
        )
    }

    private fun onRetryClick() {
        fetchVideoInfo()
    }
}
