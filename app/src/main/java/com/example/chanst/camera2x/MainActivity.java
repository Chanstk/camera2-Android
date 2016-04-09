package com.example.chanst.camera2x;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private CameraMan2 cm2;
    private SurfaceView mCameraView;
    private Context context;
    Size mPreviewSize = new Size(1280, 720);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        mCameraView = (SurfaceView) findViewById(R.id.texture);
        cm2 = new CameraMan2(this,mPreviewSize.getWidth(),mPreviewSize.getHeight());

    }
    @Override
    protected void onResume() {
        super.onResume();
        cm2.start();
        mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                cm2.startPreview(holder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cm2.stopPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
        });
        mCameraView.getHolder().setFixedSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        mCameraView.requestLayout();
        mCameraView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cm2.stop();
    }
}
