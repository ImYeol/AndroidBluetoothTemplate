package com.example.gyl115.bluetoothprofiletest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by gyl115 on 17. 2. 24.
 */

public class BluetoothSPPClient extends AppCompatActivity {
    private String TAG="MainActivity";

    private static final int CONNECTION_SUCCESS = 1;
    private static final int CONNECTION_FAILED = 2;

    private BluetoothAdapter mBluetoothAdapter;
    private TextView mResultText;
    public final ParcelUuid oopUUID= ParcelUuid.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectThread mConnectThread;
    private BluetoothSocket clientSocket;

    private OutputStream os;

    //////////////
    public static final UUID MY_UUID =
            UUID.fromString("D04E3068-E15B-4482-8306-4CABFA1726E7");
    private JsonReader jsonReader;
    private AccecptThread mAcceptThread;
    private static final int RECEIBED_DATA = 3;
    private ConnectedThread mConnectedThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_activity);
        mResultText = (TextView) findViewById(R.id.text_result);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listen();
    }

    private void listen() {
        if( mAcceptThread != null ){
            try {
                mAcceptThread.interrupt();
                mAcceptThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        mAcceptThread = new AccecptThread();
        mAcceptThread.start();
    }

    public void onStartClick(View v) {
        //    Log.d(TAG,"onStartClick :"+mBluetoothAdapter.isEnabled());
        if (mBluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            if (devices.size() > 0) {
                BluetoothDevice dev=null;
                for (BluetoothDevice device : devices) {
                    Log.d(TAG, "bonded devices : " + device.getName());
                    connect(device);
                }
            }
        }
    }
    public void onSendData(View v){
        if(os != null){
            byte[] data= new byte[20];
            for(byte i=0; i< 20 ; i++){
                data[i]=i;
            }
            try {
                os.write(data);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onStopClick(View v){
        if(mConnectThread != null){
            mConnectThread.interrupt();
            try {
                mConnectThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mConnectThread.cancel();
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch ( msg.what){
                case CONNECTION_SUCCESS :
                    Log.d(TAG,"connection success");
                    Toast.makeText(BluetoothSPPClient.this,"connection success",Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_FAILED :
                    Log.d(TAG,"connection failed");
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case RECEIBED_DATA :
                    Log.d(TAG,"Received_data");
                    BluetoothPictureInfo picture = (BluetoothPictureInfo) msg.obj;
                    Toast.makeText(BluetoothSPPClient.this,picture.getFileName(),Toast.LENGTH_SHORT).show();

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
                clientSocket= mDevice.createRfcommSocketToServiceRecord(oopUUID.getUuid());
                Log.d(TAG,"clientSocket: "+ clientSocket);
                clientSocket.connect();
                Message msg = mHandler.obtainMessage(CONNECTION_SUCCESS);
                msg.sendToTarget();

            } catch (IOException e) {
                e.printStackTrace();
                Message msg = mHandler.obtainMessage(CONNECTION_FAILED);
                msg.sendToTarget();
                return ;
            }

            try {
                os= clientSocket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void cancel(){
            if(clientSocket.isConnected()){
                try {
                    os.close();
                    clientSocket.close();
                    os=null;
                    clientSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class AccecptThread extends Thread {

        private BluetoothServerSocket mServerSocket;

        public AccecptThread(){
            try {
                mServerSocket = BluetoothAdapter.getDefaultAdapter()
                        .listenUsingRfcommWithServiceRecord("hahaha",MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    clientSocket = mServerSocket.accept(); // blocking call
                } catch (IOException e) {
                    Log.v(TAG, e.getMessage());
                    break;
                }

                Log.d(TAG, "socket is accepted");
                // If a connection was accepted
                if (clientSocket != null) {
                    // Do work in a separate thread
                    try {
                        mConnectedThread = new ConnectedThread(clientSocket);
                        mConnectedThread.start();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG,"exception on connectThread");
                    }
                    Log.d(TAG, "connectedThread is called");
                }
            }
        }

        public void cancel(){
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private class ConnectedThread extends Thread{

        private final BluetoothSocket mSocket;
        private int bytesRead;
        private Handler h;
        private InputStream inputStream;
        private OutputStream outputStream;
        private boolean connected=false;
        private BufferedWriter writer;
        private DataInputStream in;



        public ConnectedThread(BluetoothSocket socket) throws Exception {
            //mSocket = socket;
            InputStream tmp = null;
            if (socket == null) {
                throw new InvalidParameterException("Bluetooth socket can't be null");
            }

            this.mSocket = socket;

            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                connected = true;
            } catch (IOException e) {
                throw new Exception("Can't get stream from bluetooth socket");
            } finally {
                if (!connected) {
                    // closeConnection();
                    cancel();
                }
            }
        }

        @Override
        public void run() {
            Log.d(TAG,"run Server Connected Thread");
            JsonReader reader = null;
            while(true){
                try {
                    reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.d(TAG,"unSupported encoding Exception");
                    e.printStackTrace();
                }
                try {
                //List<Message> messages = new ArrayList<Message>();
                GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
                final Gson gson = builder.create();
                BluetoothPictureInfo picture;
                picture = gson.fromJson(reader, BluetoothPictureInfo.class);
                Message message = mHandler.obtainMessage(RECEIBED_DATA);
                message.obj = picture;
                message.sendToTarget();
                Log.d(TAG,"end of run in connectedThread");
            } catch (Exception e) {
                Log.d(TAG,"error on json reader");
                e.printStackTrace();
                break;
            }
        }
            try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


}
