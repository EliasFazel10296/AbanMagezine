/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 8/2/20 4:02 AM
 * Last modified 8/2/20 4:00 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.abanabsalan.aban.magazine.PostsConfigurations.SinglePost.SinglePostUI

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abanabsalan.aban.magazine.PostsConfigurations.DataHolder.PostsDataParameters
import com.abanabsalan.aban.magazine.PostsConfigurations.DataHolder.PostsLiveData
import com.abanabsalan.aban.magazine.PostsConfigurations.SinglePost.Extensions.hidePopupPreferences
import com.abanabsalan.aban.magazine.PostsConfigurations.SinglePost.Extensions.setupUserInterface
import com.abanabsalan.aban.magazine.PostsConfigurations.SinglePost.SinglePostUI.Adapters.SinglePostViewAdapter
import com.abanabsalan.aban.magazine.Preferences.PopupPreferencesController
import com.abanabsalan.aban.magazine.R
import com.abanabsalan.aban.magazine.Utils.Ads.AdsConfiguration
import com.abanabsalan.aban.magazine.Utils.UI.Theme.OverallTheme
import com.abanabsalan.aban.magazine.Utils.UI.Theme.ThemeType
import com.abanabsalan.aban.magazine.Utils.UI.Theme.toggleLightDarkThemePostView
import com.abanabsalan.aban.magazine.databinding.PostsViewUiBinding
import com.google.android.material.appbar.AppBarLayout
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerInterface

class SinglePostView : AppCompatActivity(), GestureListenerInterface, AppBarLayout.OnOffsetChangedListener {

    val overallTheme: OverallTheme by lazy {
        OverallTheme(applicationContext)
    }

    val postsLiveData: PostsLiveData by lazy {
        ViewModelProvider(this@SinglePostView).get(PostsLiveData::class.java)
    }

    val adsConfiguration: AdsConfiguration by lazy {
        AdsConfiguration(this@SinglePostView)
    }

    private var postTopBarIsExpanded = true

    var postId: String? = null
    var featureImageLink: String? = null
    var postTitle: String? = null
    var postExcerpt: String? = null
    var postLink: String? = null

    lateinit var postsViewUiBinding: PostsViewUiBinding

    companion object {

        fun show(context: AppCompatActivity,
                 featuredImageSharedElement: AppCompatImageView,
                 postId: String,
                 postFeaturedImage: String, postTitle: String, postContent: String,
                 postExcerpt: String, postLink: String) {

            Intent(context, SinglePostView::class.java).apply {

                putExtra(PostsDataParameters.PostParameters.PostId, postId)

                putExtra(PostsDataParameters.PostParameters.PostFeaturedImage, postFeaturedImage)

                putExtra(PostsDataParameters.PostParameters.PostTitle, postTitle)
                putExtra(PostsDataParameters.PostParameters.PostContent, postContent)

                putExtra(PostsDataParameters.PostParameters.PostExcerpt, postExcerpt)
                putExtra(PostsDataParameters.PostParameters.PostLink, postLink)

                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this@apply,
                    ActivityOptions.makeSceneTransitionAnimation(context,
                        featuredImageSharedElement,
                        context.getString(R.string.featuredImageTransitionName))
                        .toBundle())

            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postsViewUiBinding = PostsViewUiBinding.inflate(layoutInflater)
        setContentView(postsViewUiBinding.root)

        adsConfiguration.initialize()

        postId = intent.getStringExtra(PostsDataParameters.PostParameters.PostId)

        featureImageLink = intent.getStringExtra(PostsDataParameters.PostParameters.PostFeaturedImage)

        postTitle = intent.getStringExtra(PostsDataParameters.PostParameters.PostTitle)
        postExcerpt = intent.getStringExtra(PostsDataParameters.PostParameters.PostExcerpt)
        postLink = intent.getStringExtra(PostsDataParameters.PostParameters.PostLink)

        val rawPostContent = intent.getStringExtra(PostsDataParameters.PostParameters.PostContent)

        PopupPreferencesController(this@SinglePostView, postsViewUiBinding.preferencePopupInclude)
            .initializeForPostView(postId)

        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        postsViewUiBinding.postRecyclerView.layoutManager = linearLayoutManager

        val singlePostViewAdapter: SinglePostViewAdapter = SinglePostViewAdapter(this@SinglePostView)

        if (!postTitle.isNullOrEmpty() && !featureImageLink.isNullOrEmpty()) {
            setupUserInterface(postTitle!!, featureImageLink!!)
        }

        postsLiveData.singleSinglePostLiveItemData.observe(this@SinglePostView, Observer {

            if (it.isNotEmpty()) {

                singlePostViewAdapter.singlePostItemsData.clear()

                singlePostViewAdapter.singlePostItemsData.addAll(it)
                postsViewUiBinding.postRecyclerView.adapter = singlePostViewAdapter

            }

        })

        /*Change Theme*/
        postsLiveData.toggleTheme.observe(this@SinglePostView, Observer {

            var delayTheme: Long = 3333

            when(it) {
                ThemeType.ThemeLight -> {
                    delayTheme = 3000
                }
                ThemeType.ThemeDark -> {
                    delayTheme = 1133
                }
            }

            Handler().postDelayed({

                singlePostViewAdapter.notifyItemRangeChanged(0, singlePostViewAdapter.itemCount, null)


                toggleLightDarkThemePostView(this@SinglePostView)

            }, delayTheme)

        })

        postsLiveData.prepareRawDataToRenderForSinglePost(rawPostContent)

    }

    override fun onResume() {
        super.onResume()

        if (adsConfiguration.interstitialAd.isLoaded) {
            adsConfiguration.interstitialAd.show()
        }

        postsViewUiBinding.postTopBar.addOnOffsetChangedListener(this@SinglePostView)

    }

    override fun onPause() {
        super.onPause()

        postsViewUiBinding.postTopBar.removeOnOffsetChangedListener(this@SinglePostView)

    }

    override fun onBackPressed() {

        if (postsViewUiBinding.preferencePopupInclude.root.isShown) {

            hidePopupPreferences()

        } else {

            this@SinglePostView.finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        }

    }

    override fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {
        super.onSwipeGesture(gestureConstants, downMotionEvent, moveMotionEvent, initVelocityX, initVelocityY)


    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        postTopBarIsExpanded = (verticalOffset == 0)

        if (postTopBarIsExpanded) {
            if (postsViewUiBinding.postMenuButton.isShown) {
                postsViewUiBinding.postMenuButton.visibility = View.INVISIBLE
            }
            if (postsViewUiBinding.postMenuIcon.isShown) {
                postsViewUiBinding.postMenuIcon.visibility = View.INVISIBLE
            }
        } else {
            if (!postsViewUiBinding.postMenuButton.isShown) {
                postsViewUiBinding.postMenuButton.visibility = View.VISIBLE
            }
            if (!postsViewUiBinding.postMenuIcon.isShown) {
                postsViewUiBinding.postMenuIcon.visibility = View.VISIBLE
            }
        }

    }
}