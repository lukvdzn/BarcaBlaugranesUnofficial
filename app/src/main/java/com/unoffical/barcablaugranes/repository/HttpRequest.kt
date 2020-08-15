package com.unoffical.barcablaugranes.repository

import android.util.Log
import okhttp3.*
import java.io.IOException

object HttpRequest {

    fun getRequestWithCallback(url: String, headers: Headers = Headers.of(), callback: (Call, Response) -> Unit) {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().headers(headers).url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(javaClass.name, "Error building Http Request with Callback!")
            }

            override fun onResponse(call: Call, response: Response) {
                callback(call, response)
            }
        })
    }
}