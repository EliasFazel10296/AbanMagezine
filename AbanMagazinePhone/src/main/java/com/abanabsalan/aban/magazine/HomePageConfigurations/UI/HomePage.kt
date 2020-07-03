/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 7/3/20 9:31 AM
 * Last modified 7/3/20 9:12 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.abanabsalan.aban.magazine.HomePageConfigurations.UI

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abanabsalan.aban.magazine.AbanMagazinePhoneApplication
import com.abanabsalan.aban.magazine.HomePageConfigurations.DataHolder.HomePageLiveData
import com.abanabsalan.aban.magazine.HomePageConfigurations.Extensions.setupUserInterface
import com.abanabsalan.aban.magazine.HomePageConfigurations.Extensions.startNetworkOperations
import com.abanabsalan.aban.magazine.Utils.Network.NetworkCheckpoint
import com.abanabsalan.aban.magazine.Utils.Network.NetworkConnectionListener
import com.abanabsalan.aban.magazine.Utils.Network.NetworkConnectionListenerInterface
import com.abanabsalan.aban.magazine.Utils.UI.Display.columnCount
import com.abanabsalan.aban.magazine.Utils.UI.Theme.OverallTheme
import com.abanabsalan.aban.magazine.databinding.HomePageViewBinding
import javax.inject.Inject

class HomePage : AppCompatActivity(), NetworkConnectionListenerInterface {

    val overallTheme: OverallTheme by lazy {
        OverallTheme(applicationContext)
    }

    val networkCheckpoint: NetworkCheckpoint by lazy {
        NetworkCheckpoint(applicationContext)
    }

    val homePageLiveData: HomePageLiveData by lazy {
        ViewModelProvider(this@HomePage).get(HomePageLiveData::class.java)
    }

    @Inject
    lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var homePageViewBinding: HomePageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homePageViewBinding = HomePageViewBinding.inflate(layoutInflater)
        setContentView(homePageViewBinding.root)

        setupUserInterface()

        (application as AbanMagazinePhoneApplication)
            .dependencyGraph
            .subDependencyGraph()
            .create(this@HomePage, homePageViewBinding.rootView)
            .inject(this@HomePage)

        networkConnectionListener.networkConnectionListenerInterface = this@HomePage

        val primaryRecyclerViewLayoutManager = GridLayoutManager(applicationContext, columnCount(applicationContext, 115), RecyclerView.VERTICAL, false)
        homePageViewBinding.primaryCategoriesView.layoutManager = primaryRecyclerViewLayoutManager

        val secondaryRecyclerViewLayoutManager = GridLayoutManager(applicationContext, columnCount(applicationContext, 55), RecyclerView.VERTICAL, false)
        homePageViewBinding.primaryCategoriesView.layoutManager = secondaryRecyclerViewLayoutManager

        homePageViewBinding.root.post {

            homePageLiveData.postsLiveItemData.observe(this@HomePage, Observer {

                if (it.isNotEmpty()) {



                }

            })

            homePageLiveData.categoriesLiveItemData.observe(this@HomePage, Observer {

                if (it.isNotEmpty()) {



                }

            })

            startNetworkOperations()

        }

    }

    override fun onResume() {
        super.onResume()


    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()

        networkConnectionListener.unregisterDefaultNetworkCallback()

    }

    override fun onBackPressed() {

        startActivity(Intent(Intent.ACTION_MAIN).apply {
            this.addCategory(Intent.CATEGORY_HOME)
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

    }

    override fun networkAvailable() {

        startNetworkOperations()

    }

    override fun networkLost() {

    }

}