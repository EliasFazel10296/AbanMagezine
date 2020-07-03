/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 7/3/20 10:20 AM
 * Last modified 7/3/20 10:02 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.abanabsalan.aban.magazine.HomePageConfigurations.UI.Adapters.PrimaryCategory

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abanabsalan.aban.magazine.CategoriesConfigurations.DataHolder.CategoriesItemData
import com.abanabsalan.aban.magazine.HomePageConfigurations.UI.HomePage
import com.abanabsalan.aban.magazine.R
import com.abanabsalan.aban.magazine.Utils.UI.Theme.ThemeType

class PrimaryCategoryAdapter (private val context: HomePage, private val themeLightDark: Int): RecyclerView.Adapter<PrimaryCategoryViewHolder>() {

    val categoriesItemData: ArrayList<CategoriesItemData> = ArrayList<CategoriesItemData>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PrimaryCategoryViewHolder {

        return PrimaryCategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.home_page_primary_categories_item, viewGroup, false))
    }

    override fun getItemCount(): Int {

        return categoriesItemData.size
    }

    override fun onBindViewHolder(primaryCategoryViewHolder: PrimaryCategoryViewHolder, position: Int) {

        when (themeLightDark) {
            ThemeType.ThemeLight -> {



            }
            ThemeType.ThemeDark -> {



            }
        }

        primaryCategoryViewHolder.categoryNameView.text = Html.fromHtml(categoriesItemData[position].categoryName)

    }

}