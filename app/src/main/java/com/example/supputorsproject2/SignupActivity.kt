/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.example.supputorsproject2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        /* 회원가입 버튼 클릭시 처리*/
        btn_signup.setOnClickListener {
            var id : String? = signup_id.text.toString()
            var nick : String? = signup_nick.text.toString()
            var pw : String? = signup_pw.text.toString()
            var name : String? = signup_name.text.toString()
            var birth : String? = signup_birth.text.toString()
            var phone : String? = signup_phone.text.toString()

            val ps: Pattern = Pattern.compile("^[a-zA-Z0-9\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$")

            if(id == "" || nick == "" || pw == "" || name == "" || birth == "" || phone == ""){
                Toast.makeText(this, "회원 정보에 공백이 존재합니다.", Toast.LENGTH_SHORT).show()
            }else if(!ps.matcher(id).matches()){
                Toast.makeText(this, "아이디에는 특수문자가 존재할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }else if(!ps.matcher(nick).matches()){
                Toast.makeText(this, "닉네임에는 특수문자가 존재할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }else if(!ps.matcher(name).matches()){
                Toast.makeText(this, "이름에는 특수문자가 존재할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }else if(!ps.matcher(birth).matches()){
                Toast.makeText(this, "생일에는 특수문자가 존재할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }else if(!ps.matcher(phone).matches()){
                Toast.makeText(this, "핸드폰번호에는 특수문자가 존재할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }else if(phone?.length != 11){
                Toast.makeText(this, "핸드폰번호는 11자리로 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if(birth?.length != 8){
                Toast.makeText(this, "생년월일은 8자리로 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else {
                val loginIntent = Intent(this, LoginActivity::class.java)
                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
