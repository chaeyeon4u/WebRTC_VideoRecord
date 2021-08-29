package com.example.supputorsproject2
/* Main, RTC, SignallingClient, RTCClient :  FireBase와의 연동*/
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.supputorsproject2.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_start.*

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Constants.isIntiatedNow = true
        Constants.isCallEnded = true

        start_meeting.setOnClickListener {
            if (meeting_id.text.toString().trim().isNullOrEmpty())
                meeting_id.error = "Please enter meeting id"
            else {
                db.collection("calls")//"calls"라는 컬렉션을 생성
                        .document(meeting_id.text.toString())//meeting_id.text를 String형태로 전달 (.document, .add, .set 메서드 모두 값 추가하는 메서드)
                        .get()
                        .addOnSuccessListener {//연결 성공시
                            if (it["type"]=="OFFER" || it["type"]=="ANSWER" || it["type"]=="END_CALL") {//실패시 실행
                                meeting_id.error = "Please enter new meeting ID"
                            } else {//성공시 실행 RTCActivity로 전환하는데 meetingID랑 isJoin 값도 함께 전달
                                val intent = Intent(this@MainActivity, RTCActivity::class.java)
                                intent.putExtra("meetingID",meeting_id.text.toString())
                                intent.putExtra("isJoin",false)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener {//id가 존재해 열결 실패시
                            meeting_id.error = "Please enter new meeting ID"
                        }
            }
        }

        join_meeting.setOnClickListener {
            if (meeting_id.text.toString().trim().isNullOrEmpty())
                meeting_id.error = "Please enter meeting id"
            else {
                val intent = Intent(this@MainActivity, RTCActivity::class.java)
                intent.putExtra("meetingID",meeting_id.text.toString())
                intent.putExtra("isJoin",true)
                startActivity(intent)
            }
        }
    }
}