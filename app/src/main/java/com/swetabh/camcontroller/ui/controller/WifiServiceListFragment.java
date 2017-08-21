package com.swetabh.camcontroller.ui.controller;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.swetabh.camcontroller.R;
import com.swetabh.camcontroller.adapter.WifiDevicesAdapter;
import com.swetabh.camcontroller.base.MainContract;
import com.swetabh.camcontroller.cammanager.CamManager;
import com.swetabh.camcontroller.model.WifiP2pService;
import com.swetabh.camcontroller.ui.MainActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class WifiServiceListFragment extends Fragment implements MainContract.WifiServiceListViewContract, WifiDevicesAdapter.OnItemClick {


    private MainContract.WifiServiceListPresenterContract mPresenter;
    private MainContract.ActivityCommunicator mCommunicator;
    private RecyclerView mDevicesRecyclerView;
    private WifiDevicesAdapter mAdapter;
    private Context mContext;
    private LinearLayout mFindingServiceLayout;

    public WifiServiceListFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wifi_service_list, container, false);
        mFindingServiceLayout = (LinearLayout) view.findViewById(R.id.ll_empty);
        mDevicesRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new WifiDevicesAdapter(new ArrayList<WifiP2pService>(), this);
        mDevicesRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.start();
        }
    }

    @Override
    public void setPresenter(MainContract.WifiServiceListPresenterContract presenter) {
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

    @Override
    public void addService(WifiP2pService service) {
        if (mAdapter != null) {
            mFindingServiceLayout.setVisibility(View.GONE);
            mAdapter.addWifiP2pService(service);
        }
    }

    @Override
    public void startDiscovery() {
        mCommunicator.discoverService();
    }

    @Override
    public void onDeviceClick(WifiP2pService service) {
        if (mCommunicator != null) {
            mCommunicator.connectP2p(service);
        }
    }

}
