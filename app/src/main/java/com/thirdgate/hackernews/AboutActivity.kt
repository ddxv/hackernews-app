package com.thirdgate.hackernews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    AboutPage()
                }
            }
        }
    }
}

@Composable
fun AboutPage() {

    val context = LocalContext.current

    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName
    val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode
    }
    val versionText = "$versionName ($versionCode)"


    val currentContext = rememberUpdatedState(context)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("HackerNews", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Version $versionText", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Source code available", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("App Source Code", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            "github.com/ddxv/hackernews-app",
            fontSize = 20.sp,
            modifier = Modifier.clickable {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ddxv/hackernews-app"))
                currentContext.value.startActivity(intent)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Python backend API", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            "github.com/ddxv/hackernews-api",
            fontSize = 20.sp,
            modifier = Modifier.clickable {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ddxv/hackernews-api"))
                currentContext.value.startActivity(intent)
            }
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text("About Developer", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "thirdgate.dev",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://thirdgate.dev"))
                currentContext.value.startActivity(intent)
            }
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text("Privacy Policy", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "thirdgate.dev/privacypolicy.html",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://thirdgate.dev/privacypolicy.html")
                )
                currentContext.value.startActivity(intent)
            }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun AboutPagePreview() {
    AboutPage()
}
