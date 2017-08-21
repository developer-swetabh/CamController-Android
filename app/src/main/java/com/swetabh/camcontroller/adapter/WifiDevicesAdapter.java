package com.swetabh.camcontroller.adapter;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swetabh.camcontroller.model.WifiP2pService;

import java.util.List;

/**
 * Created by swets on 10-08-2017.
 */

public class WifiDevicesAdapter extends RecyclerView.Adapter<WifiDevicesAdapter.ViewHolder> {

    private List<WifiP2pService> items;
    private OnItemClick mListener;

    public WifiDevicesAdapter(List<WifiP2pService> list, OnItemClick listener) {
        items = list;
        mListener = listener;
    }

    @Override
    public WifiDevicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WifiDevicesAdapter.ViewHolder holder, int position) {
        WifiP2pService service = items.get(position);
        holder.vhDevice.setText(service.device.deviceName + " - " + service.instanceName);
        holder.vhStatus.setText(getDeviceStatus(service.device.status));
    }

    private String getDeviceStatus(int status) {
        switch (status) {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addWifiP2pService(WifiP2pService service) {
        items.add(service);
        notifyDataSetChanged();
    }

    public interface OnItemClick {
        void onDeviceClick(WifiP2pService service);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView vhDevice;
        TextView vhStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            vhDevice = (TextView) itemView.findViewById(android.R.id.text1);
            vhStatus = (TextView) itemView.findViewById(android.R.id.text2);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                mListener.onDeviceClick(items.get(getAdapterPosition()));
            }
        }
    }
}
