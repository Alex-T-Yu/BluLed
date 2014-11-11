package golbang.com.bluled;

import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import golbang.com.bluled.Service.BluetoothLEDService;
import golbang.com.bluled.Service.BluetoothService;
import golbang.com.bluled.Utils.Constants;

public class main_phone extends FragmentActivity {

    // Debugging
    private static final String TAG = "MAINActivity";

    // Context, System
    private Context mContext;
    private BluetoothLEDService mService;
    public ActivityHandler mActivityHandler;

    // Global
    private boolean mStopService = true;

    private FragmentTabHost mTabHost;
    private BluetoothService btService = null;

    private static final int REQUEST_CONNECT_DEVICE = 0;
    private static final int REQUEST_ENABLE_BT = 1;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTab01 tab01 = (FragmentTab01)fragmentManager.findFragmentByTag("tab01");

        switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
            Log.i("MAIN_ACTIVITY", "MESSAGE_STATE_CHANGE: " + msg.arg1);

            switch (msg.arg1){
                case BluetoothService.STATE_CONNECTED:
                    tab01.onBtConnect();
                    break;

                case BluetoothService.STATE_NONE:
                    tab01.onBtDisconnect();
                    break;

                default:
                    break;
                }

            default:

            break;
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //----- System, Context
        mContext = getApplicationContext();
        mActivityHandler = new ActivityHandler();
        mService = new BluetoothLEDService();

        setContentView(R.layout.activity_main_phone);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab01").setIndicator("GENERAL\nCONFIG"),FragmentTab01.class,null);
        mTabHost.addTab(mTabHost.newTabSpec("tab02").setIndicator("COLOR\nCONFIG"),FragmentTab02.class,null);
        mTabHost.addTab(mTabHost.newTabSpec("tab03").setIndicator("ABOUT"),FragmentSupport.class,null);

        // Do data initialization after service binding complete
        doBindService();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_phone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeActivity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();		// TODO: Disable this line to run below code
        // TODO: Prevent app termination.
    }

    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Service connected");

            mService = ((BluetoothLEDService.BluetoothLEDServiceBinder) binder).getService();
            mService.setActivityHandler(mActivityHandler);

            // Activity couldn't work with mService until connections are made
            // So initialize parameters and settings here, not while running onCreate()
            initialize();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService.setActivityHandler(null);
            mService = null;
        }
    };

    private void doBindService() {
        bindService(new Intent(this, BluetoothLEDService.class), mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private void doStopService() {
        mService.finalizeService();
        stopService(new Intent(this, BluetoothLEDService.class));
        unbindService(mServiceConn);
    }

    /**
     * Initialization / Finalization
     */
    private void initialize() {
        // If BT is not on, request that it be enabled.
        // RetroWatchService.setupBT() will then be called during onActivityResult
        if(!mService.isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }
        // Otherwise, setup the bluetooth session.
        else {
            mService.setupBT();
        }
    }

    private void finalizeActivity() {
        if(mStopService)
            doStopService();

        //RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
    }

    /**
     * Launch the DeviceListActivity to see devices and do scan
     */
    private void doScan() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);
    }

    /**
     * Launch notification settings screen
     */
    private void setNotificationAccess() {
        Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }

    /**
     * Ensure this device is discoverable by others
     */
    private void ensureDiscoverable() {
        if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessageToRemote(String message) {
        if(mService != null && message != null && message.length() > 0)
            mService.sendMessageToRemote(message);
    }

    /**
     * Sends a message.
     * @param buf  Buffer to send.
     */
    private void sendMessageToRemote(byte[] buf) {
        if(mService != null && buf != null && buf.length > 0)
            mService.sendMessageToRemote(buf);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//        switch (requestCode) {
//            case REQUEST_CONNECT_DEVICE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    btService.getDeviceInfo(data);
//                }
//                break;
//            case REQUEST_ENABLE_BT:
//                // When the request to enable Bluetooth returns
//                if (resultCode == Activity.RESULT_OK) {
//                    // 확인 눌렀을 때
//                    //Next Step
//                } else {
//                    // 취소 눌렀을 때
////                    Log.d(TAG, "Bluetooth is not enabled");
//                }
//                break;
//        }

        Log.d(TAG, "onActivityResult " + resultCode);

        switch(requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Attempt to connect to the device
                    if(address != null && mService != null)
                        mService.connectDevice(address);
                }
                break;

            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a BT session
                    mService.setupBT();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.e(TAG, "BT is not enabled");
                    //Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    mStopService = true;
                }
                break;
        }	// End of switch(requestCode)

    }

    public void setBt(){
        scanDevice();
    }

    public void setBtDisconnect(){
        btService.stop();
    }

    public void scanDevice(){
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void setLed(boolean status){
        String sendCode;

        if(mService == null){
            Toast.makeText(this,"블루투스가 연결되지 않았습니다.",Toast.LENGTH_SHORT).show();
        }else{
            if(status) {
                sendCode = "ON_WHITE";

            }else{
                sendCode = "OFF";
            }

            mService.sendMessageToRemote(sendCode);
        }
    }

    public void setLedColor(String rgbCode) {

//        if(btService == null){
//            Toast.makeText(this,"블루투스가 연결되지 않았습니다.",Toast.LENGTH_SHORT).show();
//        }else{
//            String sendCode = "RGB_" + rgbCode;
//            btService.write(sendCode.getBytes());
//        }
        if(mService == null){
            Toast.makeText(this,"블루투스가 연결되지 않았습니다.",Toast.LENGTH_SHORT).show();
        }else{
            String sendCode = "RGB_" + rgbCode;
            mService.sendMessageToRemote(sendCode);
        }

    }

    public void getActiveNoti() {
        NotificationManager notificationManager = (NotificationManager) this.getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }	// End of class ActivityHandler



}
