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
    val type = object : TypeToken<Map<String, Any>>() {}.type
    val myBaseUrl: String = "https://hacker-news.firebaseio.com/v0/"


    suspend fun getArticle(articleNum: String): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            var url = myBaseUrl + "item/${articleNum}.json?print=pretty"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val bodyString = response.body?.string()
                gson.fromJson(bodyString, type)
            }
        }
    }

}
