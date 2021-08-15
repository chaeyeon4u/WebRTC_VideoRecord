/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.example.supputorsproject2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /* 로그인 버튼 클릭시 처리*/
        btn_login.setOnClickListener {
            val ps: Pattern = Pattern.compile("^[a-zA-Z0-9\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$")

            /* 유효성 검사 및 로그인 */
            if(user_id.text.toString() == ""){
                Toast.makeText(this, "아이디에 공백이 존재합니다.", Toast.LENGTH_SHORT).show()
            }else if(user_pw.text.toString() == ""){
                Toast.makeText(this, "비밀번호에 공백이 존재합니다.", Toast.LENGTH_SHORT).show()
            }else if(!ps.matcher(user_id.text.toString()).matches()){
                Toast.makeText(this, "아이디에는 특수문자가 존재할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }else{
                /* 로그인 이후 화면전환 */
                val HomeIntent = Intent(this, HomeActivity::class.java)
                startActivity(HomeIntent)
                Toast.makeText(this, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }

        }

        /* 회원가입 버튼 클릭시 처리 */
        btn_signup.setOnClickListener {
            val SignupIntent = Intent(this, SignupActivity::class.java)
            startActivity(SignupIntent)
        }
    }
}
