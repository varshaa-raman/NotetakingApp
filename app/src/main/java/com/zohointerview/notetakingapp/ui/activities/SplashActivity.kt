package com.zohointerview.notetakingapp.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import com.zohointerview.notetakingapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remove Title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)

        // Using a handler to delay loading the MainActivity
        Handler(Looper.getMainLooper()).postDelayed({

            // Start activity
            startActivity(Intent(this, MainActivity::class.java))

            // Animate the loading of new activity
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            // Close this activity
            finish()

        }, 800)


    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        makeFullScreen()
    }


    private fun makeFullScreen() {
        // Make Fullscreen
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            // Hide the toolbar
            supportActionBar?.hide()
        }
    }
}