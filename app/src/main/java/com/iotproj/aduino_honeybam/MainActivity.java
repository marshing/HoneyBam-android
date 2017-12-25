package com.iotproj.aduino_honeybam;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
//
//    TextView tv_w;
//    Button bt_w;
//    EditText et_send;
//    Button bt_send;

    BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName = null;
    static boolean isConnectionError = false;
    ConnectedTask mConnectedTask = null;
    private final int REQUEST_BLUETOOTH_ENABLE = 100;

    ReceiveWeatherTask receiveWeatherTask;

    GPSInfo gps;
    Location location;

    TextView tv_address;
    ImageView iv_weather;
    ToggleButton tb_power, tb_timer1, tb_timer2, tb_timer3, tb_timer4, tb_timer5;
    ImageButton bt_ref;

//    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        sp = getSharedPreferences("myFile", Activity.MODE_PRIVATE);
//        final SharedPreferences.Editor editor = sp.edit();
//

        tv_address = (TextView)findViewById(R.id.tv_addr);
        iv_weather = (ImageView) findViewById(R.id.iv_weather);
        tb_power = (ToggleButton)findViewById(R.id.tb_power);
        tb_timer1 = (ToggleButton)findViewById(R.id.tb_timer1);
        tb_timer2 = (ToggleButton)findViewById(R.id.tb_timer2);
        tb_timer3 = (ToggleButton)findViewById(R.id.tb_timer3);
        tb_timer4 = (ToggleButton)findViewById(R.id.tb_timer4);
        tb_timer5 = (ToggleButton)findViewById(R.id.tb_timer5);
        bt_ref = (ImageButton) findViewById(R.id.bt_ref);

//
//         tv_w = (TextView)findViewById(R.id.tv_weather);
//         bt_w = (Button)findViewById(R.id.bt_connectW);
//
//         et_send = (EditText) findViewById(R.id.et_send);
//         bt_send = (Button)findViewById(R.id.bt_send);
//
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            showErrorDialog("This device is not implement Bluetooth");
            return;
        }
        else {
            Log.d(getClass().getName(), "Initialisation successful");
            showPairedDevicesListDialog();
        }


         gps = new GPSInfo(MainActivity.this);
         location = gps.firstSetLocation();

         Log.d(getClass().getName(), "location : (" + location.getLatitude() + "," + location.getLongitude() + ")");
         getWeatherData();

         bt_ref.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 tv_address.setText(GPSToAddress.getAddress(MainActivity.this, location.getLatitude(), location.getLongitude()));

                 String weather = receiveWeatherTask.getDescription().toLowerCase();
                 if( weather.equals("haze") ) {
                     sendMessge("purple");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.cloudy));
                 } else if(weather.equals("fog")){
                     sendMessge("purple");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.cloudy));
                 } else if(weather.equals("clouds")){
                     sendMessge("purple");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.cloudy));
                 } else if(weather.equals("few clouds")){
                     sendMessge("purple");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.cloudy));
                 } else if(weather.equals("scattered clouds")){
                     sendMessge("purple");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.cloudy));
                 } else if(weather.equals("broken clouds")){
                     sendMessge("purple");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.cloudy));
                 } else if(weather.equals( "overcast clouds")){
                     sendMessge("purple");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.cloudy));
                 } else if(weather.equals("clear sky")){
                     sendMessge("red");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.sunny));
                 } else if(weather.equals("shower rain")){
                     sendMessge("blue");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.rain));
                 } else if(weather.equals("rain")){
                    sendMessge("blue");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.rain));
                 } else if(weather.equals("thunderstorm")){
                     sendMessge("blue");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.rain));
                 } else if(weather.equals("snow")){
                    sendMessge("green");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.snow));
                 } else if(weather.equals("mist")){
                    sendMessge("purple");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.cloudy));
                 } else{
                     sendMessge("green");
                     iv_weather.setBackground(getResources().getDrawable(R.drawable.sunny));
                 }

             }
         });
        tb_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tb_power.isChecked()){
                  tb_power.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));
                    sendMessge("onon");
                }else {
                    tb_power.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));
                    sendMessge("offoff");
                }
            }
        });

         tb_timer1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
//                 editor.putString("timer", "tiemr1");
//                 editor.commit();
                 tb_timer1.setChecked(true);
                 tb_timer2.setChecked(false);
                 tb_timer3.setChecked(false);
                 tb_timer4.setChecked(false);
                 tb_timer5.setChecked(false);
                tb_timer1.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_30s));
                 tb_timer2.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_1dark));
                 tb_timer3.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_3dark));
                 tb_timer4.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_5dark));
                 tb_timer5.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_10dark));
                 sendMessge("timer1");
             }
         });
        tb_timer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                editor.putString("timer", "tiemr2");
//                editor.commit();
                tb_timer1.setChecked(false);
                tb_timer2.setChecked(true);
                tb_timer3.setChecked(false);
                tb_timer4.setChecked(false);
                tb_timer5.setChecked(false);
                tb_timer1.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_30dark));
                tb_timer2.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_1m));
                tb_timer3.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_3dark));
                tb_timer4.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_5dark));
                tb_timer5.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_10dark));
                sendMessge("timer2");
            }
        });
        tb_timer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                editor.putString("timer", "tiemr3");
//                editor.commit();
                tb_timer1.setChecked(false);
                tb_timer2.setChecked(false);
                tb_timer3.setChecked(true);
                tb_timer4.setChecked(false);
                tb_timer5.setChecked(false);
                tb_timer1.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_30dark));
                tb_timer2.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_1dark));
                tb_timer3.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_3m));
                tb_timer4.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_5dark));
                tb_timer5.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_10dark));
                sendMessge("timer3");
            }
        });
        tb_timer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                editor.putString("timer", "tiemr4");
//                editor.commit();
                tb_timer1.setChecked(false);
                tb_timer2.setChecked(false);
                tb_timer3.setChecked(false);
                tb_timer4.setChecked(true);
                tb_timer5.setChecked(false);
                tb_timer1.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_30dark));
                tb_timer2.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_1dark));
                tb_timer3.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_3dark));
                tb_timer4.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_5m));
                tb_timer5.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_10dark));
                sendMessge("timer4");
            }
        });
        tb_timer5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                editor.putString("timer", "tiemr5");
//                editor.commit();
                tb_timer1.setChecked(false);
                tb_timer2.setChecked(false);
                tb_timer3.setChecked(false);
                tb_timer4.setChecked(false);
                tb_timer5.setChecked(true);
                tb_timer1.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_30dark));
                tb_timer2.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_1dark));
                tb_timer3.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_3dark));
                tb_timer4.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_5dark));
                tb_timer5.setBackgroundDrawable(getResources().getDrawable(R.drawable.t_10m));
                sendMessge("timer5");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mConnectedTask != null){
            mConnectedTask.cancel(true);
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {
        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        ConnectTask(BluetoothDevice bluetoothDevice){
            mBluetoothDevice = bluetoothDevice;
            mConnectedDeviceName = bluetoothDevice.getName();

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            try{
                mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                Log.d(getClass().getName(), "create socket for" + mConnectedDeviceName);
            }catch (IOException e){ Log.e(getClass().getName(), "socket create failed"+e.getMessage()); }

            Toast.makeText(MainActivity.this, "connecting...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mBluetoothAdapter.cancelDiscovery();

            try{
                mBluetoothSocket.connect();
            }catch (IOException e) {
                try{
                    mBluetoothSocket.close();
                }catch (IOException e2){
                    Log.e(getClass().getName(), "unable to close()" + " socket during connection failure", e2);
                }
                return  false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                connected(mBluetoothSocket);
            }else {
                isConnectionError = true;
                Log.d(getClass().getName(), "Unable to connect device");
                showErrorDialog("Unable to connect device");
            }
        }
    }

    public void connected(BluetoothSocket socket){
        mConnectedTask = new ConnectedTask(socket);
        mConnectedTask.execute();
    }

    private class ConnectedTask extends AsyncTask<Void,String,Boolean>{

        private InputStream mInputStream = null;
        private OutputStream mOutputStream = null;
        private BluetoothSocket mBluetoothSocket = null;

        ConnectedTask(BluetoothSocket socket) {
            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(getClass().getName(), "socket not created", e);
            }

            Log.d(getClass().getName(), "connected to " + mConnectedDeviceName);
            Toast.makeText(MainActivity.this, "connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            byte[] readBuffer = new byte[1024];
            int readBufferPosition = 0;

            while(true) {
                if( isCancelled() ) return  false;
                try {

                    int bytesAvailable = mInputStream.available();

                    if(bytesAvailable > 0) {

                        byte[] packetBytes = new byte[bytesAvailable];

                        mInputStream.read(packetBytes);

                        for (int i = 0; i < bytesAvailable; i++) {
                            byte b = packetBytes[i];
                            if(b=='\n'){
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                String recvMessage = new String(encodedBytes, "UTF-8");

                                readBufferPosition = 0;

                                Log.d(getClass().getName(), "recv message: " + recvMessage);
                                publishProgress(recvMessage);
                            }
                            else
                            {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                }catch (IOException e) {
                    Log.d(getClass().getName(), "disconnected", e);
                    return false;
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(!aBoolean) {
                closeSocket();
                Log.d(getClass().getName(), "Device connection was lost");
                isConnectionError = true;
                showErrorDialog("Device connection was lost");
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);

            closeSocket();
        }

        void closeSocket(){
            try{
                mBluetoothSocket.close();
                Log.d(getClass().getName(), "close socket()");
            }catch (IOException e2){
                Log.e(getClass().getName(), "unable to close() socket during connection failure", e2);
            }
        }

        void write(String msg){
            //msg+="\n";

            try {
                mOutputStream.write(msg.getBytes());
                mOutputStream.flush();
            }catch (IOException e){
                Log.e(getClass().getName(), "Exception during send", e);
            }

//            et_send.setText("");
        }
    }



    public void showPairedDevicesListDialog(){
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        final BluetoothDevice[] pairedDevices = devices.toArray(new BluetoothDevice[0]);

        if(pairedDevices.length==0)
        {
            showQuitDialog("No devices have been paired.\nYou must pair it with another device.");
            return;
        }

        String[] items;
        items = new String[pairedDevices.length];
        for(int i=0; i<pairedDevices.length; i++){
            items[i] = pairedDevices[i].getName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select device");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ConnectTask task = new ConnectTask(pairedDevices[i]);
                task.execute();
            }
        });
        builder.create().show();
    }

    public void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if ( isConnectionError  ) {
                    isConnectionError = false;
                    finish();
                }
            }
        });
        builder.create().show();
    }

    public void showQuitDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    void sendMessge(String msg){
        if(mConnectedTask != null) {
            mConnectedTask.write(msg);
            Log.d(getClass().getName(), "send message: " + msg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_BLUETOOTH_ENABLE){
            if (resultCode == RESULT_OK){
                //BlueTooth is now Enabled
                showPairedDevicesListDialog();
            }
            if(resultCode == RESULT_CANCELED){
                showQuitDialog( "You need to enable bluetooth");
            }
        }
    }


    private void getWeatherData () {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        String APIKEY = "324777d0469753a3ed44928eac12230d";
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lng + "&units=metric&appid=" + APIKEY;
        receiveWeatherTask = new ReceiveWeatherTask();
        receiveWeatherTask.execute(url);

    }
}
