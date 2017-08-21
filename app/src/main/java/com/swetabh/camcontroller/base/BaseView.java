package com.swetabh.camcontroller.base;

import com.swetabh.camcontroller.cammanager.CamManager;

/**
 * Created by swets on 10-08-2017.
 */

public interface BaseView<T, V> {
    void setPresenter(T presenter);

    void setCommunicator(V communicator);

    void showWifiP2pStatus(String message);

    void setCameraManager(CamManager object);
}
