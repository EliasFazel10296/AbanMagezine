/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/26/21 9:10 AM
 * Last modified 2/26/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.abanabsalan.aban.magazine.ProductShowcaseConfigurations.UI.Adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.online_store_all_products_items.view.*
import net.geekstools.imageview.customshapes.ShapesImage

class AllProductsViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val rootViewItem: ConstraintLayout = view.rootViewItem
    val productFeatureImageView: ShapesImage = view.productFeatureImageView
    val productTitleView: AppCompatTextView = view.productTitleView
    val onSaleView: AppCompatImageView = view.onSaleView
}