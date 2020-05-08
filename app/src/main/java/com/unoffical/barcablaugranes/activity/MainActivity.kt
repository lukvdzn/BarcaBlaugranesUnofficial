package com.unoffical.barcablaugranes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.unoffical.barcablaugranes.fragments.MainEntryPageFragment
import com.unoffical.barcablaugranes.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.fragment_container,
                MainEntryPageFragment()
            )
            commit()
        }
    }
}