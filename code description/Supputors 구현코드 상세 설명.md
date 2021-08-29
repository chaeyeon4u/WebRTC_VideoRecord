# Supputors 구현코드 상세 설명

# HomeActivity.kt

![Untitled](Supputors%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%E1%84%8F%E1%85%A9%E1%84%83%E1%85%B3%20%E1%84%89%E1%85%A1%E1%86%BC%E1%84%89%E1%85%A6%20%E1%84%89%E1%85%A5%E1%86%AF%E1%84%86%E1%85%A7%E1%86%BC%20b9a213b7337342cc84780acb8ac3c1e6/Untitled.png)

**btn_shoot의 클릭리스너**

→ MainActivity로 전환

**btn_monitor의 클릭리스너**

→ MainActivity로 전환

# LoginActivity.kt

![Untitled](Supputors%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%E1%84%8F%E1%85%A9%E1%84%83%E1%85%B3%20%E1%84%89%E1%85%A1%E1%86%BC%E1%84%89%E1%85%A6%20%E1%84%89%E1%85%A5%E1%86%AF%E1%84%86%E1%85%A7%E1%86%BC%20b9a213b7337342cc84780acb8ac3c1e6/Untitled%201.png)

**btn_login의 클릭 리스너**

- 유효성 검사

→ 공백검사, 특수문자 검사, 길이검사

**btn_signup의 클릭 리스너**

→ SignupActivity로 전환

# MainActivity.kt

![Untitled](Supputors%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%E1%84%8F%E1%85%A9%E1%84%83%E1%85%B3%20%E1%84%89%E1%85%A1%E1%86%BC%E1%84%89%E1%85%A6%20%E1%84%89%E1%85%A5%E1%86%AF%E1%84%86%E1%85%A7%E1%86%BC%20b9a213b7337342cc84780acb8ac3c1e6/Untitled%202.png)

**start_meeting 클릭리스너**

```kotlin
start_meeting.setOnClickListener {
            if (meeting_id.text.toString().trim().isNullOrEmpty())
                meeting_id.error = "Please enter meeting id"
            else {
                db.collection("calls")
                        .document(meeting_id.text.toString())
                        .get()
                        .addOnSuccessListener {
                            if (it["type"]=="OFFER" || it["type"]=="ANSWER" || it["type"]=="END_CALL") {
                                meeting_id.error = "Please enter new meeting ID"
                            } else {
                                val intent = Intent(this@MainActivity, RTCActivity::class.java)
                                intent.putExtra("meetingID",meeting_id.text.toString())
                                intent.putExtra("isJoin",false)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener {
                            meeting_id.error = "Please enter new meeting ID"
                        }
            }
        }
```

- meeting_id가 null값이면 오류 메시지를 출력
- 미팅아이디가 비어있지 않으면,
    - FireStore에 "calls" 이라는 식별자에 meeting_id로 문서 데이터를 추가한다.
    - get() 메서드로 단일 문서의 검색을 진행한다.
        - FireStore에 type의 값이 존재하면, 값에 따른 방이 이미 할당되어있다는 뜻이므로 오류메시지를 출력한다.
        - type의 값이 존재하지 않는다면, meetingID값과, isJoin(false)값을 함께 RTCActivity로 넘겨주며 화면을 전환한다.
    - FireStore에 연결 실패시 에러 메시지를 출력한다.

**join_meeting 클릭리스너**

```kotlin
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
```

- meeting_id가 null값이면 오류메시지 출력
- meeting_id가 존재한다면 meetingID값과, isJoin(isJoin)값을 함께 RTCActivity로 넘겨주며 화면을 전환한다.

# RTCActivity

**onCreate**() : 생성함수

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Media Recorder의 권한을 얻기위한 부분 일부 시작*/
        checkSelfPermission()
        /*Miedia recorder 부분 일부 끝*/

        if (intent.hasExtra("meetingID"))
            meetingID = intent.getStringExtra("meetingID")!!
        if (intent.hasExtra("isJoin"))
            isJoin = intent.getBooleanExtra("isJoin",false)

        checkCameraAndAudioPermission()
        audioManager.selectAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
        switch_camera_button.setOnClickListener {
            rtcClient.switchCamera()
        }

        audio_output_button.setOnClickListener {
            if (inSpeakerMode) {
                inSpeakerMode = false
                audio_output_button.setImageResource(R.drawable.ic_baseline_hearing_24)
                audioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.EARPIECE)
            } else {
                inSpeakerMode = true
                audio_output_button.setImageResource(R.drawable.ic_baseline_speaker_up_24)
                audioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
            }
        }
        video_button.setOnClickListener {
            if (isVideoPaused) {
                isVideoPaused = false
                video_button.setImageResource(R.drawable.ic_baseline_videocam_off_24)
            } else {
                isVideoPaused = true
                video_button.setImageResource(R.drawable.ic_baseline_videocam_24)
            }
            rtcClient.enableVideo(isVideoPaused)
        }
        mic_button.setOnClickListener {
            if (isMute) {
                isMute = false
                mic_button.setImageResource(R.drawable.ic_baseline_mic_off_24)
            } else {
                isMute = true
                mic_button.setImageResource(R.drawable.ic_baseline_mic_24)
            }
            rtcClient.enableAudio(isMute)
        }
			video_record_button.setOnClickListener{
            if(isRecord){
                isRecord = false
                video_record_button.setImageResource(R.drawable.video_record)
            }else{
                isRecord = true
                video_record_button.setImageResource(R.drawable.video_record_paused)
                startMediaProjection()//MediaRecoder 녹화기능 시작할 때
            }
        }

        end_call_button.setOnClickListener {
            rtcClient.endCall(meetingID)
            remote_view.isGone = false
            Constants.isCallEnded = true
            finish()
            startActivity(Intent(this@RTCActivity, MainActivity::class.java))
        }
        
    }
```

- checkSelfPermission() : 사용자 정의함수
-> Media Recoder의 권한을 얻기위한 함수

- MainActivity에서 넘어온 "meetingID" 값이 있을경우 받아온다.
- MainActivity에서 넘어온 isJoin값이 있을경우 받아온다.

- checkCameraAndAudioPermission() : 사용자 정의함수
-> 카메라 및 오디오 권한을 받아온다.

- audioManager.selectAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
-> 현대 활성 오디오 장치의 상태를 채택한다.

- switch_camera_button 클릭리스너
-> rtcClient.switchCamera()를 실행시켜 카메라를 바꾼다.

- audio_output_button 클릭리스너
-> setImageResource로 버튼의 이미지 변환.
-> audioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.EARPIECE)
: 이어폰 모드로 바꿔주는 함수
-> audioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
: 스피커폰 모드로 바꿔주는 함수

- video_button 클릭리스너
-> setImageResource로 버튼의 이미지 변환.
-> rtcClient.enableVideo(isVideoPaused)
: 자신의 화면 비디오를 볼 수 있게하거나 없게 만들어주는 함수

- mic_button 클릭리스너
-> setImageResource로 버튼의 이미지 변환.
-> rtcClient.enableAudio(isMute)
: 음소거 유/무 상태로 바꿔주는 함수

- video_record_button
-> setImageResource로 버튼의 이미지 변환.

- end_call_button 클릭리스너
-> rtcClient.endCall(meetingID)
meetingID의 RTC를 종료시킨다.
-> activity_main.xml 파일의 SurfaceViewRenderer()를 지운다

**checkCameraAndAudioPermission()** 

```kotlin
private fun checkCameraAndAudioPermission() {
        if ((ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this,AUDIO_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED)) {
            requestCameraAndAudioPermission()
        } else {
            onCameraAndAudioPermissionGranted()
        }
    }
```

카메라와 오디오 권한을 얻는 함수이다.

- 카메라와 오디오의 권한이 없는 경우 requestCameraAndAudioPermission() 호출
- 카메라와 오디오의 권한이 있는 경우 onCameraAndAudioPermissionGranted() 호출

**requestCameraAndAudioPermission()**

```kotlin
private fun requestCameraAndAudioPermission(dialogShown: Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, AUDIO_PERMISSION) &&
            !dialogShown) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA_PERMISSION, AUDIO_PERMISSION), CAMERA_AUDIO_PERMISSION_REQUEST_CODE)
        }
    }
```

- ActivityCompat.shouldShowRequestPermissionRationale() :

    사용자가 이전에 권한 요청을 거부한 경우에 true반환.

- 사용자가 카메라와 오디오에 대한 권한을 거부한 경우 showPermissionRationaleDialog() 사용자정의 함수 호출하여 권한에 대한 알림창을 생성한다.
- 사용자가 카메라에 대한 권한을 수락한 경우

    ActivityCompat.requestPermissions 내장 함수를 통해 안드로이드 시스템이 사용자에게 권한 요청 대화창을 표시 하도록

**showPermissionRationaleDialog()**

```kotlin
private fun showPermissionRationaleDialog() {
    AlertDialog.Builder(this)
            .setTitle("Camera And Audio Permission Required")
            .setMessage("This app need the camera and audio to function")
            .setPositiveButton("Grant"){dialog, _->
dialog.dismiss()
                requestCameraAndAudioPermission(true)
}
.setNegativeButton("Deny"){dialog, _->
dialog.dismiss()
                onCameraPermissionDenied()
}
.show()
}
```

권한에 대한 알림창 생성

**onCameraAndAudioPermissionGranted()**

```kotlin
private fun onCameraAndAudioPermissionGranted() {
        rtcClient = RTCClient(
                application,
                object : PeerConnectionObserver() {
                    override fun onIceCandidate(p0: IceCandidate?) {
                        super.onIceCandidate(p0)
                        signallingClient.sendIceCandidate(p0, isJoin)
                        rtcClient.addIceCandidate(p0)
                    }

                    override fun onAddStream(p0: MediaStream?) {
                        super.onAddStream(p0)
                        Log.e(TAG, "onAddStream: $p0")
                        p0?.videoTracks?.get(0)?.addSink(remote_view)
                    }

                    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                        Log.e(TAG, "onIceConnectionChange: $p0")
                    }

                    override fun onIceConnectionReceivingChange(p0: Boolean) {
                        Log.e(TAG, "onIceConnectionReceivingChange: $p0")
                    }

                    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                        Log.e(TAG, "onConnectionChange: $newState")
                    }

                    override fun onDataChannel(p0: DataChannel?) {
                        Log.e(TAG, "onDataChannel: $p0")
                    }

                    override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
                        Log.e(TAG, "onStandardizedIceConnectionChange: $newState")
                    }

                    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
                        Log.e(TAG, "onAddTrack: $p0 \n $p1")
                    }

                    override fun onTrack(transceiver: RtpTransceiver?) {
                        Log.e(TAG, "onTrack: $transceiver" )
                    }
                }
        )

        rtcClient.initSurfaceView(remote_view)
        rtcClient.initSurfaceView(local_view)
        rtcClient.startLocalVideoCapture(local_view)
        signallingClient =  SignalingClient(meetingID,createSignallingClientListener())
        if (!isJoin)
            rtcClient.call(sdpObserver,meetingID)
    }
```

- IceCandidate : 네트워크 정보를 담는 데이터, WebRTC가 원격 장치와 통신을 하기 위해 요구되는

                             프로토콜과 라우팅에 대해 알려줍니다.

**createSignallingClientListener()**

```kotlin
private fun createSignallingClientListener() = object : SignalingClientListener {
        override fun onConnectionEstablished() {
            end_call_button.isClickable = true
        }

        override fun onOfferReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
            Constants.isIntiatedNow = false
            rtcClient.answer(sdpObserver,meetingID)
            remote_view_loading.isGone = true
        }

        override fun onAnswerReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
            Constants.isIntiatedNow = false
            remote_view_loading.isGone = true
        }

        override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
            rtcClient.addIceCandidate(iceCandidate)
        }

        override fun onCallEnded() {
            if (!Constants.isCallEnded) {
                Constants.isCallEnded = true
                rtcClient.endCall(meetingID)
                finish()
                startActivity(Intent(this@RTCActivity, MainActivity::class.java))
            }
        }
    }
```

# **VideoRecord를 위한 함수**

<MediaProjection API>

- MediaProjection

→ 화면을 캡쳐한 데이터를 가져오거나 음성 데이터를 가져오기 위한 API이다.

→ 실제 동작은 영상만 가능합니다.

- MediaProjection.Callback

→ Callbacks for the projection session

→ MediaProjection 처리에 대한 Callback을 받을 수 있다.

- MediaProjectionManager

→ Managers the retrieval of certain types of MediaProjection tokens.

→ MediaProjection에 대한 권한을 처리한다.

- VirtualDisplay

→ MediaProjection을 생성된 화면의 정보를 가집니다.

**checkSelfPermission()**

```kotlin
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
    }
```

- RECORD_AUDIO와 WRITE_EXTERNAL_STORAGE에 대한 권한을 확인하고 요청한다.
- ActivityCompat.requestPermissions (this, 배열형태의 권한, 요청코드)

    : 내장 함수를 통해 안드로이드 시스템이 사용자에게 권한 요청 대화창을 표시 하도록 도와준다.

**initView()**

뷰 초기

```kotlin

private fun initView() {
      findViewById<View>(R.id.video_record_button).setOnClickListener { 
      startMediaProjection()
      }
}
```

- video_record_button의 클릭리스너

→ 미디어 프로젝션 요청

**startMediaProjection()** 

** MEDIA_PROJECTION_SERVICE

미디어 프로젝션 요청

```kotlin
private fun startMediaProjection() {     
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         val mediaProjectionManager =
             getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
         startActivityForResult(
             mediaProjectionManager.createScreenCaptureIntent(),
            REQUEST_CODE_MediaProjection
         )
     }
}
```

- MediaProjection : Audio Recorder 과 Screen Capture을 위한 Document Reference
- MediaProjectionManager

→ Managers the retrieval of certain types of MediaProjection tokens.

→ MediaProjectionManager는 getSystemService를 통해 service를 생성하고, 사용자에게 권한을 요구하게 됩니다.

- LOLLIPOP이상 버전이면, getSystemService를 통해  서비스를 획득하고, MediaProjectionManager로 사용자에게 권한을 요구하게 됩니다.

**onActivityResult()**

```kotlin
override fun onActivityResult(
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
```

사용자에게 권한을 획득받은 이후 onActivityResult를 통해 MediaProjectiond을 생성하여 사용한다.

- 사용자가 권한을 허용해주었으면,  사용자정의함수인 screenRecorder()을 호출한다.

**screenRecorder()**

화면 녹화

```kotlin
private fun screenRecorder(resultCode: Int, data: Intent?) {
        val screenRecorder = createRecorder()
        val mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        //이부분 foreground로 처리해야함
				//사용자가 권한을 허용해주었기에 mediaProjection을 사용할 수 있다.
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
								//종료이후 비디오 영상을 띄운다.
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(videoFile), "video/mp4")
                startActivity(intent)
            }
        }
        screenRecorder?.start()
    }
```

- getSystemService를 통해  서비스를 획득하고, MediaProjectionManager로 사용자에게 권한을 요구하게 됩니다.
- mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)
→ startMediaProjection에서 ****사용자가 권한을 허용해주었기에 mediaProjection을 사용가능함
- MediaProjection.Callback은 MediaProjection을 사용하는 중에 호출됩니다.onStop()에 대한 이벤트를 제공합니다.
- video_record_button의 클릭리스너

    :  버튼의 이미지를 변경해준다.

- Uri를 지정해주고 ACTION_VIEW를 지정해주어 videoFile을 띄운다.

**createRecorder()**

미디어 레코더

```kotlin
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
```

## MediaRecorder

Android 멀티미디어 프레임워크에는 다양한 일반 오디오 및 동영상 포맷을 캡처하고 인코딩하는 지원 기능이 포함되어 있습니다. 기기 하드웨어에서 지원되는 경우 `[MediaRecorder](https://developer.android.com/reference/android/media/MediaRecorder?hl=ko)` API를 사용할 수 있습니다.

========================================

## Firebase와의 연동부분 추가 설명

( Python 서버와의 연동을 위해 어디서 요청을 보내고, 

영상을 실시간으로 받아오는지에 대한 추가 설명 )

### MainActivity.kt에서

### start_meeting 버튼을 클릭하면 'calls' collection의 document에 meeting 아이디를 생성해서 전달해주고, 연결 성공 응답이 오면 meeting_id와 isJoin(false)를 RTCActivity.kt로 전달해준다.

```kotlin
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
```

### join_meeting 버튼을 클릭하면 RTCActivity.kt로 meeting_id와 isJoin(true)를 전해준다.

```kotlin
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
```

⇒ 이 두 버튼 클릭시 python server에 요청 코드를 추가하면 된다.

## SignallingClient.kt

### 백그라운드에서 지속적으로 리스너를 실행해 사용되는 클래스로 SDPtype이 "offer"인지, "answer"인지 "end_call"인지 확인하고 이를 기반으로 리스너의 특정 메소드를 호출

SDP 타입 알아내는 코드

```kotlin
//firebase에 "calls"라는 collection에 meetingID를 넣어서 성공적으로 수행할 경우
db.collection("calls")
    .document(meetingID)
    .addSnapshotListener { snapshot, e ->//firestore의 data를 snapshot에 담음

        if (e != null) {
            Log.w(TAG, "listen:error", e)
            return@addSnapshotListener
        }

        //firebase의 type값이 "OFFER", "ANSWER", "END_CALL"인지 확인후 처리.. sdp와 type을 insert 한다.
        if (snapshot != null && snapshot.exists()) {
            //"OFFER" -> SignallingClientListener.onOfferReceived
            //"ANSWER" -> SignallingClientListener.onAnswerReceived
            //"END_CALL" -> SignallingClientListener.onCallEnded
            val data = snapshot.data
            if (data?.containsKey("type")!! &&
                data.getValue("type").toString() == "OFFER") {
                    listener.onOfferReceived(SessionDescription(
                        SessionDescription.Type.OFFER,data["sdp"].toString()))
                SDPtype = "Offer"
            } else if (data?.containsKey("type") &&
                data.getValue("type").toString() == "ANSWER") {
                    listener.onAnswerReceived(SessionDescription(
                        SessionDescription.Type.ANSWER,data["sdp"].toString()))
                SDPtype = "Answer"
            } else if (!Constants.isIntiatedNow && data.containsKey("type") &&
                data.getValue("type").toString() == "END_CALL") {
                listener.onCallEnded()
                SDPtype = "End Call"

            }
            Log.d(TAG, "Current data: ${snapshot.data}")
        } else {
            Log.d(TAG, "Current data: null")
        }
    }

```

## SDPtype에 따라 IceCandidate 객체 생성 후 처리

```kotlin
//"calls" collection 안에 "candidates"라는 collection에서 사용자의 속성을 확인한후 처리.
db.collection("calls").document(meetingID)
        .collection("candidates").addSnapshotListener{ querysnapshot,e->
            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            if (querysnapshot != null && !querysnapshot.isEmpty) {
                for (dataSnapShot in querysnapshot) {

                    val data = dataSnapShot.data
                    if (SDPtype == "Offer" && data.containsKey("type") && data.get("type")=="offerCandidate") {
                        listener.onIceCandidateReceived(
                                IceCandidate(data["sdpMid"].toString(),
                                    (data["sdpMLineIndex"] as Long).toInt(),
                                        data["sdpCandidate"].toString()))
                    } else if (SDPtype == "Answer" && data.containsKey("type") && data.get("type")=="answerCandidate") {
                        listener.onIceCandidateReceived(
                                IceCandidate(data["sdpMid"].toString(),
                                    (data["sdpMLineIndex"] as Long).toInt(),
                                        data["sdpCandidate"].toString()))
                    }
                    Log.e(TAG, "candidateQuery: $dataSnapShot" )
                }
            }
        }
```

## sendIceCandidate

## candidate이라는 collection에 필드를(serverUrl, sdpMid, sdpMLineIndex, sdpCandidate, type) 생성하여 firebase에 setting

```kotlin
fun sendIceCandidate(candidate: IceCandidate?,isJoin : Boolean) = runBlocking {
    val type = when {//candidate의 type을 구분
        isJoin -> "answerCandidate"
        else -> "offerCandidate"
    }
    val candidateConstant = hashMapOf(
            "serverUrl" to candidate?.serverUrl,
            "sdpMid" to candidate?.sdpMid,
            "sdpMLineIndex" to candidate?.sdpMLineIndex,
            "sdpCandidate" to candidate?.sdp,
            "type" to type
    )

    //calls>candidates에 data setting
    db.collection("calls")
        .document("$meetingID").collection("candidates").document(type)
        .set(candidateConstant as Map<String, Any>)
        .addOnSuccessListener {
            Log.e(TAG, "sendIceCandidate: Success" )
        }
        .addOnFailureListener {
            Log.e(TAG, "sendIceCandidate: Error $it" )
        }
}
```

# RTCClient.kt

세션을 유지하기위해 Server와 Peer를 연결

## PeerConnection.call

createOffer() 이용하여 통화서비스 시작

```kotlin
private fun PeerConnection.call(sdpObserver: SdpObserver, meetingID: String) {
    val constraints = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
    }

    createOffer(object : SdpObserver by sdpObserver {
        override fun onCreateSuccess(desc: SessionDescription?) {
            setLocalDescription(object : SdpObserver {
                override fun onSetFailure(p0: String?) {
                    Log.e(TAG, "onSetFailure: $p0")
                }

                override fun onSetSuccess() {
                    val offer = hashMapOf(
                            "sdp" to desc?.description,
                            "type" to desc?.type
                    )
                    //calls collection에 meetingID document의 offer(sdp, type)을 추가
                    db.collection("calls").document(meetingID)
                            .set(offer)
                            .addOnSuccessListener {
                                Log.e(TAG, "DocumentSnapshot added")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error adding document", e)
                            }
                    Log.e(TAG, "onSetSuccess")
                }

                override fun onCreateSuccess(p0: SessionDescription?) {
                    Log.e(TAG, "onCreateSuccess: Description $p0")
                }

                override fun onCreateFailure(p0: String?) {
                    Log.e(TAG, "onCreateFailure: $p0")
                }
            }, desc)
            sdpObserver.onCreateSuccess(desc)
        }

        override fun onSetFailure(p0: String?) {
            Log.e(TAG, "onSetFailure: $p0")
        }

        override fun onCreateFailure(p0: String?) {
            Log.e(TAG, "onCreateFailure: $p0")
        }
    }, constraints)
}
```

## fun PeerConnection.answer()은

createAnswer()메서드로 응답을 제공하는데 사용된다.

이 메소드는 사용자가 요청받을 때마다 트리거되고 createAnswer () 가 성공 하면 SDPtype을 " ANSWER " 유형으로 업데이트합니다 . 다른 쪽에서 성공적으로 응답을 받으면 통화가 시작된다.

```kotlin
private fun PeerConnection.answer(sdpObserver: SdpObserver, meetingID: String) {
    val constraints = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
    }
    createAnswer(object : SdpObserver by sdpObserver {
        override fun onCreateSuccess(desc: SessionDescription?) {
            val answer = hashMapOf(
                    "sdp" to desc?.description,
                    "type" to desc?.type
            )
            db.collection("calls").document(meetingID)
                    .set(answer)
                    .addOnSuccessListener {
                        Log.e(TAG, "DocumentSnapshot added")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error adding document", e)
                    }
            setLocalDescription(object : SdpObserver {
                override fun onSetFailure(p0: String?) {
                    Log.e(TAG, "onSetFailure: $p0")
                }

                override fun onSetSuccess() {
                    Log.e(TAG, "onSetSuccess")
                }

                override fun onCreateSuccess(p0: SessionDescription?) {
                    Log.e(TAG, "onCreateSuccess: Description $p0")
                }

                override fun onCreateFailure(p0: String?) {
                    Log.e(TAG, "onCreateFailureLocal: $p0")
                }
            }, desc)
            sdpObserver.onCreateSuccess(desc)
        }

        override fun onCreateFailure(p0: String?) {
            Log.e(TAG, "onCreateFailureRemote: $p0")
        }
    }, constraints)
}
```

## onRemoteSessionReceived()

이 메서드는 PeerConnection에 원격 세션을 추가하여 연결을 설정하는 데 사용됩니다. 따라서 call () 및 answer () 메서드 아래에 추가된다.

```kotlin
fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
    remoteSessionDescription = sessionDescription
    peerConnection?.setRemoteDescription(object : SdpObserver {
        override fun onSetFailure(p0: String?) {
            Log.e(TAG, "onSetFailure: $p0")
        }

        override fun onSetSuccess() {
            Log.e(TAG, "onSetSuccessRemoteSession")
        }

        override fun onCreateSuccess(p0: SessionDescription?) {
            Log.e(TAG, "onCreateSuccessRemoteSession: Description $p0")
        }

        override fun onCreateFailure(p0: String?) {
            Log.e(TAG, "onCreateFailure")
        }
    }, sessionDescription)

}
```

## addIceCandidate()

연결에 Candidate을 추가하는 데 사용됩니다. 

" offerCandidate " 및 " answerCandidate "이 서로 통신 할 수 있다.

```kotlin
fun endCall(meetingID: String) {
    db.collection("calls").document(meetingID).collection("candidates")
            .get().addOnSuccessListener {
                val iceCandidateArray: MutableList<IceCandidate> = mutableListOf()
                for ( dataSnapshot in it) {
                    if (dataSnapshot.contains("type") && dataSnapshot["type"]=="offerCandidate") {
                        val offerCandidate = dataSnapshot
                        iceCandidateArray.add(IceCandidate(offerCandidate["sdpMid"].toString(), Math.toIntExact(offerCandidate["sdpMLineIndex"] as Long), offerCandidate["sdp"].toString()))
                    } else if (dataSnapshot.contains("type") && dataSnapshot["type"]=="answerCandidate") {
                        val answerCandidate = dataSnapshot
                        iceCandidateArray.add(IceCandidate(answerCandidate["sdpMid"].toString(), Math.toIntExact(answerCandidate["sdpMLineIndex"] as Long), answerCandidate["sdp"].toString()))
                    }
                }
                peerConnection?.removeIceCandidates(iceCandidateArray.toTypedArray())
            }
    val endCall = hashMapOf(
            "type" to "END_CALL"
    )
    db.collection("calls").document(meetingID)
            .set(endCall)
            .addOnSuccessListener {
                Log.e(TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }

    peerConnection?.close()
}
```