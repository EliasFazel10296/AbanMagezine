/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 8/2/20 4:02 AM
 * Last modified 8/2/20 4:00 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.abanabsalan.aban.magazine.PostsConfigurations.SinglePost.SinglePostUI.Adapters.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_view_content_item_text_link.view.*

class PostViewTextLinkAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val rootViewItem: ConstraintLayout = view.rootViewItem
    val postTextLink: TextView = view.postTextLink
}