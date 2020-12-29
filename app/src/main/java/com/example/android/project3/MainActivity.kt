package com.example.android.project3

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private var downloadId: Long = 0
    private var selectedUrlFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        initOnClick()
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
                        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this@MainActivity, "Fail", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
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
                    "/repos/repository.zip"
                )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadId =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
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
        animationButton.setOnClickListener {
            animationButton.buttonState = ButtonState.Clicked
            loadingCircle.buttonState = ButtonState.Clicked

            if (!optionSelected()) {
                Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT)
                    .show()
            } else {
                downloadFile()
            }
        }
    }

    private fun optionSelected(): Boolean = !selectedUrlFile.isNullOrEmpty()
}