package com.example.chanst.camera2x;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Arrays;

public class CameraMan2 extends CameraManBase {
    private static final String TAG = "CameraMan2";
    private static final int IMAGE_READER_BUFFER_SIZE = 12;
    private CameraManager mCameraManager;
    private String mCameraId;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private ImageReader mImageReader;
    CameraMan2(Context context, int width, int height) {
        super(width, height);
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        mCameraId = getBackFacingCameraId();
    }

    @Override
    public void start() {
        startBackgroundThread();
        mPreviewSize = getOptimalSize(mCameraId, mDesiredSize);
        Log.e(TAG, "" + mPreviewSize);
        try {
            Log.i("hehe", "1");
            mCameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    Log.i("hehe","2");
                    mCameraDevice = camera;
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    mCameraDevice = null;

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    Log.e(TAG, "OpenCamera error: " + error);
                    camera.close();
                    mCameraDevice = null;
                }
            }, null);
        } catch (CameraAccessException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        stopBackgroundThread();
    }

    @Override
    void startPreview(SurfaceHolder holder) {
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(),
                mPreviewSize.getHeight(),
                ImageFormat.YUV_420_888,
                IMAGE_READER_BUFFER_SIZE);

        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                if (mCameraDevice == null) {
                    Log.e(TAG, "mCameraDevice is null");
                    return;
                }
                Image image = reader.acquireLatestImage();
                if (image == null) {
                    Log.e(TAG, "No image for ImageReader");
                    return;
                }

                image.close();
            }
        }, mBackgroundHandler);

        try {
            Surface surface = mImageReader.getSurface();
            Surface view = holder.getSurface();
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(view);
            mPreviewRequestBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(Arrays.asList(view, surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCaptureSession = session;
                    try {
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "Capture session config failed");
                    mCameraDevice = null;
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    void stopPreview() {
        try {
            if (mCaptureSession != null) {
                mCaptureSession.stopRepeating();
                mCaptureSession = null;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    private Size getOptimalSize(String cameraId, Size desiredSize) {
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map != null) {
                Size[] sizes = map.getOutputSizes(ImageFormat.YUV_420_888);

                if (sizes == null) {
                    Log.e(TAG, "YUV_420_888 format is not supported");
                } else {
                    return chooseOptimalSize(sizes, desiredSize);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getBackFacingCameraId() {
        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);

                // Skip front facing camera
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "No back facing camera found!");
        return null;
    }
}
