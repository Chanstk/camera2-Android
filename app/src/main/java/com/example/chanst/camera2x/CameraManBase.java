package com.example.chanst.camera2x;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class CameraManBase {
    private static final String TAG = "CameraManBase";

    protected Size mDesiredSize;
    public Size mPreviewSize;

    protected HandlerThread mBackgroundThread;
    protected Handler mBackgroundHandler;

    CameraManBase(int width, int height) {
        mDesiredSize = new Size(width, height);
    }

    abstract public void start();

    abstract public void stop();

    abstract void startPreview(SurfaceHolder holder);

    abstract void stopPreview();

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraMan");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the <b>largest</b>
     * one whose width and height are less than the desired ones, and whose aspect ratio matches
     * the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param desiredSize The minimum desired size
     * @return The optimal {@code Size}, or null if none were big enough
     */
    protected static Size chooseOptimalSize(@NonNull Size[] choices, Size desiredSize) {
        Log.d(TAG, "Preview sizes: " + Arrays.toString(choices));

        // Collect the supported resolutions that are at least as big as the desired size.
        int width = desiredSize.getWidth();
        int height = desiredSize.getHeight();
        List<Size> ok = new ArrayList<>();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() <= width && option.getHeight() <= height) {
                ok.add(option);
            }
        }

        // Pick the biggest of those, assuming we found any.
        if (ok.isEmpty()) {
            Log.e(TAG, "Couldn't find any suitable preview size, use the bigger one");
            return Collections.max(Arrays.asList(choices), new CompareSizesByArea());
        } else {
            return Collections.max(ok, new CompareSizesByArea());
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
