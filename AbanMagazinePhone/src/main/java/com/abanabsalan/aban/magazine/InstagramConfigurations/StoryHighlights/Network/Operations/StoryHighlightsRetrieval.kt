/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 12/31/20 5:45 AM
 * Last modified 12/31/20 5:44 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.abanabsalan.aban.magazine.InstagramConfigurations.StoryHighlights.Network.Operations

import android.content.Context
import android.util.Log
import com.abanabsalan.aban.magazine.CacheConfigurations.CacheMechanism
import com.abanabsalan.aban.magazine.InstagramConfigurations.StoryHighlights.Network.Endpoints.StoryHighlightsEndpoint
import com.abanabsalan.aban.magazine.Utils.Network.Extensions.JsonRequestResponseInterface
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

object EnqueueEndPointQuery {
    const val JSON_REQUEST_TIMEOUT = (1000 * 3)
    const val JSON_REQUEST_RETRIES = (3)
}
class StoryHighlightsRetrieval (private val context: Context) {

    private val cacheMechanism = CacheMechanism(context)

    fun start(jsonRequestResponseInterface: JsonRequestResponseInterface) = CoroutineScope(Dispatchers.IO).async {

        val storyHighlightsEndpoint: StoryHighlightsEndpoint = StoryHighlightsEndpoint()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            storyHighlightsEndpoint.getInstagramStoryHighlightsEndpoint,
            null,
            { response ->
                Log.d("JsonObjectRequest ${this@StoryHighlightsRetrieval.javaClass.simpleName}", response.toString())

                if (response != null) {

                    jsonRequestResponseInterface.jsonRequestResponseSuccessHandler(response)

                }

            }, {
                Log.d("JsonObjectRequestError", it?.networkResponse?.statusCode.toString())

                jsonRequestResponseInterface.jsonRequestResponseFailureHandler(it?.networkResponse?.statusCode)

            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            EnqueueEndPointQuery.JSON_REQUEST_TIMEOUT,
            EnqueueEndPointQuery.JSON_REQUEST_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        val requestQueue = Volley.newRequestQueue(context)

        if (cacheMechanism.checkTimeToLive()) {
            requestQueue.cache.clear()
        }

        requestQueue.add(jsonObjectRequest)
    }

}