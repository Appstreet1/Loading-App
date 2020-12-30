package com.example.android.project3.ui

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.android.project3.R
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        motion_layout.transitionToEnd()

        initNotificationManager()

        notificationManager.cancelAll()

        initView()
        initOnClick()
    }

    private fun initNotificationManager(){
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private fun initView(){
        renderFileName()
        renderStatus()
    }

    private fun renderFileName() {
        filename_data.text = getValueFromIntent()
    }

    private fun renderStatus(){
        status_data.text = getStatus()
    }

    private fun getStatus(): String {
        return if (getValueFromIntent().isEmpty()) {
            getString(R.string.fail)
        } else {
            getString(R.string.success)
        }
    }

    private fun initOnClick(){
        ok_button.setOnClickListener {
            finish()
        }
    }

    private fun getValueFromIntent(): String {
        val intent = intent

        return intent.getStringExtra(getString(R.string.filename)).toString()
    }
}
