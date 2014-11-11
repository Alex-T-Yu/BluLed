package golbang.com.bluled.Service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import golbang.com.bluled.Utils.Constants;
import golbang.com.bluled.Connectivity.BluetoothManager;

/**
 * Created by yoosung-jong on 14. 11. 7..
 */
public class BluetoothLEDService extends Service{
    private static final String TAG = "RetroWatchService";

    // Context, System
    private Context mContext = null;
    private static Handler mActivityHandler = null;
    private ServiceHandler mServiceHandler = new ServiceHandler();
    private IServiceListener mServiceListener = new ServiceListenerImpl();
    private final IBinder mBinder = new BluetoothLEDServiceBinder();

    // Notification broadcast receiver
    private NotificationReceiver mReceiver;

    // Bluetooth
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothManager mBtManager = null;

    private String mConnectedDeviceName = null;		// Name of the connected device


    /*****************************************************
     *	Overrided methods
     ******************************************************/
    @Override
    public void onCreate() {
        Log.d(TAG, "#");
        Log.d(TAG, "# Service - onCreate() starts here");

        mContext = getApplicationContext();
        initialize();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "#");
        Log.d(TAG, "# Service - onStartCommand() starts here");

        // If service returns START_STICKY, android restarts service automatically after forced close.
        // At this time, onStartCommand() method in service must handle null intent.
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        finalizeService();
    }

    @Override
    public void onLowMemory (){
        // onDestroy is not always called when applications are finished by Android system.
        finalizeService();
    }

    /*****************************************************
     *	Private methods
     ******************************************************/
    private void initialize() {
        Log.d(TAG, "# Service : initialize ---");

        // Set broadcast receiver
        mReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NOTIFICATION_LISTENER);
        registerReceiver(mReceiver,filter);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            // BT is not on, need to turn on manually
        } else {
            if(mBtManager == null)
                setupBT();
        }
    }





    /*****************************************************
     *	Public methods
     ******************************************************/
    public void finalizeService() {
        Log.d(TAG, "# Service : finalize ---");

        mBluetoothAdapter = null;
        // Stop the bluetooth session
        if (mBtManager != null)
            mBtManager.stop();
        mBtManager = null;
        // Unregister broadcast receiver
        if(mReceiver != null)
            unregisterReceiver(mReceiver);
        mReceiver = null;
    }

    public void setActivityHandler(Handler h) {
        mActivityHandler = h;
    }

    /**
     * Setup and initialize BT manager
     */
    public void setupBT() {
        Log.d(TAG, "Service - setupBT()");

        // Initialize the BluetoothManager to perform bluetooth connections
        mBtManager = new BluetoothManager(this, mServiceHandler);
        if (mBtManager.getState() == BluetoothManager.STATE_NONE) {
            // Start the bluetooth services
            mBtManager.start();
        }
    }

    /**
     * Check bluetooth is enabled or not.
     */
    public boolean isBluetoothEnabled() {
        if(mBluetoothAdapter==null) {
            Log.e(TAG, "# Service - cannot find bluetooth adapter. Restart app.");
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * Get scan mode
     */
    public int getBluetoothScanMode() {
        int scanMode = -1;
        if(mBluetoothAdapter != null)
            scanMode = mBluetoothAdapter.getScanMode();

        return scanMode;
    }


    /**
     * Return the current connection state.
     */
//	public int getState() {
//		return (mBtManager != null) ? mBtManager.getState() : BluetoothManager.STATE_NONE;
//	}

    /**
     * Start AcceptThread to begin a bluetooth session in listening (server) mode.
     */
//	public void startBluetooth() {
//		Logs.d(TAG, "Service - mBtManager.start() ");
//
//		if(mBtManager != null)
//			mBtManager.start();
//	}

    /**
     * Initiate a connection to a remote device.
     * @param address  Device's MAC address to connect
     */
    public void connectDevice(String address) {
        Log.d(TAG, "Service - connect to " + address);

        // Get the BluetoothDevice object
        if(mBluetoothAdapter != null) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

            if(device != null && mBtManager != null) {
                mBtManager.connect(device);
            }
        }
    }

    /**
     * Connect to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public void connectDevice(BluetoothDevice device) {
        if(device != null && mBtManager != null) {
            mBtManager.connect(device);
        }
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket	The BluetoothSocket on which the connection was made
     * @param device	The BluetoothDevice that has been connected
     */
//	public void connected(BluetoothSocket socket, BluetoothDevice device) {
//		Logs.d(TAG, "Service - mBtManager.connected()");
//
//		if(socket != null && device != null && mBtManager != null) {
//			mBtManager.connected(socket, device);
//		}
//	}

    /**
     * Stop all bluetooth threads
     */
//	public synchronized void stopBluetooth() {
//		if(mBtManager != null)
//			mBtManager.stop();
//	}

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
//	public void write(byte[] out) {
//		if(out != null && mBtManager != null)
//			mBtManager.write(out);
//	}



    /**
     * Launch the DeviceListActivity to see devices and do scan
     */
//	public void doScan() {
//        Intent serverIntent = new Intent(this, DeviceListActivity.class);
//        startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE);
//    }

    /**
     * Launch notification settings screen
     */
//	public void setNotificationAccess() {
//    	Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//    	startActivity(intent);
//    }

    /**
     * Ensure this device is discoverable by others
     */
//	public void ensureDiscoverable() {
//        Logs.d(TAG, "ensure discoverable");
//        if (mBluetoothAdapter.getScanMode() !=
//            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            startActivity(discoverableIntent);
//        }
//    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    public void sendMessageToRemote(String message) {
        // Check that we're actually connected before trying anything
        if (mBtManager.getState() != BluetoothManager.STATE_CONNECTED) {
            mActivityHandler.obtainMessage(Constants.MESSAGE_NOT_CONNECTED).sendToTarget();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] buf = message.getBytes();
            mBtManager.write(buf);
        }
    }

    /**
     * Sends a message.
     * @param buf  Buffer to send.
     */
    public void sendMessageToRemote(byte[] buf) {
        // Check that we're actually connected before trying anything
        if (mBtManager.getState() != BluetoothManager.STATE_CONNECTED) {
            mActivityHandler.obtainMessage(Constants.MESSAGE_NOT_CONNECTED).sendToTarget();
            return;
        }

        // Check that there's actually something to send
        if (buf.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            mBtManager.write(buf);
        }
    }

//	public void makeNotification(String title, String text, String ticker) {
//        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification.Builder ncomp = new Notification.Builder(this);
//        ncomp.setContentTitle(title);
//        if(text != null)
//        	ncomp.setContentText(text);
//        if(ticker != null)
//        	ncomp.setTicker(ticker);
//        ncomp.setSmallIcon(R.drawable.ic_launcher);
//        ncomp.setAutoCancel(true);
//        nManager.notify((int)System.currentTimeMillis(),ncomp.build());
//    }
//
//    private void clearAllNotifications() {
//        Intent i = new Intent("com.example.watcher.NOTIFICATION_LISTENER_SERVICE");
//        i.putExtra("command","clearall");
//        sendBroadcast(i);
//
//        mNotificationList.clear();
//        mConversationArrayAdapter.clear();
//    }
//
//    private void getAllNotifications() {
//        Intent i = new Intent("com.example.watcher.NOTIFICATION_LISTENER_SERVICE");
//        i.putExtra("command","list");
//        sendBroadcast(i);
//
//        mNotificationList.clear();
//        mConversationArrayAdapter.clear();
//    }

//    private String[] mFilters = {"android"};
//    private String[] mConverterBefore = {"gm"};
//    private String[] mConverterAfter = {"Gmail"};
//
//    private int sendNotifications() {
//    	// send time first
//    	Calendar c = Calendar.getInstance();
//    	int curNoon = c.get(Calendar.AM_PM);
//    	int curHour = c.get(Calendar.HOUR);
//    	int curMinute = c.get(Calendar.MINUTE);
//    	int curWeek = c.get(Calendar.DAY_OF_WEEK);
//
//    	byte[] t_buffer = new byte[5];
//    	t_buffer[0] = '@';
//    	t_buffer[1] = (byte)curNoon;
//    	t_buffer[2] = (byte)curHour;
//    	t_buffer[3] = (byte)curMinute;
//    	t_buffer[4] = (byte)curWeek;
//    	sendMessage(t_buffer);
//
//    	// send new list signal
//    	sendMessage("#");
//
//    	// send package name
//    	int count = 0;
//    	for(MyNotification noti : mNotificationList) {
//    		if( sendNotification(noti) )
//    			count++;
//    	}
//
//    	Toast.makeText(this, Integer.toString(count)+" notifications are sent", Toast.LENGTH_LONG).show();
//    	return count;
//    }
//
//    private boolean sendNotification(Notifications noti) {
//		if(noti.mPackageName == null)
//			return false;
//
//		boolean pass = true;
//		for(int i=0; i<mFilters.length; i++) {
//    		if( noti.mPackageName.startsWith(mFilters[i]) )
//    			pass = false;
//		}
//
//		// Filtering package name
//		String trimmedName = null;
//		if(pass) {
//			// Trim package name
//			if( noti.mPackageName.contains(".") ) {
//				trimmedName = noti.mPackageName.substring(noti.mPackageName.lastIndexOf(".") + 1);
//			} else {
//				trimmedName = noti.mPackageName;
//			}
//			// Convert name
//			for(int i=0; i<mConverterBefore.length; i++) {
//				if(mConverterBefore[i].equals(trimmedName))
//					trimmedName = mConverterAfter[i];
//			}
//			// Send
//			sendMessage("+" + trimmedName + ";");
//		}
//
//		return pass;
//    }



    /*****************************************************
     *	Sub classes
     ******************************************************/
    public class BluetoothLEDServiceBinder extends Binder {
        public BluetoothLEDService getService() {
            return BluetoothLEDService.this;
        }
    }

    class ServiceHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {

            switch(msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    // Changes of Bluetooth state
                    Log.d(TAG, "Service - MESSAGE_STATE_CHANGE: " + msg.arg1);

                    switch (msg.arg1) {
                        case BluetoothManager.STATE_CONNECTED:
                            break;

                        case BluetoothManager.STATE_CONNECTING:
                            break;

                        case BluetoothManager.STATE_LISTEN:
                        case BluetoothManager.STATE_NONE:
                            break;
                    }
                    break;

                case Constants.MESSAGE_WRITE:
                    Log.d(TAG, "Service - MESSAGE_WRITE: ");
                    break;

                case Constants.MESSAGE_READ:
                    Log.d(TAG, "Service - MESSAGE_READ: ");

                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String strBuf = new String(readBuf, 0, msg.arg1);
                    break;

                case Constants.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "Service - MESSAGE_DEVICE_NAME: ");

                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.SERVICE_HANDLER_MSG_KEY_DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;

                case Constants.MESSAGE_TOAST:
                    Log.d(TAG, "Service - MESSAGE_TOAST: ");

                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(Constants.SERVICE_HANDLER_MSG_KEY_TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;

            }	// End of switch(msg.what)

            super.handleMessage(msg);
        }
    }	// End of class MainHandler

    class ServiceListenerImpl implements IServiceListener
    {
        @Override
        public void OnReceiveCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4) {
            switch(msgType) {
                default:
                    break;
            }
        }
    }	// End of class ServiceListenerImpl

    /**
     * Broadcast receiver class. Receives notification data
     */
    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int cmd = intent.getIntExtra(NotificationReceiverService.NOTIFICATION_KEY_CMD, 0);
            int noti_id = intent.getIntExtra(NotificationReceiverService.NOTIFICATION_KEY_ID, -1);
            String packageName = intent.getStringExtra(NotificationReceiverService.NOTIFICATION_KEY_PACKAGE);
            String textTicker = intent.getStringExtra(NotificationReceiverService.NOTIFICATION_KEY_TEXT);

            switch(cmd) {
                case NotificationReceiverService.NOTIFICATION_CMD_LIST:
                case NotificationReceiverService.NOTIFICATION_CMD_ADD:
                    if(packageName != null) {
                        Log.d(TAG,"** Service - Add noti="+noti_id+", package="+packageName);
                        // TODO:
                    }
                    break;

                case NotificationReceiverService.NOTIFICATION_CMD_REMOVE:
                    Log.d(TAG,"** Service - Delete noti="+noti_id+", package="+packageName);

//            	for(int i = mNotificationList.size() - 1; i > -1; i--) {
//            		MyNotification noti = mNotificationList.get(i);
//            		if(noti.id == noti_id) {
//            			mNotificationList.remove(i);
//            			if(noti.mPackageName != null)
//            				mConversationArrayAdapter.remove(noti.mPackageName);
//            		}
//            	}
//
//            	if(mDeleteTimer == null) {
//            		mDeleteTimer = new Timer();
//            		mDeleteTimer.schedule(mDeleteTimerTask, 5*1000);
//            	}
                    break;
            }	// End of switch(cmd)
        }
    }	// End of class NotificationReceiver

    /**
     * Auto-refresh Timer
     */
//    TimerTask mTimerTask = new TimerTask(){
//    	public void run() {
//    		mHandler.post(new Runnable() {
//    			public void run() {
//    				sendNotifications();
//    			}
//    		});
//    	}
//    };
//
//    TimerTask mDeleteTimerTask = new TimerTask(){
//    	public void run() {
//    		mHandler.post(new Runnable() {
//    			public void run() {
//    				sendNotifications();
//    				mDeleteTimer = null;
//    			}
//    		});
//    	}
//    };
}
