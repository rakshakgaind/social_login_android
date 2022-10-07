package com.project.socal_login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.project.socal_login.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(application);

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}