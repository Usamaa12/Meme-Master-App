package com.MemeMaster.MemeMaster

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.MemeMaster.MemeMaster.MainActivity
import com.MemeMaster.MemeMaster.R
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Surface(
                color = Color(0xFFBCA5E6),
                modifier = Modifier.fillMaxSize()
            ) {
                SplashScreenContent()
                SplashScreenF()
            }
        }
    }

    @Composable
    private fun SplashScreenF() {
        LaunchedEffect(key1 = true) {
            delay(3000)
            startActivity(Intent(this@SplashScreen,MainActivity::class.java))
            finish()
        }

    }
}
@Composable
fun SplashScreenContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Welcome to",
            color = Color.Black,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontStyle = FontStyle.Italic,
                fontSize = 28.sp
            )
        )
        Text(
            text = "Meme Master",
            color = Color.Black,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontStyle = FontStyle.Italic,
                fontSize = 28.sp
            )

        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {

    Surface(
        color = Color(0xFFBCA5E6),
        modifier = Modifier.fillMaxSize()
    ) {
        SplashScreenContent()

    }
}