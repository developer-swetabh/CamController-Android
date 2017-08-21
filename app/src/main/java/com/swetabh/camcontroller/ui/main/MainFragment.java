package com.swetabh.camcontroller.ui.main;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.swetabh.camcontroller.R;
import com.swetabh.camcontroller.base.MainContract;
import com.swetabh.camcontroller.cammanager.CamManager;
import com.swetabh.camcontroller.ui.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener, MainContract.MainViewContract {


    private Context mContext;
    private MainContract.MainPresenterContract mPresenter;
    private MainContract.ActivityCommunicator mCommunicator;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        view.findViewById(R.id.button_act_as_cam).setOnClickListener(this);
        view.findViewById(R.id.button_act_as_controller).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_act_as_cam:
                if (mCommunicator != null) {
                    mCommunicator.openRoboCamFragment();
                }
                Toast.makeText(mContext, "Cam Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_act_as_controller:
                if (mCommunicator != null) {
                    mCommunicator.openWifiServicesListFragment();
                }
                Toast.makeText(mContext, "controller clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void setPresenter(MainContract.MainPresenterContract presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setCommunicator(MainContract.ActivityCommunicator communicator) {
        mCommunicator = communicator;
    }

    @Override
    public void showWifiP2pStatus(String message) {
        Log.d(MainActivity.TAG, message);
    }

    @Override
    public void setCameraManager(CamManager object) {

    }

}
