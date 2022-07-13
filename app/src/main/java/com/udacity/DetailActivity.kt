package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val contentIntent = Intent(applicationContext, MainActivity::class.java)
        ok_button.setOnClickListener{
            startActivity(contentIntent)
        }
        var extras = intent.extras
        var message = extras?.get("message") as Message
        fileName_edit.setText("${message.name}")
        if (message.status == "Failed")
            status_edit.setTextColor(Color.RED)
        status_edit.setText("${message.status}")
    }

}
