package com.example.gyl115.bluetoothprofiletest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.rxbluetooth.BluetoothConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.Set;
import java.util.UUID;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by gyl115 on 17. 2. 24.
 */

public class BluetoothSPPServer extends AppCompatActivity {
    private String TAG="BTServer";

    private static final int CONNECTION_SUCCESS = 1;
    private static final int CONNECTION_FAILED = 2;
    private static final int RECEIBED_DATA=3;

    private BluetoothAdapter mBluetoothAdapter;
    private TextView mResultText;
    public final ParcelUuid SDP_UUID= ParcelUuid.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectThread;
    private AccecptThread mAcceptThread;
    private BluetoothSocket clientSocket;

    private BluetoothConnection connection;
    private ImageView imageView;

    //////////////////////////
    private ConnectThread mThread;
    private OutputStream os;
    private BluetoothSocket mySocket;
    public static final UUID MY_UUID =
            UUID.fromString("D04E3068-E15B-4482-8306-4CABFA1726E7");
    private JsonWriter jsonWriter;
    private static final int CLIENT_CONNECT_SUCCESS=4;
    private boolean isClientConnected=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity);
        mResultText = (TextView) findViewById(R.id.text_result_server);
        imageView = (ImageView) findViewById(R.id.imageView);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void onStartClick(View v) {
        //    Log.d(TAG,"onStartClick :"+mBluetoothAdapter.isEnabled());
        if (mBluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            if (devices.size() > 0) {
                for (BluetoothDevice device : devices) {
                    Log.d(TAG, "bonded devices : " + device.getName());

                }
                listen();
            }
        }
    }

    public void onStartClient(View v){
        if (mBluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            if (devices.size() > 0) {
                BluetoothDevice dev=null;
                for (BluetoothDevice device : devices) {
                    Log.d(TAG, "bonded devices : " + device.getName());
                    dev = device;
                }
                connect(dev);
            }
        } else {
            Log.d(TAG,"bluetoothAdapter is not enabled");
        }
        isClientConnected=true;
    }

    @Override
    protected void onDestroy() {
        if(mConnectThread !=null){
            mConnectThread.interrupt();
            mConnectThread =null;
        }
        if(mAcceptThread != null){
            mAcceptThread.interrupt();
            mAcceptThread = null;
        }
        super.onDestroy();
    }

    public void onStopClick(View v){
        if(mConnectThread.isAlive()){
            try {
                mConnectThread.interrupt();
                mConnectThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mConnectThread.cancel();
        }

        if(mAcceptThread.isAlive()){
            try {
                mAcceptThread.interrupt();
                mAcceptThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mAcceptThread.cancel();
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
                case RECEIBED_DATA :
                    Log.d(TAG,"Received_data");
                    BluetoothPictureInfo picture = (BluetoothPictureInfo) msg.obj;
                    mResultText.setText(picture.getFileName());
                    byte[] image = Base64.decode(picture.getRawImageData(),Base64.DEFAULT);
                    Glide.with(BluetoothSPPServer.this).load(image).into(imageView);
                    break;
                case CLIENT_CONNECT_SUCCESS :
                    Log.d(TAG,"Client socket connection success");
                    isClientConnected= true;
                    break;
            }
        }
    };

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

    private class AccecptThread extends Thread {

        private BluetoothServerSocket mServerSocket;

        public AccecptThread(){
            try {
                mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("test",SDP_UUID.getUuid());
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
                        mConnectThread = new ConnectedThread(clientSocket);
                        mConnectThread.start();
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
            /*try {

                connection = new BluetoothConnection(socket);
                //tmp = socket.getInputStream();
                //	in=new DataInputStream(tmp);
                // reader = new BufferedReader(new
                // InputStreamReader(socket.getInputStream()));
                // writer = new BufferedWriter(new
                // OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"failed to getInputStream");
            } catch (Exception e){
                e.printStackTrace();
            }*/
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
                //List<Message> messages = new ArrayList<Message>();
                GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
                final Gson gson = builder.create();
                BluetoothPictureInfo picture=null;
                try {
                    //reader.beginObject();
                    // while (reader.hasNext()) {
                    picture = gson.fromJson(reader, BluetoothPictureInfo.class);
                    //}
                    //reader.endObject();
                    Log.d(TAG,"success to get data : "+ picture.getFileName() );
                    Log.d(TAG,"image data: "+ picture.getRawImageData().substring(0,100));

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


            /*connection.observeStringStream().observeOn(Schedulers.immediate())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<String>() {
                        @Override public void call(String string) {
                            // This will be called every string received
                            Log.d(TAG,"String received !!!!!!!!!!!!");
                            GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
                            final Gson gson = builder.create();
                            BluetoothPictureInfo data = gson.fromJson(string, BluetoothPictureInfo.class);
                            Log.d(TAG,data.getFileName());
                            Log.d(TAG,"thread: " + Thread.currentThread());
                            byte[] picture = Base64.decode(data.getRawImageData(),Base64.DEFAULT);
                            Glide.with(BluetoothSPPServer.this).load(picture).into(imageView);
                        }
                    }, new Action1<Throwable>() {
                        @Override public void call(Throwable throwable) {
                            // Error occured
                            Log.e(TAG,"error");
                        }
                    });*/
           /* byte[] buffer=new byte[100];
            try {
                while((bytesRead=reader.read(buffer)) > 0)
                {
                    Log.d(TAG, "data:"+ new String(buffer,bytesRead));
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        }

        public void cancel(){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void connect(BluetoothDevice device) {
        if( mThread != null ){
            try {
                mThread.interrupt();
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        mThread = new ConnectThread(device,mHandler);
        mThread.start();
    }

    public void onSendData(View v) {
        if(!isClientConnected){
            return ;
        }
        Log.d(TAG,"onSendData clicked");
        try {
            jsonWriter = new JsonWriter(new OutputStreamWriter(os,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG,"unSupportedEncodingException");
            e.printStackTrace();
        }
        BluetoothPictureInfo picture = new BluetoothPictureInfo();
        picture.setFileName("hahaha");
        picture.setRawImageData("hohoho");
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();
        gson.toJson(picture,BluetoothPictureInfo.class,jsonWriter);
        try {
            jsonWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                mySocket= mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                Log.d(TAG,"mySocket: "+ mySocket.getRemoteDevice().getName());
                mySocket.connect();
                Message msg = mHandler.obtainMessage(CLIENT_CONNECT_SUCCESS);
                msg.sendToTarget();

            } catch (IOException e) {
                e.printStackTrace();
                Message msg = mHandler.obtainMessage(CONNECTION_FAILED);
                msg.sendToTarget();
                return ;
            }

            try {
                os= mySocket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void cancel(){
            if(mySocket.isConnected()){
                try {
                    os.close();
                    mySocket.close();
                    os=null;
                    mySocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
