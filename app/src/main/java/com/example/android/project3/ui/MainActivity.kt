package com.example.android.project3.ui

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android.project3.R
import com.example.android.project3.utils.ButtonState
import com.example.android.project3.utils.Urls
import com.example.android.project3.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private var downloadId: Long = 0
    private var selectedUrlFile = ""
    lateinit var notificationManager: NotificationManager
    private var WRITE_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        motion_layout_main.transitionToEnd()

        requestPermission()
    }

    private fun requestPermission() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == WRITE_EXTERNAL_STORAGE
            && (grantResults.isNotEmpty())
            && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            initNotificationManager()

            registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            initOnClick()

            createChannel(
                getString(R.string.download_notification_channel_id),
                getString(R.string.download_notification_channel_name)
            )
        }
    }

    private fun initNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent.action

            if (downloadId == id && action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                val query = DownloadManager.Query()
                query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                val manager =
                    context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = manager.query(query)
                if (cursor.moveToFirst() && cursor.count > 0) {
                    val status =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.success),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        notificationManager.sendNotification(
                            resources.getString(R.string.notification_description) +
                                    " $selectedUrlFile",
                            applicationContext, getFileName()
                        )
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.fail),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.my_description)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    private fun getFileName() =
        when (selectedUrlFile) {
            Urls.glideUrl -> getString(R.string.glide)
            Urls.loadingAppUrl -> getString(R.string.load_app)
            Urls.retrofitUrl -> getString(R.string.retrofit)
            else -> ""
        }


    private fun downloadFile() {
        val file = File(getExternalFilesDir(null), "/repos")

        if (!file.exists()) {
            file.mkdirs()
        }

        val request =
            DownloadManager.Request(Uri.parse(selectedUrlFile))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    getString(R.string.sub_path)
                )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadId =
            downloadManager.enqueue(request)
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.rb_glide ->
                    if (checked) selectedUrlFile = Urls.glideUrl
                R.id.rb_load_app ->
                    if (checked) selectedUrlFile = Urls.loadingAppUrl
                R.id.rb_retrofit ->
                    if (checked) selectedUrlFile = Urls.retrofitUrl
            }
        }
    }

    private fun initOnClick() {
        loadingButton.setOnClickListener {
            loadingButton.buttonState = ButtonState.Clicked
//            loadingCircle.buttonState = ButtonState.Clicked

            if (!optionSelected()) {
                Toast.makeText(this, getString(R.string.please_select_file), Toast.LENGTH_SHORT)
                    .show()
            } else {
                downloadFile()
            }
        }
    }


    private fun optionSelected(): Boolean = !selectedUrlFile.isEmpty()
}