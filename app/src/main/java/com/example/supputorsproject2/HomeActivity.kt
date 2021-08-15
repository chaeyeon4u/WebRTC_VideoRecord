/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.example.supputorsproject2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /*Toolbar Navigation 클릭시 처리*/
        //val toolbara : Toolbar? = toolbar as Toolbar?
        /*val toolbara : Toolbar? = toolbar
        setSupportActionBar(toolbara)

        toolbara?.setNavigationOnClickListener {
            Toast.makeText(this, "메뉴가 클릭되었습니다.", Toast.LENGTH_SHORT).show()
        }*/

        //촬영버튼 클릭시 처리
        btn_shoot.setOnClickListener {
            val Intent = Intent(this, MainActivity::class.java)
            startActivity(Intent)
        }

        btn_monitor.setOnClickListener {
            val Intent = Intent(this, MainActivity::class.java)
            startActivity(Intent)
        }

    }
}
