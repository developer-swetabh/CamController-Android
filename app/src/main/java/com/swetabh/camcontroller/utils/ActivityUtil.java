package com.swetabh.camcontroller.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.swetabh.camcontroller.R;
import com.swetabh.camcontroller.constants.AppConstant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.swetabh.camcontroller.constants.AppConstant.FRAGMENT_DIALOG;

/**
 * Created by swets on 10-08-2017.
 */

public class ActivityUtil {

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, String tag) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }


    public static void showErrorDialog(Context context, FragmentManager manager) {
        ErrorDialog.newInstance(context.getString(R.string.camera_error))
                .show(manager, FRAGMENT_DIALOG);
    }

    public static void showConfirmationDialog(Context context, FragmentManager manager) {
        new ConfirmationDialog().show(manager, FRAGMENT_DIALOG);
    }

    public static void showOperationalAlert(Context mContext, FragmentManager manager) {
        OperationalAlertDialog.newInstance(mContext.getString(R.string.face_detector_alert))
                .show(manager, FRAGMENT_DIALOG);
    }

    /**
     * @param context - takes context
     *
     *
     * creates a file with name as "faceTracker_201708221509.jpeg
     */
    public static File createImageFile(Context context) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "faceTracker_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(null);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = new File(storageDir, File.separator + imageFileName + ".jpg");
        return image;
    }


    /**
     * {@class OperationalAlertDialog} is used to show dialog when there is an error in requesting permission.
     */
    public static class OperationalAlertDialog extends android.support.v4.app.DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static OperationalAlertDialog newInstance(String message) {
            OperationalAlertDialog dialog = new OperationalAlertDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
        }

    }

    /**
     * {@class ErrorDialog} is used to show dialog when there is an error in requesting permission.
     */
    public static class ErrorDialog extends android.support.v4.app.DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }


    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends android.support.v4.app.DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission.CAMERA},
                                        AppConstant.REQUEST_CAMERA_PERMISSION);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

}
