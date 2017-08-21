package com.swetabh.camcontroller.sockethandler;

import android.os.Handler;
import android.util.Log;

import com.swetabh.camcontroller.cammanager.CamManager;
import com.swetabh.camcontroller.constants.AppConstant;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by swets on 12-08-2017.
 */

public final class CamSocketHandler extends Thread {

    private static final String TAG = CamSocketHandler.class.getSimpleName();
    private final Handler mHandler;
    private final InetAddress mAddress;
    private CamManager mCamManager;

    public CamSocketHandler(Handler handler, InetAddress groupOwnerAddress) {
        this.mHandler = handler;
        this.mAddress = groupOwnerAddress;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(
                    new InetSocketAddress(mAddress.getHostAddress(), AppConstant.SERVER_PORT),
                    AppConstant.CONNECTION_TIME_OUT);
            Log.d(TAG, "Launching the I/O handler");
            mCamManager = new CamManager(socket, mHandler);
            new Thread(mCamManager).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }
}
