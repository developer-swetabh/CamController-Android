package com.swetabh.camcontroller.ui.controller;

import com.swetabh.camcontroller.base.BasePresenterImp;
import com.swetabh.camcontroller.base.MainContract;
import com.swetabh.camcontroller.model.WifiP2pService;

/**
 * Created by swets on 10-08-2017.
 */

public class WifiServiceListPresenter extends BasePresenterImp<MainContract.WifiServiceListViewContract>
        implements MainContract.WifiServiceListPresenterContract {

    @Override
    public void start() {
        super.start();
        if(mView!=null){
            mView.startDiscovery();
        }
    }

    @Override
    public void addService(WifiP2pService service) {
        if (mView != null){
            mView.addService(service);
        }
    }
}
