package com.example.supputorsproject2

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.media.MediaRecorder
import android.media.MediaSession2Service
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MediaRecordService : Service(){

    /* MediaRecord를 위한 변수 시작 */
    //외부 저장소에 비디오파일을 저장하기 위한 변수
    /*private val videoFile =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .toString() + "/MediaProjection.mp4"

    //코드 권한과 코드 미디어프로젝션 요청을 위한 변수..
    //MediaProjection 이란? : Screen Capture or Record Audio를 위해 사용하는 Reference
    private val REQUEST_CODE_PERMISSIONS = 100
    private val REQUEST_CODE_MediaProjection = 101

    private var mediaProjection: MediaProjection? = null*/
    /* MediaRecord를 위한 변수 끝 */


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "Action Received = ${intent?.action}")
       //intent가 시스템에 의해 재생성되었을 때 null값이므로 Java에서는 null check 필수적
        when(intent?.action){
            "startMediaRecordService" -> {
                Log.e(TAG, "Start Foreground 인텐트를 받음")
                startForegroundService()
            }
            "stopMediaRecordService" -> {
                Log.e(TAG, "Stop Foreground 인텐트를 받음")
                stopForegroundService()
            }
        }

        //오류 발생으로 종료시 자동 재시작
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = MediaNotification.createNotification(this)
        startForeground(NOTIFICATION_ID, notification)

        /*MediaProjection 사용위한 권한 획득 */
        //checkSelfPermission()
        var rtcActivity = RTCActivity()
        rtcActivity.startMediaProjection()
    }


    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }

    companion object {
        const val TAG = "[MediaRecordService]"
        const val NOTIFICATION_ID = 23
    }


    /*MEDIA RECORDER 기능 추가를 위한 함수 시작*/
    //OK
    /*override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        // 미디어 프로젝션 응답
        if (requestCode == REQUEST_CODE_MediaProjection && resultCode == Activity.RESULT_OK) {
            screenRecorder(resultCode, data)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    *//**
     * 화면녹화
     *
    // * @param resultCode
    // * @param data
     *//*
    //ok
    private fun screenRecorder(resultCode: Int, data: Intent?) {
        val screenRecorder = createRecorder()
        val mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        //이부분 foreground로 처리해야함
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)
        val callback: MediaProjection.Callback = object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                if (screenRecorder != null) {
                    screenRecorder.stop()
                    screenRecorder.reset()
                    screenRecorder.release()
                }
                mediaProjection?.unregisterCallback(this)
                mediaProjection = null
            }
        }
        mediaProjection?.registerCallback(callback, null)
        val displayMetrics =
            Resources.getSystem().displayMetrics
        mediaProjection?.createVirtualDisplay(
            "sample",
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            screenRecorder!!.surface,
            null,
            null
        )
        //val actionRec = findViewById<Button>(R.id.video_record_button)
        //actionRec.text = "STOP REC"
        video_record_button.setImageResource(R.drawable.video_record_paused)//추가
        video_record_button.setOnClickListener {
            //actionRec.text = "START REC"
            video_record_button.setImageResource(R.drawable.video_record)//추가
            if (mediaProjection != null) {
                mediaProjection!!.stop()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(videoFile), "video/mp4")
                startActivity(intent)
            }
        }
        screenRecorder?.start()
    }

    *//**
     * 미디어 레코더
     *
     * @return
     *//*
    //ok
    private fun createRecorder(): MediaRecorder? {
        val mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setOutputFile(videoFile)
        val displayMetrics =
            Resources.getSystem().displayMetrics
        mediaRecorder.setVideoSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setVideoEncodingBitRate(512 * 1000)
        mediaRecorder.setVideoFrameRate(30)
        try {
            mediaRecorder.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return mediaRecorder
    }

    *//**
     * 뷰 초기화
     *//*
    //Ok
    private fun initView() {
        findViewById<View>(R.id.video_record_button).setOnClickListener { // 미디어 프로젝션 요청
            startMediaProjection()
        }
    }

    *//**
     * 미디어 프로젝션 요청
     *
     *//*
    //OK
    //MediaProjectionManager : 사용자에게 권한을 요청한다.
    public fun startMediaProjection() { //MediaProjection : : Audio Recorder 과 Screen Capture을 위한 Document Reference
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getSystemService를 통해 MediaProjection 서비스를 받아온다.
            val mediaProjectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            //사용자에게 권한을 요청한다.
            startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_CODE_MediaProjection
            )
        }
    }

    *//**
     * 음성녹음, 저장소 퍼미션
     *
     * @return
     *//*
    //OK
    fun checkSelfPermission(): Boolean {
        var temp = ""
        //RECORD_AUDIO권한 유뮤 확인.
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.RECORD_AUDIO + " "
        }

        //WRITE_EXTERNAL_STORAGE 권한 유무 확인
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " "
        }

        //권한을 요청한다.
        return if (TextUtils.isEmpty(temp) == false) {
            ActivityCompat.requestPermissions(
                this,
                temp.trim { it <= ' ' }.split(" ").toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
            false
        } else {
            initView()
            true
        }
    }*/
    /*MEDIA RECORDER 기능 추가를 위한 함수 종료*/
}