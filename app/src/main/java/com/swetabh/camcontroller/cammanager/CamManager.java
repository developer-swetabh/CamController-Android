package com.swetabh.camcontroller.cammanager;

import android.os.Handler;
import android.util.Log;

import com.swetabh.camcontroller.constants.AppConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by swets on 14-08-2017.
 */

/**
 * Handles reading actions with socket buffers. Uses a handler  to post message
 * to UI thread for UI updates.
 */
public final class CamManager implements Runnable {

    private static final String TAG = CamManager.class.getSimpleName();
    private final Socket mSocket;
    private final Handler mHandler;

    private InputStream iStream;
    private OutputStream oStream;

    public CamManager(Socket socket, Handler handler) {
        this.mSocket = socket;
        this.mHandler = handler;
    }

    @Override
    public void run() {
        try {
            iStream = mSocket.getInputStream();
            oStream = mSocket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;
            mHandler.obtainMessage(AppConstant.ACTION_SEND, this).sendToTarget();
            while (true) {
                try {
                    // read from input stream
                    bytes = iStream.read(buffer);
                    if (bytes == -1) {
                        return;
                    }
                    //send the obtain bytes ti UI Activity
                    Log.d(TAG, "Received Message : " + String.valueOf(buffer));
                    mHandler.obtainMessage(AppConstant.ACTION_GET, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            oStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }
}
