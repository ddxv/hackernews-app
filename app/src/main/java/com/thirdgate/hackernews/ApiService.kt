import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

class ApiService {
    private val client = OkHttpClient()

    val gson = Gson()
    val type = object : TypeToken<Map<Int, Any>>() {}.type
    val myBaseUrl: String = "https://thirdgate.dev/api/articles/"


    suspend fun getArticle(articleNum: String): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            val url = myBaseUrl + articleNum
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val bodyString =
                    response.body?.string() ?: throw IOException("Response body is null")
                var myMap: Map<Int, Map<String, Any>> = gson.fromJson(bodyString, type)
                // JSON is nested inside the articles ID
                var myMap2: Map<String, Any> =
                    myMap[articleNum.toInt()] ?: throw IOException("Key not found in map")
                myMap2
            }
        }
    }

    suspend fun getArticles(articleType: String): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            val url = "$myBaseUrl/list/$articleType"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val bodyString =
                    response.body?.string() ?: throw IOException("Response body is null")
                var myMap: Map<String, Map<String, Any>> = gson.fromJson(bodyString, type)
                myMap
            }
        }
    }

}
