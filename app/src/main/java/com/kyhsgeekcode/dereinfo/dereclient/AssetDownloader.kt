package com.kyhsgeekcode.dereinfo.dereclient

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

object AssetDownloader {
    fun download() {
        CoroutineScope(Dispatchers.IO).launch {
            val url =
                URL("https://asset-starlight-stage.akamaized.net/dl/10023800/manifests/Android_AHigh_SHigh")
            // URL("http://storage.game.starlight-stage.jp/dl/resources/High/AssetBundles/Android/1c00f829a44f2c136a5f093fe8c9050c")
            val urlConnection =
                url.openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("X-Unity-Version", "2018.3.8f1")
            urlConnection.setRequestProperty(
                "User-Agent",
                "Dalvik/2.1.0 (Linux; U; Android 7.0; Nexus 42 Build/XYZZ1Y)"
            )
            urlConnection.setRequestProperty("Accept", "*/*")
            urlConnection.setRequestProperty("Host", "asset-starlight-stage.akamaized.net")
            urlConnection.setRequestProperty("Connection", "Keep-Alive")
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate")
            //          urlConnection.setRequestProperty("APP_VER", "5.9.5")
//            urlConnection.setRequestProperty("RES_VER", "10023800")
            urlConnection.requestMethod = "GET"
            try {
                val statusCode = urlConnection.responseCode
                val inputStream: InputStream
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.errorStream
                    Log.e("AssetDownloader", "StatusCode:$statusCode")
                } else {
                    inputStream = urlConnection.inputStream
                }
                val bytes = inputStream.readBytes()
                val text = String(bytes, Charset.defaultCharset())
                Log.d("AssetDownloader", text)
            } catch (e: Exception) {
                Log.e("AssetDownloader", "a", e)
            } finally {
                urlConnection.disconnect()
            }

        }
    }
}