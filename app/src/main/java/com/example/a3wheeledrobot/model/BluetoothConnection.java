package com.example.a3wheeledrobot.model;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.Toast;


import com.example.a3wheeledrobot.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnection {
    private static final String TAG = "BluetoothConnectionTag";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static ArrayList deviceStrs = new ArrayList();
    private static ArrayList devices = new ArrayList();
    public static String DeviceAddress;
    public static BluetoothDevice device;
    static BluetoothSocket sock;
    static BluetoothSocket sockFallback;
    private static Boolean  isConnected = false ;

    public static BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public static Boolean getIsConnected() {
        return isConnected;
    }

    public static ArrayList getDeviceStrs() {
        return deviceStrs;
    }

    public static ArrayList getDevices() {
        return devices;
    }

    public static void getAllDeviceAddress() {
        devices.clear();
        deviceStrs.clear();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }

        }

    }

    public static void startConnection() throws IOException {

        isConnected = false;
        String remoteDevice = DeviceAddress;
        Log.d(TAG,"startConnection:DeviceAddress:" + remoteDevice);
        if( remoteDevice == null || "".equals(remoteDevice)){
            Log.d ( TAG,"No Bluetooth device has been selected " ) ;

        }else{
            device = bluetoothAdapter.getRemoteDevice(remoteDevice);
            Log.d ( TAG , "startConnection:Stopping Bluetooth discovery");
            bluetoothAdapter.cancelDiscovery();

        try{
            Log.d(TAG,"startConnection:befor sock connect");
            // Instantiate a BluetoothSocket for the remote
            // device and connect it.
            sock = device.createRfcommSocketToServiceRecord(MY_UUID);
            sock.connect();
            isConnected = true;
        } catch (Exception e) {
          /* Log.d("startConnection", "There was an error" + ",Falling back..", e);
            Class<?> clazz = sock.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {

                /************Fallback method 1*********************/
             /*   Method m = clazz.getMethod(
                        "createRfcommSocket"
                        , paramTypes
                );
                Object[] params = new Object[]{Integer.valueOf(1)};
                sockFallback = (BluetoothSocket) m.invoke(
                        sock.getRemoteDevice()
                        , params
                );
                sockFallback.connect();
                sock = sockFallback;

                Log.d("", "Connected");

            } catch (Exception e2) {
                Log.d("startConnection", "Stopping app..", e2);
                throw new IOException();
            }*/
            isConnected=false;
        }

        }
        if( isConnected){
            Log.d(TAG, "connected");
            write("connection established");
        }
        else{
            Log.d(TAG, "NOT connected");
        }



    }

    public static void write(String data)throws IOException{


        sock.getOutputStream().write(data.getBytes());
    }
    public static String makeFrame(int[] taken ,int...args){
        String res = "$" ;

        for (int i = 0  ; i<taken.length;i++){
            if ( taken[i] != -1){
                res+= args[taken[i]]+ "$";
            }else
                res+= "#$";

        }
        res+="\n";
        return res;
    }

    public static String readRawData(InputStream in, char c) throws IOException {
        byte b = 0;
        StringBuilder res = new StringBuilder();
        Log.d(TAG, "readRawData: before while true");

        while (true) {
            b = (byte) in.read();
            if (b == -1) break;                 // reach the end of the stream

            if ((char) b == c) break;
            res.append((char) b);
            Log.d(TAG, "readRawData: inside loop " + res);
        }
        return res.toString();
    }


}
