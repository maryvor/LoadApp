package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager

    private lateinit var downloadManager: DownloadManager

    private val message = Message("","")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        custom_button.setOnClickListener {
            when(radio_group.checkedRadioButtonId){
                glide_radiobutton.id -> {
                    custom_button.setLoadingState()
                    message.name = getString(R.string.glide_description)
                    download(URL_GLIDE)
                }
                retrofit_radiobutton.id -> {
                    custom_button.setLoadingState()
                    message.name = getString(R.string.retrofit_description)
                    download(URL_RETROFIT)
                }
                loadapp_radiobutton.id -> {
                    custom_button.setLoadingState()
                    message.name = getString(R.string.loadapp_description)
                    download(URL_LOAD_APP)
                }
                -1 -> {
                    Toast.makeText(this, "Please select file to download", Toast.LENGTH_SHORT).show()
                }
            }
        }
        createNotificationChannel(getString(R.string.channel_id), getString(R.string.channel_name))
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)


            val query = DownloadManager.Query()
            query.setFilterById(id!!)

            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        message.status = getString(R.string.success)
                    }
                    DownloadManager.STATUS_FAILED -> {
                        message.status = getString(R.string.fail)
                    }

                }
            }
            custom_button.downloadCompleted()
            notificationManager.sendNotification(message, context)
        }
    }

    private fun download(url : String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.i("Loading", "Inside download method")
    }

    companion object {
        private const val URL_LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE =
            "https://codeload.github.com/bumptech/glide/zip/refs/heads/master"
        private const val URL_RETROFIT =
            "https://codeload.github.com/square/retrofit/zip/refs/heads/master"
    }

    private fun createNotificationChannel(id:String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
@Parcelize
class Message(var name: String, var status: String): Parcelable
