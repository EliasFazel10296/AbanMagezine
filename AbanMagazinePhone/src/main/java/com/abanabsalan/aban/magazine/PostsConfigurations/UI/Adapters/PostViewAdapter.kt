/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 7/5/20 3:47 PM
 * Last modified 7/5/20 3:47 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.abanabsalan.aban.magazine.PostsConfigurations.UI.Adapters

import android.graphics.drawable.Drawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abanabsalan.aban.magazine.PostsConfigurations.DataHolder.PostsDataParameters
import com.abanabsalan.aban.magazine.PostsConfigurations.DataHolder.SinglePostItemData
import com.abanabsalan.aban.magazine.PostsConfigurations.UI.Adapters.ViewHolders.PostViewIFrameAdapterViewHolder
import com.abanabsalan.aban.magazine.PostsConfigurations.UI.Adapters.ViewHolders.PostViewImageAdapterViewHolder
import com.abanabsalan.aban.magazine.PostsConfigurations.UI.Adapters.ViewHolders.PostViewParagraphAdapterViewHolder
import com.abanabsalan.aban.magazine.PostsConfigurations.UI.Adapters.ViewHolders.PostViewTextLinkAdapterViewHolder
import com.abanabsalan.aban.magazine.PostsConfigurations.UI.PostView
import com.abanabsalan.aban.magazine.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target


class PostViewAdapter (private val postViewContext: PostView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val singlePostItemsData: ArrayList<SinglePostItemData> = ArrayList<SinglePostItemData>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {

            PostsDataParameters.PostItemsViewParameters.PostParagraph -> {

                PostViewParagraphAdapterViewHolder(
                    LayoutInflater.from(postViewContext)
                        .inflate(R.layout.post_view_content_item_paragraph, viewGroup, false)
                )

            }
            PostsDataParameters.PostItemsViewParameters.PostImage -> {

                PostViewImageAdapterViewHolder(
                    LayoutInflater.from(postViewContext)
                        .inflate(R.layout.post_view_content_item_image, viewGroup, false)
                )

            }
            PostsDataParameters.PostItemsViewParameters.PostTextLink -> {

                PostViewTextLinkAdapterViewHolder(
                    LayoutInflater.from(postViewContext)
                        .inflate(R.layout.post_view_content_item_text_link, viewGroup, false)
                )

            }
            PostsDataParameters.PostItemsViewParameters.PostIFrame -> {

                PostViewIFrameAdapterViewHolder(
                    LayoutInflater.from(postViewContext)
                        .inflate(R.layout.post_view_content_item_i_frame, viewGroup, false)
                )

            }
            else -> {

                PostViewParagraphAdapterViewHolder(
                    LayoutInflater.from(postViewContext)
                        .inflate(R.layout.post_view_content_item_paragraph, viewGroup, false)
                )

            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        return singlePostItemsData[position].dataType
    }

    override fun getItemCount(): Int {

        return singlePostItemsData.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (singlePostItemsData[position].dataType) {

            PostsDataParameters.PostItemsViewParameters.PostParagraph -> {

                singlePostItemsData[position].postItemParagraph?.let {

                    (viewHolder as PostViewParagraphAdapterViewHolder).postParagraph.text = Html.fromHtml(it.paragraphText)
                }

            }
            PostsDataParameters.PostItemsViewParameters.PostImage -> {

                singlePostItemsData[position].postItemImage?.let {

                    val drawableError: Drawable? = postViewContext.getDrawable(android.R.drawable.ic_menu_report_image)
                    drawableError?.setTint(postViewContext.getColor(R.color.red))

                    val requestOptions = RequestOptions()
                        .error(drawableError)

                    Glide.with(postViewContext)
                        .load(it.imageLink)
                        .apply(requestOptions)
                        .transform(CenterInside(),RoundedCorners(13))
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .listener(object : RequestListener<Drawable> {

                            override fun onLoadFailed(glideException: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {

                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                                postViewContext.runOnUiThread {
                                    (viewHolder as PostViewImageAdapterViewHolder).postImage.setImageDrawable(resource)
                                    (viewHolder as PostViewImageAdapterViewHolder).postImageLoading.visibility = View.GONE
                                }

                                return false
                            }

                        })
                        .submit()

                }

            }
            PostsDataParameters.PostItemsViewParameters.PostTextLink -> {

                singlePostItemsData[position].postItemTextLink?.let {

                    (viewHolder as PostViewTextLinkAdapterViewHolder).postTextLink.text = Html.fromHtml(it.linkText)
                }

            }
            PostsDataParameters.PostItemsViewParameters.PostIFrame -> {

                singlePostItemsData[position].postItemIFrame?.let {

                    (viewHolder as PostViewIFrameAdapterViewHolder).postIFrame.settings.javaScriptEnabled = true
                    (viewHolder as PostViewIFrameAdapterViewHolder).postIFrame.loadData(it.iFrameContent, "text/html", "UTF-8")
                }

            }
            else -> {

            }

        }

    }

}