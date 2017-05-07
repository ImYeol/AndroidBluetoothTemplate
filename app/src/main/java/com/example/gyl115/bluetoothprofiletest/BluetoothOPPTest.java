package com.example.gyl115.bluetoothprofiletest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by gyl115 on 17. 2. 24.
 */

public class BluetoothOPPTest extends AppCompatActivity {
    private String TAG="MainActivity";

    private static final int CONNECTION_SUCCESS = 1;
    private static final int CONNECTION_FAILED = 2;

    private BluetoothAdapter mBluetoothAdapter;
    private TextView mResultText;
    public final ParcelUuid oopUUID= ParcelUuid.fromString("00001105-0000-1000-8000-00805f9b34fb");
    private Thread mConnectThread;
    private BluetoothSocket clientSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResultText = (TextView) findViewById(R.id.text_result);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void onStartClick(View v) {
        //    Log.d(TAG,"onStartClick :"+mBluetoothAdapter.isEnabled());
        if (mBluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            if (devices.size() > 0) {
                for (BluetoothDevice device : devices) {
                    Log.d(TAG, "bonded devices : " + device.getName() + " uuid: " + device.getUuids());
                    connect(device);
                }
            }
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch ( msg.what){
                case CONNECTION_SUCCESS :
                    Log.d(TAG,"connection success");
                    break;
                case CONNECTION_FAILED :
                    Log.d(TAG,"connection failed");
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void connect(BluetoothDevice device) {
        if( mConnectThread != null ){
            try {
                mConnectThread.interrupt();
                mConnectThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        mConnectThread = new ConnectThread(device, mHandler);
        mConnectThread.start();
    }

    private class ConnectThread extends Thread{

        private BluetoothDevice mDevice;
        private Handler StateHandler;

        public ConnectThread(BluetoothDevice device,Handler handler){
            mDevice = device;
            StateHandler = handler;
        }

        @Override
        public void run() {
            try {
                clientSocket= mDevice.createInsecureRfcommSocketToServiceRecord(oopUUID.getUuid());
                Log.d(TAG,"clientSocket: "+ clientSocket);
                clientSocket.connect();
                Message msg = mHandler.obtainMessage(CONNECTION_SUCCESS);
                msg.sendToTarget();
                clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
                Message msg = mHandler.obtainMessage(CONNECTION_FAILED);
                msg.sendToTarget();
                return ;
            }
        }
    }


}
