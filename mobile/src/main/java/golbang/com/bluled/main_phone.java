package golbang.com.bluled;

import android.app.Activity;
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

import golbang.com.bluled.Service.BluLedService;
import golbang.com.bluled.Utils.Constants;

public class main_phone extends FragmentActivity {

    private static final String TAG = "MAINActivity";

    private Context mContext;
    private ActivityHandler mActivityHandler;
    private BluLedService mService;

    private boolean mStopService = true;
    private boolean mBtConnect = false;
    private boolean mLedFlag = false;

    private FragmentTabHost mTabHost;

    private FragmentManager fragmentManager;
    private FragmentTab01 tab01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //----- System, Context
        mContext = getApplicationContext();
        mActivityHandler = new ActivityHandler();

        setContentView(R.layout.activity_main_phone);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab01").setIndicator("GENERAL\nCONFIG"),FragmentTab01.class,null);
        mTabHost.addTab(mTabHost.newTabSpec("tab02").setIndicator("COLOR\nCONFIG"),FragmentTab02.class,null);
        mTabHost.addTab(mTabHost.newTabSpec("tab03").setIndicator("ABOUT"),FragmentSupport.class,null);

        // Do data initialization after service binding complete

        doStartService();

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Activity - Service connected");

            mService = ((BluLedService.BTServiceBinder) binder).getService();

            initialize();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private void doStartService() {
        Log.d(TAG, "# Activity - doStartService()");
        startService(new Intent(this, BluLedService.class));
        bindService(new Intent(this, BluLedService.class), mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private void doStopService() {
        Log.d(TAG, "# Activity - doStopService()");
        mService.finalizeService();
        stopService(new Intent(this, BluLedService.class));
    }

    private void initialize() {
        Log.d(TAG, "# Activity - initialize()");
        mService.setupService(mActivityHandler);

        // If BT is not on, request that it be enabled.
        // BTService.setupBT() will then be called during onActivityResult
        if(!mService.isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }
    }

    private void finalizeActivity() {
        Log.d(TAG, "# Activity - finalizeActivity()");

        if(mStopService)
            doStopService();

        unbindService(mServiceConn);
//        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
    }

    /**
     * Launch the DeviceListActivity to see devices and do scan
     */
    public void doScan() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);
    }

    /**
     * Ensure this device is discoverable by others
     */
    public void ensureDiscoverable() {
        if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }

    public void disconnectBt(){
        mService.disconnectDevice();
    }

    /**
     * Call this method to send messages to remote
     */
    private void sendMessageToRemote(String message) {
        mService.sendMessageToRemote(message);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult / resultCode " + resultCode);
        Log.d(TAG, "onActivityResult / requestCode " + requestCode);

        switch(requestCode) {

            case Constants.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    Log.d(TAG , "address : " + address);

                    // Attempt to connect to the device
                    if(address != null && mService != null) {
                        Log.d(TAG,"connect!");
                        mService.connectDevice(address);
                    }
                }else{
                    Log.d(TAG,"REQUEST_CONNECT_NONE");
                    Toast.makeText(this,R.string.devNotFound,Toast.LENGTH_SHORT).show();
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
                }
                break;
        }	// End of switch(requestCode)
    }

    public void setTabUI(int code){

        fragmentManager = getSupportFragmentManager();
        tab01 = (FragmentTab01)fragmentManager.findFragmentByTag("tab01");

        if(code == Constants.UI_MESSAGE_CONNECT) {
            tab01.onBtConnect();
        }else if(code == Constants.UI_MESSAGE_DISCONNECT){
            mBtConnect = false;
            tab01.onBtDisconnect();
        }else{

        }
    }

    public boolean getBtConnect(){
        return mBtConnect;
    }

    public void setLedFlag(boolean flag){
        mLedFlag = flag;
    }

    public boolean getLedFlag(){
        return mLedFlag;
    }

    public class ActivityHandler extends Handler {

        @Override
        public void handleMessage(Message msg)
        {
            Log.d(TAG, String.valueOf(msg.what));
            switch(msg.what) {
                // BT state message
                case Constants.MESSAGE_BT_STATE_INITIALIZED:
                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:
                    setTabUI(Constants.UI_MESSAGE_DISCONNECT);
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED:
                    if(mService != null) {
                        String deviceName = mService.getDeviceName();
                        if(deviceName != null) {
                           setTabUI(Constants.UI_MESSAGE_CONNECT);
                            mBtConnect = true;
                        }
                    }
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:
                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }

    public void postMessage(String msgKey){

        this.sendMessageToRemote(msgKey);

    }

    public void postLedkey(int msgKey){

       switch (msgKey){
           case Constants.POST_MASSAGE_LED_ON:
               postMessage("ON_LED");
               break;

           case Constants.POST_MASSAGE_LED_OFF:
               postMessage("OFF");
               break;

           case Constants.POST_MESSAGE_LED_RGB:
               break;

           default:
               break;

       }

    }


}
