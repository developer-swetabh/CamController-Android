package com.swetabh.camcontroller.ui.robocam;

import com.swetabh.camcontroller.base.BasePresenterImp;
import com.swetabh.camcontroller.base.MainContract;
import com.swetabh.camcontroller.constants.AppConstant;

/**
 * Created by swets on 11-08-2017.
 */

public class RoboCamPresenter extends BasePresenterImp<MainContract.RoboCamViewContract>
        implements MainContract.RoboCamPresenterContract {

    @Override
    public void start() {
        super.start();
        if (mView != null) {
            mView.startDiscovery();
        }
    }

    @Override
    public void setAction(String readAction) {
        if (mView != null) {
            if (readAction.equals(AppConstant.TAKE_PICTURE))
                mView.takePic();
            else if (readAction.equals(AppConstant.ENABLE_FACE_DETECTION)) {
                mView.enableFaceDetection();
            } else if (readAction.equals(AppConstant.DISABLE_FACE_DETECTION)) {
                mView.disableFaceDetection();
            }
        }
    }
}
