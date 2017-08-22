package com.swetabh.camcontroller.ui.robocam;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.swetabh.camcontroller.R;
import com.swetabh.camcontroller.base.MainContract;
import com.swetabh.camcontroller.cammanager.CamManager;
import com.swetabh.camcontroller.customui.CameraSourcePreview;
import com.swetabh.camcontroller.customui.FaceGraphic;
import com.swetabh.camcontroller.customui.GraphicOverlay;
import com.swetabh.camcontroller.utils.ActivityUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaceDetectionFragment extends Fragment
        implements MainContract.RoboCamViewContract {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int RC_HANDLE_GMS = 9001;
    private static final String TAG = FaceDetectionFragment.class.getSimpleName();
    private final float SMILE_THRESHOLD = 0.5F;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private MainContract.RoboCamPresenterContract mPresenter;
    private MainContract.ActivityCommunicator mCommunicator;
    private Context mContext;
    private CameraSource mCameraSource;
    private boolean mSmileToSnap = false;

    public FaceDetectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        return inflater.inflate(R.layout.fragment_face_detection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreview = (CameraSourcePreview) view.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) view.findViewById(R.id.faceOverlay);
    }

    @Override
    public void onResume() {
        super.onResume();
        openCamera();
        if (mPresenter != null) {
            mPresenter.start();
        }
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestCameraPermission();
            }
            return;
        }
        createCameraSource();
    }

    private void createCameraSource() {

        FaceDetector detector = new FaceDetector.Builder(mContext)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());
        if (!detector.isOperational()) {
            ActivityUtil.showOperationalAlert(mContext, mCommunicator.getFragmentManagerFromActivity());
        }
        mCameraSource = new CameraSource.Builder(mContext, detector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        //check the device has play services available
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
        if (code != ConnectionResult.SUCCESS) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dialog.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            ActivityUtil.showConfirmationDialog(getActivity(), mCommunicator.getFragmentManagerFromActivity());
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityUtil.showErrorDialog(getActivity(), mCommunicator.getFragmentManagerFromActivity());
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void setPresenter(MainContract.RoboCamPresenterContract presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setCommunicator(MainContract.ActivityCommunicator communicator) {
        mCommunicator = communicator;
    }

    @Override
    public void showWifiP2pStatus(String message) {

    }

    @Override
    public void setCameraManager(CamManager object) {

    }

    @Override
    public void takePic() {
        mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                                      @Override
                                      public void onShutter() {

                                      }
                                  },
                new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        File file = ActivityUtil.createImageFile(mContext);
                        FileOutputStream output = null;
                        try {
                            output = new FileOutputStream(file);
                            output.write(bytes);
                            Toast.makeText(mContext, "Picture taken", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void startDiscovery() {
        if (mCommunicator != null)
            mCommunicator.discoverService();
    }

    @Override
    public void enableFaceDetection() {
        mGraphicOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableFaceDetection() {
        mGraphicOverlay.setVisibility(View.GONE);
    }

    @Override
    public void enableSmileToSnap() {
        Toast.makeText(mContext,getString(R.string.smile_to_snap_is_enabled),Toast.LENGTH_SHORT).show();
        mSmileToSnap = true;
    }

    @Override
    public void disableSmileToSnap() {
        Toast.makeText(mContext,getString(R.string.smile_to_snap_is_disabled),Toast.LENGTH_SHORT).show();
        mSmileToSnap = false;
    }

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        public GraphicFaceTracker(GraphicOverlay overlay) {
            this.mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            Log.d(TAG, "smiling probability : " + face.getIsSmilingProbability());
            if (mSmileToSnap && face.getIsSmilingProbability() > SMILE_THRESHOLD) {
                takePic();
            }
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }

    }
}
