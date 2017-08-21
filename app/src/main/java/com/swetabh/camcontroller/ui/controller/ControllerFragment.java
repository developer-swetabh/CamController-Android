package com.swetabh.camcontroller.ui.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swetabh.camcontroller.R;
import com.swetabh.camcontroller.base.MainContract;
import com.swetabh.camcontroller.cammanager.CamManager;
import com.swetabh.camcontroller.constants.AppConstant;

public class ControllerFragment extends Fragment implements MainContract.ControllerViewContract, View.OnClickListener {

    private MainContract.ControllerPresenterContract mPresenter;
    private MainContract.ActivityCommunicator mCommunicator;
    private CamManager camManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_controller, container, false);

        view.findViewById(R.id.btn_take_pic).setOnClickListener(this);
        view.findViewById(R.id.btn_enable_face_detection).setOnClickListener(this);
        view.findViewById(R.id.btn_disable_face_detection).setOnClickListener(this);
        return view;
    }

    @Override
    public void setPresenter(MainContract.ControllerPresenterContract presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setCommunicator(MainContract.ActivityCommunicator communicator) {
        this.mCommunicator = communicator;
    }

    @Override
    public void showWifiP2pStatus(String message) {

    }

    @Override
    public void setCameraManager(CamManager object) {
        this.camManager = object;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_pic:
                takePic();
                break;
            case R.id.btn_enable_face_detection:
                enableFaceDetection();
                break;
            case R.id.btn_disable_face_detection:
                disableFaceDetection();
                break;
        }
    }

    private void disableFaceDetection() {
        if (camManager != null) {
            camManager.write(AppConstant.DISABLE_FACE_DETECTION.getBytes());
        }
    }

    private void enableFaceDetection() {
        if (camManager != null) {
            camManager.write(AppConstant.ENABLE_FACE_DETECTION.getBytes());
        }
    }

    private void takePic() {
        if (camManager != null) {
            camManager.write(AppConstant.TAKE_PICTURE.getBytes());
        }
    }

    @Override
    public void setCamManager(CamManager camManager) {
        this.camManager = camManager;
    }

}
