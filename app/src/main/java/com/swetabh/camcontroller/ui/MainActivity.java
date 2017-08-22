package com.swetabh.camcontroller.ui;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.swetabh.camcontroller.R;
import com.swetabh.camcontroller.base.BasePresenter;
import com.swetabh.camcontroller.base.BaseView;
import com.swetabh.camcontroller.base.MainContract;
import com.swetabh.camcontroller.broadcastreceiver.WifiDirectBroadCastReceiver;
import com.swetabh.camcontroller.cammanager.CamManager;
import com.swetabh.camcontroller.constants.AppConstant;
import com.swetabh.camcontroller.model.WifiP2pService;
import com.swetabh.camcontroller.sockethandler.CamSocketHandler;
import com.swetabh.camcontroller.sockethandler.GroupOwnerSocketHandler;
import com.swetabh.camcontroller.ui.controller.ControllerFragment;
import com.swetabh.camcontroller.ui.controller.ControllerPresenter;
import com.swetabh.camcontroller.ui.controller.WifiServiceListFragment;
import com.swetabh.camcontroller.ui.controller.WifiServiceListPresenter;
import com.swetabh.camcontroller.ui.main.MainFragment;
import com.swetabh.camcontroller.ui.main.MainPresenter;
import com.swetabh.camcontroller.ui.robocam.FaceDetectionFragment;
import com.swetabh.camcontroller.ui.robocam.RoboCamFragment;
import com.swetabh.camcontroller.ui.robocam.RoboCamPresenter;
import com.swetabh.camcontroller.utils.ActivityUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements MainContract.ActivityCommunicator, Handler.Callback,
        WifiP2pManager.ConnectionInfoListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private final IntentFilter intentFilter = new IntentFilter();
    private Stack<BasePresenter> mPresenterStack;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private BasePresenter mPresenter;
    private Handler mHandler = new Handler(this);
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver = null;
    private WifiP2pDnsSdServiceRequest mServiceRequest;
    private WifiDirectBroadCastReceiver receiver;

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
        mPresenterStack = new Stack<>();
        mCurrentFragment = mFragmentManager.findFragmentById(R.id.container);

        // adding action to intent filter
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mWifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);
        startRegistrationAndDiscovery();
        if (mCurrentFragment == null) {
            openMainFragment();
        }

    }

    /**
     * Registers a local service and then initiates a service discovery
     */
    private void startRegistrationAndDiscovery() {
        Map<String, String> record = new HashMap<>();
        record.put(AppConstant.TXT_RECORD_PROP_AVAILABLE, "visible");

        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
                AppConstant.SERVICE_INSTANCE,
                AppConstant.SERVICE_REG_TYPE,
                record
        );
        mWifiP2pManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Added Local Service");
            }

            @Override
            public void onFailure(int reason) {
                mPresenter.appendMessage("Failed to add a service");
            }
        });

        //discoverService();
    }

    public void discoverService() {
        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */

        mWifiP2pManager.setDnsSdResponseListeners(mChannel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(
                            String instanceName,
                            String registrationType,
                            WifiP2pDevice srcDevice) {

                        if (instanceName.equalsIgnoreCase(AppConstant.SERVICE_INSTANCE)) {
                            WifiP2pService service = new WifiP2pService();
                            service.device = srcDevice;
                            service.instanceName = instanceName;
                            service.serviceRegistrationType = registrationType;
                            if (mCurrentFragment instanceof WifiServiceListFragment)
                                ((MainContract.WifiServiceListPresenterContract) mPresenter).addService(service);
                            Log.d(TAG, "onBonjourServiceAvailable "
                                    + instanceName);
                        }

                    }
                },
                new WifiP2pManager.DnsSdTxtRecordListener() {
                    /*
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName,
                            Map<String, String> txtRecordMap,
                            WifiP2pDevice srcDevice) {

                        Log.d(TAG,
                                srcDevice.deviceName + " is "
                                        + txtRecordMap.get(AppConstant.TXT_RECORD_PROP_AVAILABLE));

                    }
                });
        /*
        * After attaching listener, create a service request and initiate discovery
        * */
        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mWifiP2pManager.addServiceRequest(mChannel, mServiceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mPresenter.appendMessage("Added service discovery request");
            }

            @Override
            public void onFailure(int reason) {
                mPresenter.appendMessage("Failed adding service discovery request");
            }
        });

        mWifiP2pManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mPresenter.appendMessage("Service discovery initiated");
            }

            @Override
            public void onFailure(int reason) {
                mPresenter.appendMessage("Service discovery failed");
            }
        });
    }

    @Override
    public void connectP2p(WifiP2pService service) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (mServiceRequest != null) {
            mWifiP2pManager.removeServiceRequest(mChannel, mServiceRequest, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    mPresenter.appendMessage("previous connection removed");
                }

                @Override
                public void onFailure(int reason) {
                    mPresenter.appendMessage("failed to remove connection");
                }
            });
        }
        mWifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                mPresenter.appendMessage("Connecting to service");
            }

            @Override
            public void onFailure(int errorCode) {
                mPresenter.appendMessage("Failed connecting to service");
            }
        });
    }

    @Override
    public void openWifiServicesListFragment() {
        mCurrentFragment = Fragment.instantiate(this, WifiServiceListFragment.class.getName());
        mPresenter = new WifiServiceListPresenter();
        attachBasicThings();
        mPresenterStack.push(mPresenter);
        ActivityUtil.addFragmentToActivity(mFragmentManager, mCurrentFragment, AppConstant.WIFI_SERVICE_LIST_FRAGMENT);
    }

    @Override
    public void removeGroups() {
        if (mWifiP2pManager != null && mWifiP2pManager != null) {
            mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                }

                @Override
                public void onSuccess() {
                }

            });
        }
    }

    @Override
    public FragmentManager getFragmentManagerFromActivity() {
        return mFragmentManager;
    }

    @Override
    public void openRoboCamFragment() {
        mCurrentFragment = Fragment.instantiate(this, RoboCamFragment.class.getName());
        mPresenter = new RoboCamPresenter();
        attachBasicThings();
        mPresenterStack.push(mPresenter);
        ActivityUtil.addFragmentToActivity(mFragmentManager, mCurrentFragment, AppConstant.ROBO_CAM_FRAGMENT);
    }

    @Override
    public void openFaceDetectorCamFragment() {
        mCurrentFragment = Fragment.instantiate(this, FaceDetectionFragment.class.getName());
        mPresenter = new RoboCamPresenter();
        attachBasicThings();
        mPresenterStack.push(mPresenter);
        ActivityUtil.addFragmentToActivity(mFragmentManager, mCurrentFragment, AppConstant.FACE_DETECTION_CAM_FRAGMENT);
    }

    @Override
    public void openMainFragment() {
        mCurrentFragment = Fragment.instantiate(this, MainFragment.class.getName());
        mPresenter = new MainPresenter();
        attachBasicThings();
        mPresenterStack.push(mPresenter);
        ActivityUtil.addFragmentToActivity(mFragmentManager, mCurrentFragment, AppConstant.MAIN_FRAGMENT);
    }

    private void attachBasicThings() {
        attachCommunicator();
        attachView();
    }

    private void attachView() {
        if (mPresenter != null)
            mPresenter.attachView((BaseView) mCurrentFragment);
    }

    private void attachCommunicator() {
        if (mCurrentFragment != null && mCurrentFragment instanceof BaseView)
            ((BaseView) mCurrentFragment).setCommunicator(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WifiDirectBroadCastReceiver(mWifiP2pManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = mFragmentManager.getBackStackEntryCount();
        if (backStackEntryCount == 0 || backStackEntryCount == 1) {
            finish();
        } else {
            String fragmentTag = mFragmentManager.getBackStackEntryAt(backStackEntryCount - 2).getName();
            mCurrentFragment = mFragmentManager.findFragmentByTag(fragmentTag);
            if (!mPresenterStack.isEmpty()) {
                mPresenterStack.pop();
                mPresenter = mPresenterStack.peek();
                attachCommunicator();
                attachView();

            }
            super.onBackPressed();
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case AppConstant.ACTION_GET:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readAction = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, readAction);
                ((MainContract.RoboCamPresenterContract) mPresenter).setAction(readAction);
                break;
            case AppConstant.ACTION_SEND:
                Object object = msg.obj;
                mPresenter.setCamManager((CamManager) object);
                break;
        }
        return false;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Thread handler = null;

        if (p2pInfo.isGroupOwner) {
            try {
                handler = new GroupOwnerSocketHandler(getHandler());
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to create a server thread - " + e.getMessage());
            }
            Log.d(TAG, "Connected as group owner");
        } else {
            handler = new CamSocketHandler(getHandler(), p2pInfo.groupOwnerAddress);
            handler.start();
            Log.d(TAG, "connected as peer");
        }
        if (mCurrentFragment instanceof WifiServiceListFragment) {
            openControllerFragment();
        }
    }

    private void openControllerFragment() {
        mCurrentFragment = Fragment.instantiate(this, ControllerFragment.class.getName());
        mPresenter = new ControllerPresenter();
        attachBasicThings();
        mPresenterStack.push(mPresenter);
        ActivityUtil.addFragmentToActivity(mFragmentManager, mCurrentFragment, AppConstant.CONTROLLER_FRAGMENT);
    }

    @Override
    protected void onStop() {
        removeGroups();
        super.onStop();
    }

}
