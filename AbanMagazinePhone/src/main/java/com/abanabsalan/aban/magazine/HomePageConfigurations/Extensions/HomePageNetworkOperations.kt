/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/12/20 6:05 AM
 * Last modified 11/12/20 5:48 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.abanabsalan.aban.magazine.HomePageConfigurations.Extensions

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import com.abanabsalan.aban.magazine.CategoriesConfigurations.Network.Endpoints.CategoriesEndpointsFactory
import com.abanabsalan.aban.magazine.CategoriesConfigurations.Network.Operations.CategoriesRetrieval
import com.abanabsalan.aban.magazine.HomePageConfigurations.DataHolder.HomePageLiveData
import com.abanabsalan.aban.magazine.HomePageConfigurations.UI.HomePage
import com.abanabsalan.aban.magazine.InstagramConfigurations.StoryHighlights.Network.Operations.StoryHighlightsRetrieval
import com.abanabsalan.aban.magazine.PostsConfigurations.DataHolder.PostsDataParameters
import com.abanabsalan.aban.magazine.PostsConfigurations.Network.Endpoints.PostsEndpointsFactory
import com.abanabsalan.aban.magazine.PostsConfigurations.Network.Operations.NewestPostsRetrieval
import com.abanabsalan.aban.magazine.ProductShowcaseConfigurations.Operations.ProductShowcaseRetrieval
import com.abanabsalan.aban.magazine.R
import com.abanabsalan.aban.magazine.SpecificCategoryConfigurations.Network.Endpoints.SpecificCategoryEndpointsFactory
import com.abanabsalan.aban.magazine.SpecificCategoryConfigurations.Network.Operations.SpecificCategoryRetrieval
import com.abanabsalan.aban.magazine.SpecificCategoryConfigurations.Utils.PageCounter
import com.abanabsalan.aban.magazine.TagsConfigurations.Network.Endpoints.TagsEndpointsFactory
import com.abanabsalan.aban.magazine.TagsConfigurations.Network.Operations.TagsPostsRetrieval
import com.abanabsalan.aban.magazine.Utils.BlogContent.LanguageUtils
import com.abanabsalan.aban.magazine.Utils.BlogContent.PostsData
import com.abanabsalan.aban.magazine.Utils.InApplicationReview.InApplicationReviewProcess
import com.abanabsalan.aban.magazine.Utils.InApplicationUpdate.InApplicationUpdateProcess
import com.abanabsalan.aban.magazine.Utils.Network.Extensions.JsonRequestResponseInterface
import com.abanabsalan.aban.magazine.Utils.Network.NetworkSettingCallback
import com.abanabsalan.aban.magazine.Utils.UI.Display.columnCount
import com.abanabsalan.aban.magazine.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import com.abanabsalan.aban.magazine.Utils.UI.NotifyUser.SnackbarBuilder
import com.abanabsalan.aban.magazine.databinding.HomePageViewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.net.ssl.HttpsURLConnection

fun HomePage.homePageRemoteConfiguration() {

    val postsData = PostsData(applicationContext)

    val firebaseRemoteConfiguration = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }
    firebaseRemoteConfiguration.setConfigSettingsAsync(configSettings)

    firebaseRemoteConfiguration
        .fetchAndActivate().addOnCompleteListener(this@homePageRemoteConfiguration) { task ->

            if (task.isSuccessful) {

                if (firebaseRemoteConfiguration.getString(getString(R.string.websiteDataDate)).toLong() > postsData.readTotalPostsNumber()) {

                    if (postsData.readTotalPostsNumber() > 0.toLong()) {

                        if (scrollViewAtTop && updateDelay) {
                            Log.d(this@homePageRemoteConfiguration.javaClass.simpleName, "Updating Content")

                            updateDelay = false

                            cacheDir.deleteRecursively()

                            CoroutineScope(Dispatchers.IO).launch {

                                try {

                                    firestoreDatabase.clearPersistence()

                                    Glide.get(this@homePageRemoteConfiguration).clearDiskCache()
                                    Glide.get(this@homePageRemoteConfiguration).clearMemory()

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                            setupRefreshView()

                            startNetworkOperations()

                        }

                    }

                    postsData.saveTotalPostsNumber(firebaseRemoteConfiguration.getString(getString(R.string.websiteDataDate)).toLong())

                }

            } else {



            }

        }

}

fun HomePage.startNetworkOperations() {

    val languageUtils: LanguageUtils = LanguageUtils()

    if (networkCheckpoint.networkConnection()) {

        /*Load Featured Posts*/
        startFeaturedPostCategoryRetrieval(applicationContext, homePageViewBinding, homePageLiveData, PageCounter.PageNumberToLoad)

        /*Load Newest Posts*/
        val newestPostsRetrieval: NewestPostsRetrieval = NewestPostsRetrieval(applicationContext)
        newestPostsRetrieval.start(
            PostsEndpointsFactory(
                baseDomainEndpoint = languageUtils.selectedBaseDomain(applicationContext),
                numberOfPageInPostsList = 1,
                amountOfPostsToGet = 10,
                sortByType = "date",
                sortBy = "desc",
                postsLanguage = languageUtils.selectedLanguage(applicationContext)
            ),
            object : JsonRequestResponseInterface {

                override fun jsonRequestResponseSuccessHandler(rawDataJsonArray: JSONArray) {
                    super.jsonRequestResponseSuccessHandler(rawDataJsonArray)

                    homePageLiveData.prepareRawDataToRenderForNewestPosts(rawDataJsonArray)

                }

                override fun jsonRequestResponseFailureHandler(jsonError: String?) {
                    Log.d(this@startNetworkOperations.javaClass.simpleName, jsonError.toString())

                }

            })

        /*Load Categories*/
        val categoriesRetrieval: CategoriesRetrieval = CategoriesRetrieval(applicationContext)
        categoriesRetrieval.start(
            CategoriesEndpointsFactory(
                baseDomainEndpoint = languageUtils.selectedBaseDomain(applicationContext),
                excludeCategory = "199,1034,150",
                amountOfCategoriesToGet = 100,
                sortByType = "count"
            ),
            object : JsonRequestResponseInterface {

                override fun jsonRequestResponseSuccessHandler(rawDataJsonArray: JSONArray) {
                    super.jsonRequestResponseSuccessHandler(rawDataJsonArray)

                    homePageLiveData.prepareRawDataToRenderForCategories(rawDataJsonArray)

                }

                override fun jsonRequestResponseFailureHandler(jsonError: String?) {
                    Log.d(this@startNetworkOperations.javaClass.simpleName, jsonError.toString())

                }

            })

        /*Load Products Showcase*/
        val productShowcaseRetrieval: ProductShowcaseRetrieval = ProductShowcaseRetrieval(applicationContext)
        productShowcaseRetrieval.start(object : JsonRequestResponseInterface {

            override fun jsonRequestResponseSuccessHandler(rawDataJsonObject: JSONObject) {
                super.jsonRequestResponseSuccessHandler(rawDataJsonObject)

                homePageLiveData.prepareRawDataToRenderForProductShowcase(
                    rawDataJsonObject.getJSONObject(PostsDataParameters.JsonDataStructure.PostContent).getString(PostsDataParameters.JsonDataStructure.Rendered)
                )

            }

            override fun jsonRequestResponseFailureHandler(jsonError: String?) {
                Log.d(this@startNetworkOperations.javaClass.simpleName, jsonError.toString())

            }

        })

        /*Load Recommended Posts*/
        val tagsPostsRetrieval: TagsPostsRetrieval = TagsPostsRetrieval(applicationContext)
        tagsPostsRetrieval.start(
            TagsEndpointsFactory(
                tags = "167,2756"
            ),
            object : JsonRequestResponseInterface {

                override fun jsonRequestResponseSuccessHandler(rawDataJsonArray: JSONArray) {
                    super.jsonRequestResponseSuccessHandler(rawDataJsonArray)

//                    homePageLiveData.prepareRawDataToRenderForNewestPosts(rawDataJsonArray)


                }

                override fun jsonRequestResponseFailureHandler(jsonError: String?) {
                    Log.d(this@startNetworkOperations.javaClass.simpleName, jsonError.toString())

                }

            }
        )

        /*Load Instagram Story Highlights*/
        val storyHighlightsRetrieval: StoryHighlightsRetrieval = StoryHighlightsRetrieval(applicationContext)
        storyHighlightsRetrieval.start(object : JsonRequestResponseInterface {

            override fun jsonRequestResponseSuccessHandler(rawDataJsonObject: JSONObject) {
                super.jsonRequestResponseSuccessHandler(rawDataJsonObject)


                homePageLiveData.prepareRawDataToRenderForInstagramStoryHighlights(
                    rawDataJsonObject.getJSONObject(PostsDataParameters.JsonDataStructure.PostContent).getString(PostsDataParameters.JsonDataStructure.Rendered)
                )

            }

            override fun jsonRequestResponseFailureHandler(jsonError: String?) {
                Log.d(this@startNetworkOperations.javaClass.simpleName, jsonError.toString())

            }

        })

        /*Invoke In Application Update*/
        InApplicationUpdateProcess(this@startNetworkOperations, homePageViewBinding.rootView)
            .initialize()

        /*Invoke In Application Review*/
        InApplicationReviewProcess(context = this@startNetworkOperations)
            .start(forceReviewFlow = false)

    } else {
        Log.d(this@startNetworkOperations.javaClass.simpleName, "No Network Connection")

        SnackbarBuilder(applicationContext).show (
            rootView = homePageViewBinding.rootView,
            messageText= getString(R.string.noInternetConnectionText),
            messageDuration = Snackbar.LENGTH_INDEFINITE,
            actionButtonText = R.string.turnOnText,
            snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                override fun onActionButtonClicked(snackbar: Snackbar) {
                    super.onActionButtonClicked(snackbar)

                    if (!networkCheckpoint.networkConnection()) {

                        startActivityForResult(
                            Intent(Settings.ACTION_WIFI_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            NetworkSettingCallback.WifiSetting
                        )

                    } else {

                        snackbar.dismiss()

                    }

                }

            }
        )

    }

}

fun HomePage.internetCheckpoint() {

    if (!networkCheckpoint.networkConnection()) {

        SnackbarBuilder(applicationContext).show(
            rootView = homePageViewBinding.rootView,
            messageText = getString(R.string.noInternetConnectionText),
            messageDuration = Snackbar.LENGTH_INDEFINITE,
            actionButtonText = R.string.turnOnText,
            snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                override fun onActionButtonClicked(snackbar: Snackbar) {
                    super.onActionButtonClicked(snackbar)

                    if (!networkCheckpoint.networkConnection()) {

                        startActivityForResult(
                            Intent(Settings.ACTION_WIFI_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            NetworkSettingCallback.WifiSetting
                        )

                    } else {

                        snackbar.dismiss()

                    }

                }

            }
        )

    }

}

/**
 * Load All Posts From Featured Post Category
 **/
fun startFeaturedPostCategoryRetrieval(context: Context, homePageViewBinding: HomePageViewBinding, homePageLiveData: HomePageLiveData, numberOfPageInPostsList: Int) {

    homePageViewBinding.featuredPostsLoadingView.visibility = View.VISIBLE

    val languageUtils: LanguageUtils = LanguageUtils()

    val specificCategoryRetrieval: SpecificCategoryRetrieval = SpecificCategoryRetrieval(context)

    specificCategoryRetrieval.start(
        SpecificCategoryEndpointsFactory(
            numberOfPageInPostsList = numberOfPageInPostsList,
            sortByType = "id",
            IdOfCategoryToGetPosts = 150, // Featured Posts
            amountOfPostsToGet = (columnCount(context, 190) * 2),
            postsLanguage = languageUtils.selectedLanguage(context)
        ),
        object : JsonRequestResponseInterface {

            override fun jsonRequestResponseSuccessHandler(rawDataJsonArray: JSONArray) {
                super.jsonRequestResponseSuccessHandler(rawDataJsonArray)

                homePageLiveData.prepareRawDataToRenderForSpecificPosts(rawDataJsonArray)

            }

            override fun jsonRequestResponseFailureHandler(networkError: Int?) {
                Log.d("Specific Category Retrieval", networkError.toString())

                when (networkError) {
                    HttpsURLConnection.HTTP_BAD_REQUEST -> {/*400*/

                        homePageLiveData.specificCategoryLiveItemData.postValue(null)

                    }
                }

            }

        })

}