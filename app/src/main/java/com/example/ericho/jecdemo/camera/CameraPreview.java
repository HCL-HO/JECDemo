package com.example.ericho.jecdemo.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by ebsl on 15/11/2017.
 */

public
class CameraPreview  extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder holder;
    private Camera cam;
    private Context cxt;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.cam = camera;
        this.cxt = context;
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            /*Setting Focus*/
            Camera.Parameters parameters = cam.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            parameters.set("cam_mode", 1 );
            cam.setParameters(parameters);
            cam.setPreviewDisplay(holder);
            cam.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
