package golbang.com.bluled.Service;

/**
 * Created by yoosung-jong on 14. 11. 6..
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import golbang.com.bluled.Utils.Constants;

public class NotificationReceiverService extends NotificationListenerService {

    private static final String TAG = "NotificationReceiverService";

    // Notification broadcast intent key
    public static final String NOTIFICATION_KEY_CMD = "notification_command";
    public static final String NOTIFICATION_KEY_ID = "notification_id";
    public static final String NOTIFICATION_KEY_PACKAGE = "notification_package";
    public static final String NOTIFICATION_KEY_TEXT = "notification_text";

    // Notification command type
    public static final int NOTIFICATION_CMD_ADD = 1;
    public static final int NOTIFICATION_CMD_REMOVE = 2;
    public static final int NOTIFICATION_CMD_LIST = 3;

    private NLServiceReceiver nlservicereciver;


    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NOTIFICATION_LISTENER_SERVICE);
        registerReceiver(nlservicereciver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "**********  onNotificationPosted");
        Log.d(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());

        Intent i = new  Intent(Constants.NOTIFICATION_LISTENER);
        i.putExtra(NOTIFICATION_KEY_CMD, NOTIFICATION_CMD_ADD);
        i.putExtra(NOTIFICATION_KEY_ID, sbn.getId());
        i.putExtra(NOTIFICATION_KEY_PACKAGE, sbn.getPackageName());
        i.putExtra(NOTIFICATION_KEY_TEXT, sbn.getNotification().tickerText);
        sendBroadcast(i);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG,"********** onNOtificationRemoved");
        Log.d(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText +"t" + sbn.getPackageName());

        Intent i = new  Intent(Constants.NOTIFICATION_LISTENER);
        i.putExtra(NOTIFICATION_KEY_CMD, NOTIFICATION_CMD_REMOVE);
        i.putExtra(NOTIFICATION_KEY_ID, sbn.getId());
        i.putExtra(NOTIFICATION_KEY_PACKAGE, sbn.getPackageName());
        i.putExtra(NOTIFICATION_KEY_TEXT, sbn.getNotification().tickerText);
        sendBroadcast(i);
    }


    class NLServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("command").equals("clearall")){
                NotificationReceiverService.this.cancelAllNotifications();
            }
            else if(intent.getStringExtra("command").equals("list")){
                for (StatusBarNotification sbn : NotificationReceiverService.this.getActiveNotifications()) {
                    Intent i2 = new  Intent(Constants.NOTIFICATION_LISTENER);
                    i2.putExtra(NOTIFICATION_KEY_CMD, NOTIFICATION_CMD_LIST);
                    i2.putExtra(NOTIFICATION_KEY_ID, sbn.getId());
                    i2.putExtra(NOTIFICATION_KEY_PACKAGE, sbn.getPackageName());
                    i2.putExtra(NOTIFICATION_KEY_TEXT, sbn.getNotification().tickerText);
                    sendBroadcast(i2);
                }
            }

        }
    }	// End of class NLServiceReceiver

}
