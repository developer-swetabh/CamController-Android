package com.swetabh.camcontroller.base;

import android.support.v4.app.FragmentManager;

import com.swetabh.camcontroller.cammanager.CamManager;
import com.swetabh.camcontroller.model.WifiP2pService;

/**
 * Created by swets on 10-08-2017.
 */

public interface MainContract {

    interface ActivityCommunicator {
        void openMainFragment();

        void discoverService();

        void connectP2p(WifiP2pService service);

        void openWifiServicesListFragment();

        void removeGroups();

        FragmentManager getFragmentManagerFromActivity();

        void openRoboCamFragment();

        void openFaceDetectorCamFragment();
    }

    interface MainPresenterContract extends BasePresenter {
    }

    interface MainViewContract extends BaseView<MainPresenterContract, ActivityCommunicator> {

    }

    interface WifiServiceListPresenterContract extends BasePresenter {
        void addService(WifiP2pService service);
    }

    interface WifiServiceListViewContract extends BaseView<WifiServiceListPresenterContract, ActivityCommunicator> {

        void addService(WifiP2pService service);

        void startDiscovery();
    }


    interface RoboCamPresenterContract extends BasePresenter {


        void setAction(String readAction);
    }

    interface RoboCamViewContract extends BaseView<RoboCamPresenterContract, ActivityCommunicator> {

        void takePic();

        void startDiscovery();

        void enableFaceDetection();

        void disableFaceDetection();
    }

    interface ControllerPresenterContract extends BasePresenter {
        void setCamManager(CamManager camManager);
    }

    interface ControllerViewContract extends BaseView<ControllerPresenterContract, ActivityCommunicator> {
        void setCamManager(CamManager camManager);
    }

}
