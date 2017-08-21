package com.swetabh.camcontroller.base;

import com.swetabh.camcontroller.cammanager.CamManager;

/**
 * Created by swets on 10-08-2017.
 */

public interface BasePresenter {

    void start();

    void attachView(BaseView view);


    void appendMessage(String message);

    void setCamManager(CamManager object);
}
