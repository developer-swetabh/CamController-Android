package com.swetabh.camcontroller.sockethandler;

import android.os.Handler;
import android.util.Log;

import com.swetabh.camcontroller.cammanager.CamManager;
import com.swetabh.camcontroller.constants.AppConstant;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by swets on 14-08-2017.
 */

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 */
public final class GroupOwnerSocketHandler extends Thread {

    private static final String TAG = GroupOwnerSocketHandler.class.getSimpleName();
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private ServerSocket socket;

    /*
    * A thread pool for client sockets
    */
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>()
    );

    public GroupOwnerSocketHandler(Handler handler) throws IOException {
        try {
            socket = new ServerSocket(AppConstant.SERVER_PORT);
            this.handler = handler;
            Log.d("GroupOwnerSocketHandler", "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // A blocking operation. Initiate a CamManager instance when
                // there is a new connection
                pool.execute(new CamManager(socket.accept(), handler));
                Log.d(TAG, "Launching the I/O handler");
            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                e.printStackTrace();
                pool.shutdownNow();
            }
        }
    }
}
