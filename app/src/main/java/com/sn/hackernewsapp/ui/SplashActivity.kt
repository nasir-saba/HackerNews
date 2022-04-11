package com.sn.hackernewsapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.sn.hackernewsapp.R
import com.sn.hackernewsapp.ui.main.HackerNewsActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity(), LifecycleOwner {
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        activityScope.launch {
            delay(2000)
            startActivity(Intent(this@SplashActivity, HackerNewsActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}