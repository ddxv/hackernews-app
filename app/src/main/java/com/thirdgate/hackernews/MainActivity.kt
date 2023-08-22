package com.thirdgate.hackernews

import ApiService
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.thirdgate.hackernews.databinding.ActivityMainBinding
import okhttp3.OkHttpClient


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var textView: TextView
    private lateinit var textView2: TextView
    val client = OkHttpClient()
    private val apiService = ApiService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        //textView = findViewById(R.id.textview_first)
        //textView2 = findViewById(R.id.textview_second)
        //textView.text = "HIIII"
        //textView2.text = "222"


//        CoroutineScope(Dispatchers.Main).launch {
//            val result = getArticle("37208083")
//            // Handle the result here, update your UI, etc.
//            textView.text = result.toString()
//            var pretty = """
//               Title: ${result["title"]}
//            """.trimIndent()
//            //textView2.text = pretty
//        }
    }

//    suspend fun getArticle(articleNum: String): Map<String, Any> {
//        return withContext(Dispatchers.IO) {
//            apiService.getArticle(articleNum)
//        }
//    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}