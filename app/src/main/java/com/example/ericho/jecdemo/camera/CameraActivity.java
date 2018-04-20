package com.example.ericho.jecdemo.camera;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.ericho.jecdemo.R;

import java.io.IOException;


public class CameraActivity extends AppCompatActivity {

    public static final String TAG = CameraActivity.class.getName();
    public static final String VIDEO_TYPE = "VIDEOTYPE";
    public static final String TAG_FILEPATH = "filepath";
    public static final String TAG_QUALITY = "quality";
    public static final String TAG_VIDEO_MAX_TIME = "maxtime";

    public static int VIDEO_TIME_LIMIT = 15;
    public static int QUALITY = CamcorderProfile.QUALITY_480P;

    private boolean isRecording = false;
    private static String videoPath;

    private static Camera mCamera;
    private TextView timerText;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private ImageButton btn;

    private Handler timerHandler;
    private Runnable timeLimitCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();
        initVideoParameters(intent);

        timerHandler = new Handler();
        timerText = findViewById(R.id.timerText);
        btn = findViewById(R.id.button_capture);
        btn.setOnClickListener(recordButtonClick);
        FrameLayout preview = findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(this, getCameraInstance());
        preview.addView(mPreview);
    }

    private void initVideoParameters(Intent intent) {
        videoPath = intent.getStringExtra(TAG_FILEPATH);
        QUALITY = intent.getIntExtra(TAG_QUALITY, QUALITY);
        VIDEO_TIME_LIMIT = intent.getIntExtra(TAG_VIDEO_MAX_TIME, VIDEO_TIME_LIMIT);
    }


    public Camera getCameraInstance() {
        if (mCamera != null) {
            return mCamera;
        } else {
            Camera c = null;
            try {
                int numberOfCameras = Camera.getNumberOfCameras();
                if (numberOfCameras > 0) {
                    c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
                    setCameraOrientation(c);
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                // Camera is not available (in use or does not exist)
            }
            return mCamera = c; // returns null if camera is unavailable
        }
    }

    private void setCameraOrientation(Camera cam) {

        Camera.CameraInfo info =
                new Camera.CameraInfo();

        Camera.getCameraInfo(0, info);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        cam.setDisplayOrientation(result);
    }

    private boolean prepareVideoRecorder() {

        Camera camera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mMediaRecorder.setCamera(camera);
        mMediaRecorder.setOrientationHint(90);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        CamcorderProfile targetProfile = CamcorderProfile.get(getQualityProfile());
        // decrease audio quality for smaller size
        targetProfile.audioBitRate = 64000;
        targetProfile.audioSampleRate = 24000;
        mMediaRecorder.setProfile(targetProfile);

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(videoPath);
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private int getQualityProfile() {
        int quality = CamcorderProfile.QUALITY_LOW;
        if (CamcorderProfile.hasProfile(QUALITY)) {
            quality = QUALITY;
        } else {
            for (int i = QUALITY - 1; i > 0; i--) {
                if (CamcorderProfile.hasProfile(i)) {
                    quality = i;
                    break;
                }
            }
        }
        return quality;
    }

    private View.OnClickListener recordButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            if (isRecording) {
                try {
                    stopVideo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                timeLimitCountDown = new VideoTimeCounter();
                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    timerHandler.postDelayed(timeLimitCountDown, 1000);
                    mMediaRecorder.start();

                    // inform the user that recording has started
                    setCaptureButton(true);
                    isRecording = true;
                } else {
                    // prepare didn't work, release the camera
                    releaseMediaRecorder();
                    // inform user
                }
            }
            v.setEnabled(true);
        }

        class VideoTimeCounter implements Runnable {
            int counter = 1;

            @Override
            public void run() {
                if (counter > VIDEO_TIME_LIMIT + 1) {
                    stopVideo();
                } else {
                    timerHandler.postDelayed(this, 1000);
                    timerText.setText(String.valueOf(VIDEO_TIME_LIMIT + 1 - counter++));
                }
            }
        }
    };

    private void stopVideo() {
        // stop recording and release camera
        timerHandler.removeCallbacks(timeLimitCountDown);
        mMediaRecorder.stop();  // stop the recording
        releaseMediaRecorder();
        getCameraInstance().lock();         // take camera access back from MediaRecorder

        // inform the user that recording has stopped
        setCaptureButton(false);
        isRecording = false;

        setResult(RESULT_OK);
        finish();
    }

    private void setCaptureButton(boolean running) {
        if (running) {
            btn.setImageResource(R.drawable.ic_stop_black_24dp);
        } else {
            btn.setImageResource(R.drawable.ic_fiber_manual_record_red_24dp);
        }
    }

    private void releaseMediaRecorder() {
        mMediaRecorder.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCameraInstance();
    }

    @Override
    protected void onPause() {
        if (isRecording) {
            stopVideo();
        }
        lockCamera();
        super.onPause();
    }

    private void lockCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
