package com.swetabh.camcontroller.ui.controller;

import com.swetabh.camcontroller.base.BasePresenterImp;
import com.swetabh.camcontroller.base.MainContract;
import com.swetabh.camcontroller.cammanager.CamManager;

/**
 * Created by swets on 14-08-2017.
 */

public class ControllerPresenter extends BasePresenterImp<MainContract.ControllerViewContract>
        implements MainContract.ControllerPresenterContract {

    @Override
    public void setCamManager(CamManager camManager) {
        if(mView!=null){
            mView.setCamManager(camManager);
        }
    }
}
