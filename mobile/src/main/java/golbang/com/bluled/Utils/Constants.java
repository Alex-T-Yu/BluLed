package golbang.com.bluled.Utils;

/**
 * Created by yoosung-jong on 14. 11. 7..
 */
public class Constants {

    // Notification intent action string
    public static final String NOTIFICATION_LISTENER_SERVICE = "golbang.com.bluled.NOTIFICATION_LISTENER_SERVICE";
    public static final String NOTIFICATION_LISTENER = "golbang.com.bluled.NOTIFICATION_LISTENER";

    // Service handler message key
    public static final String SERVICE_HANDLER_MSG_KEY_DEVICE_NAME = "device_name";
    public static final String SERVICE_HANDLER_MSG_KEY_TOAST = "toast";

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    // Message types sent from the BluetoothManager to Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Message types sent from Service to Activity
    public static final int MESSAGE_NOT_CONNECTED = 1;
}
