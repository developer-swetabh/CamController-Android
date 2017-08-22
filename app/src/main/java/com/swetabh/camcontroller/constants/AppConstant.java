package com.swetabh.camcontroller.constants;

/**
 * Created by swets on 10-08-2017.
 */

public class AppConstant {

    // Fragment tags
    public static final String MAIN_FRAGMENT = "main_fragment";
    public static final String WIFI_SERVICE_LIST_FRAGMENT = "wifi_service_list_fragment";
    public static final String ROBO_CAM_FRAGMENT = "robo_cam_fragment";
    public static final String FACE_DETECTION_CAM_FRAGMENT = "face_detection_cam_fragment";
    public static final String CONTROLLER_FRAGMENT = "controller_fragment";
    public static final String FRAGMENT_DIALOG = "dialog";

    //server port number
    public static final int SERVER_PORT = 7621;

    // time out for socket connection
    public static final int CONNECTION_TIME_OUT = 5000;

    // TXT RECORD properties
    public static final String TXT_RECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_robocamtest";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    // Handler what
    public static final int ACTION_GET = 0x400 + 1;
    public static final int ACTION_SEND = 0x400 + 2;

    // Constants for camera
    public static final String TAKE_PICTURE = "take_picture";
    public static final String ENABLE_FACE_DETECTION = "enable_face_detection";
    public static final String DISABLE_FACE_DETECTION = "disable_face_detection";

    //camera permission request code
    public static final int REQUEST_CAMERA_PERMISSION = 1;

}
