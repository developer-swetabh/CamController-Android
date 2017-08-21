package com.swetabh.camcontroller.base;

import com.swetabh.camcontroller.cammanager.CamManager;

/**
 * Created by swets on 10-08-2017.
 */

public class BasePresenterImp<V extends BaseView> implements BasePresenter {

    public V mView;

    @Override
    public void start() {

    }

    @Override
    public void attachView(BaseView view) {
        mView = (V) view;
        if (mView != null) {
            mView.setPresenter(this);
        }
    }

    @Override
    public void appendMessage(String message) {
        if(mView!=null){
            mView.showWifiP2pStatus(message);
        }
    }

    @Override
    public void setCamManager(CamManager object) {
        if(mView!=null){
            mView.setCameraManager(object);
        }
    }
}
