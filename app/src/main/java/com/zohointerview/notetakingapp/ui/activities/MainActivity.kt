package com.zohointerview.notetakingapp.ui.activities

import android.media.Image
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.zohointerview.notetakingapp.R
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

}