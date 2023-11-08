package com.example.a3wheeledrobot;

import androidx.appcompat.app.AppCompatActivity;
import com.example.a3wheeledrobot.model.BluetoothConnection;
import com.example.a3wheeledrobot.model.MoveClass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener {
    Toolbar toolbar ;
    public double distR=-1 ;
    public double distr =-1;
    private AlertDialog.Builder alertdialogbuilder ;
    private AlertDialog confDialog;
    private static boolean configuration3w =false ;
    private static boolean configuration4w =false ;
    private static boolean configuration2w=false ;
    private static boolean configured = false ;
    private double Rdef = 1;
    private double rdef = 1 ;
    private String ports[] = {"port 1","port 2","port 3","port 4","port 5","port 6","port 7"};
    private int taken[]={-1 , -1 , -1 , -1, -1, -1, -1};


    public static boolean isConfigured() {
        return configured;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuitems, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_Bluetooth: {
                if ( !BluetoothConnection.getBluetoothAdapter().isEnabled()){
                    Toast.makeText(this.getApplicationContext(), "bluetooth is not enabled", Toast.LENGTH_SHORT).show();
                }
                else showDeviceSelecterDialog();
                return true;
            }
            //startActivity(new Intent(this, Bluetooth.class));
            case R.id.action_settings: {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                return true;
            }
            case R.id.robot_3w_config:{
                show3WRobotConfDialog();
                return true;
            }
            case R.id.robot_4w_config:{
                show4WRobotConfDialog();
                return true;
            }
            case R.id.robot_2w_config:{
               show2WRobotConfDialog();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public  void showDeviceSelecterDialog(){
        BluetoothConnection.getAllDeviceAddress();

        ArrayList deviceStrs = BluetoothConnection.getDeviceStrs();
        ArrayList devices = BluetoothConnection.getDevices();

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter =
                new ArrayAdapter(this,
                        android.R.layout.select_dialog_singlechoice,
                        deviceStrs.toArray(new String[deviceStrs.size()]));
        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int position = ((AlertDialog) dialog)
                        .getListView()
                        .getCheckedItemPosition();
               BluetoothConnection.DeviceAddress = (String) devices.get(position);
                try{
                    BluetoothConnection.startConnection();
                    if(BluetoothConnection.getIsConnected())
                        Toast.makeText(MainActivity.this, "Device Connected", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e ) {
                    Log.d("InStartConnection", "onclick : after start connection  : " + e.toString());
                }

            }
        });

        alertDialog.setTitle("choose Bluetooth device");
        alertDialog.show();

    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent,float wPercent, int sourse) {
        switch (sourse) {
            case R.id.move_joystick: {
                if (BluetoothConnection.getIsConnected()) {
                    if (configuration3w) {

                            double res[] = MoveClass.motorVal3w(xPercent, yPercent, 0, distR, distr);
                            int ans[] = solSaturation(res);
                            String Frame = BluetoothConnection.makeFrame(taken, ans);
                        Log.d("FRAME3W","3 wheel MOVE frame :"+Frame);
                        try {

                            BluetoothConnection.write(Frame);
                        } catch (Exception e) {}

                    }
                    if (configuration4w){
                        double res[] = MoveClass.motorVal4w(xPercent, yPercent, 0, distR, distr);
                        int ans[] = solSaturation(res);
                        String Frame = BluetoothConnection.makeFrame(taken,ans);
                        Log.d("FRAME4W","4 wheel MOVE frame :"+Frame);
                        try {

                            BluetoothConnection.write(Frame);
                        } catch (Exception e) {}

                    }
                    if (configuration2w) {
                        double res[] = MoveClass.motorVal2w(xPercent,yPercent,distR,distr);
                        int ans[] = solSaturation(res);
                        String Frame = BluetoothConnection.makeFrame(taken,ans);
                        Log.d("FRAME2W","2 wheel MOVE frame :"+Frame);

                        try{
                            BluetoothConnection.write(Frame);
                        }catch (Exception e){}
                    }
                }

                Log.d("MainMethod", "move JoystickX percent " + xPercent + " Y percent " + yPercent);
                break;
        }
            case R.id.rotate_joyStick: {
            if (BluetoothConnection.getIsConnected()) {
                if (configuration3w) {

                    double res[] = MoveClass.motorVal3w(0, 0, wPercent, distR, distr);
                    int ans[] = solSaturation(res);
                    String Frame = BluetoothConnection.makeFrame(taken, ans);
                    Log.d("FRAME3W","3 wheel Rotate frame :"+Frame);
                    try {
                        BluetoothConnection.write(Frame);
                    } catch (Exception e) {}
                }
                if (configuration4w){
                    double res[] = MoveClass.motorVal4w(0, 0, wPercent, distR, distr);
                    int ans[] = solSaturation(res);
                    String Frame = BluetoothConnection.makeFrame(taken, ans);
                    Log.d("FRAME4W","4 wheel Rotate frame :"+Frame);
                    try {

                        BluetoothConnection.write(Frame);
                    } catch (Exception e) {}

                }
                if (configuration2w) {
                }
            }
            Log.d("MainMethod", "Rotate Joystick -> X percent " + xPercent + " Y percent " + yPercent);
            break;
            }
            case 1000 :{
                if (BluetoothConnection.getIsConnected()) {
                    if (configuration3w) {

                        double res[] = MoveClass.motorVal3w(xPercent, yPercent,wPercent, distR, distr);
                        int ans[] = solSaturation(res);
                        String Frame = BluetoothConnection.makeFrame(taken, ans);
                        Log.d("FRAME3W","3 wheel Both frame :"+Frame);
                        try {

                            BluetoothConnection.write(Frame);
                        } catch (Exception e) {}

                    }
                    if (configuration4w){
                        double res[] = MoveClass.motorVal4w(xPercent, yPercent, wPercent, distR, distr);
                        int ans[] = solSaturation(res);
                        String Frame = BluetoothConnection.makeFrame(taken,ans);
                        Log.d("FRAME4W","4 wheel Both frame :"+Frame);
                        try {

                            BluetoothConnection.write(Frame);
                        } catch (Exception e) {}

                    }

                }

                Log.d("MainMethod", "move JoystickX percent " + xPercent + " Y percent " + yPercent);
                break;

            }

        }
    }

    public  void show3WRobotConfDialog(){
        configuration2w=false ;
    configured = true ;
    configuration3w = true ;
    configuration4w = false ;
        // show popup
        alertdialogbuilder = new AlertDialog.Builder(this);

        View popup = getLayoutInflater().inflate(R.layout.popup3,null);
        EditText RtextBox = popup.findViewById(R.id.R_val);
        EditText rtextBox = popup.findViewById(R.id.r_val);
        Spinner spinner_Motor_1 = (Spinner) popup.findViewById(R.id.spinner_port1);
        Spinner spinner_Motor_2 = (Spinner) popup.findViewById(R.id.spinner_port2);
        Spinner spinner_Motor_3 = (Spinner) popup.findViewById(R.id.spinner_port3);
        ArrayAdapter<String> adapter =new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,ports);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Motor_1.setAdapter(adapter);
        spinner_Motor_1.setSelection(0);
        spinner_Motor_2.setAdapter(adapter);
        spinner_Motor_2.setSelection(1);
        spinner_Motor_3.setAdapter(adapter);
        spinner_Motor_3.setSelection(2);
            // add  the default configuration.
        setTaken3w(spinner_Motor_1.getSelectedItemPosition(),
                spinner_Motor_2.getSelectedItemPosition(),
                spinner_Motor_3.getSelectedItemPosition());
        setDistr();
        setDistR();

        spinner_Motor_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int m2=spinner_Motor_2.getSelectedItemPosition();
                int m3=spinner_Motor_3.getSelectedItemPosition();
                if (position==m2){
                    if ((position+1)%7 == m3 ) spinner_Motor_2.setSelection((position+2)%7);
                    else spinner_Motor_2.setSelection((position+1)%7);

                }
                if (position==m3){
                    if ((position+1)%7 == m2 ) spinner_Motor_3.setSelection((position+2)%7);
                    else spinner_Motor_3.setSelection((position+1)%7);

                }
                setTaken3w(spinner_Motor_1.getSelectedItemPosition(),
                        spinner_Motor_2.getSelectedItemPosition(),
                        spinner_Motor_3.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_Motor_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int m1=spinner_Motor_1.getSelectedItemPosition();
                int m3=spinner_Motor_3.getSelectedItemPosition();
                if (position==m1){
                    if ((position+1)%7 == m3 ) spinner_Motor_1.setSelection((position+2)%7);
                    else spinner_Motor_1.setSelection((position+1)%7);
                }
                if (position==m3){
                    if ((position+1)%7 == m1 ) spinner_Motor_3.setSelection((position+2)%7);
                    else spinner_Motor_3.setSelection((position+1)%7);
                }

                setTaken3w(spinner_Motor_1.getSelectedItemPosition(),
                        spinner_Motor_2.getSelectedItemPosition(),
                        spinner_Motor_3.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_Motor_3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int m2=spinner_Motor_2.getSelectedItemPosition();
                int m1=spinner_Motor_1.getSelectedItemPosition();
                if (position==m2){
                    if ((position+1)%7 == m1 ) spinner_Motor_2.setSelection((position+2)%7);
                    else spinner_Motor_2.setSelection((position+1)%7);
                }
                if (position==m1){
                    if ((position+1)%7 == m2 ) spinner_Motor_1.setSelection((position+2)%7);
                    else spinner_Motor_1.setSelection((position+1)%7);
                }

                setTaken3w(spinner_Motor_1.getSelectedItemPosition(),
                        spinner_Motor_2.getSelectedItemPosition(),
                        spinner_Motor_3.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button save = popup.findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btn_save","hello");
                Editable R=RtextBox.getText() ;
                Editable r = rtextBox.getText();
                if (R.toString().isEmpty() && r.toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Default radius for robot and wheel", Toast.LENGTH_SHORT).show();
                    setDistr();
                    setDistR();
                }
                else {
                    setDistR(R.toString());
                    setDistr(r.toString());
                }
                confDialog.dismiss();
                Toast.makeText(MainActivity.this, "Three wheel Robot configuration selected", Toast.LENGTH_LONG).show();
            }
        });
        alertdialogbuilder.setView(popup);
        confDialog = alertdialogbuilder.create();
        confDialog.show ();
    }

    public void show2WRobotConfDialog(){
        configured = true ;
        configuration3w = false ;
        configuration4w = false ;
        configuration2w= true ;
        // show popup
        alertdialogbuilder = new AlertDialog.Builder(this);

        View popup = getLayoutInflater().inflate(R.layout.popup2,null);
        EditText RtextBox = popup.findViewById(R.id.R_val);
        EditText rtextBox = popup.findViewById(R.id.r_val);
        Spinner spinner_Motor_1 = (Spinner) popup.findViewById(R.id.spinner_port1);
        Spinner spinner_Motor_2 = (Spinner) popup.findViewById(R.id.spinner_port2);
        ArrayAdapter<String> adapter =new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,ports);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Motor_1.setAdapter(adapter);
        spinner_Motor_1.setSelection(0);
        spinner_Motor_2.setAdapter(adapter);
        spinner_Motor_2.setSelection(1);

        // add  the default configuration.
        setTaken2w(spinner_Motor_1.getSelectedItemPosition(),
                spinner_Motor_2.getSelectedItemPosition());
        setDistr();
        setDistR();

        spinner_Motor_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int m2=spinner_Motor_2.getSelectedItemPosition();
                if (position==m2){
                    spinner_Motor_2.setSelection((position+1)%7);
                }

                setTaken2w(spinner_Motor_1.getSelectedItemPosition(),
                        spinner_Motor_2.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_Motor_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int m1=spinner_Motor_1.getSelectedItemPosition();
                if (position==m1){
                    spinner_Motor_1.setSelection((position+1)%7);
                }

                setTaken2w(spinner_Motor_1.getSelectedItemPosition(),
                        spinner_Motor_2.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button save = popup.findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btn_save","hello");
                Editable R=RtextBox.getText() ;
                Editable r = rtextBox.getText();
                if (R.toString().isEmpty() && r.toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Default radius for robot and wheel", Toast.LENGTH_SHORT).show();
                    setDistr();
                    setDistR();
                }
                else {
                    setDistR(R.toString());
                    setDistr(r.toString());
                }
                confDialog.dismiss();
                Toast.makeText(MainActivity.this, "Two wheel Robot configuration selected", Toast.LENGTH_LONG).show();
            }
        });
        alertdialogbuilder.setView(popup);
        confDialog = alertdialogbuilder.create();
        confDialog.show ();
    }

    public  void show4WRobotConfDialog(){
        configuration2w=false ;
        configured = true ;
        configuration4w=true ;
        configuration3w=false ;

        alertdialogbuilder = new AlertDialog.Builder(this);
        View popup = getLayoutInflater().inflate(R.layout.popup4,null);
        //declare the ui components
        EditText RtextBox = popup.findViewById(R.id.R_val);
        EditText rtextBox = popup.findViewById(R.id.r_val);
        Spinner spinner_Motor_1 = (Spinner) popup.findViewById(R.id.spinner_port1);
        Spinner spinner_Motor_2 = (Spinner) popup.findViewById(R.id.spinner_port2);
        Spinner spinner_Motor_3 = (Spinner) popup.findViewById(R.id.spinner_port3);
        Spinner spinner_Motor_4 = (Spinner) popup.findViewById(R.id.spinner_port4);
        ArrayAdapter<String> adapter =new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,ports);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Motor_1.setAdapter(adapter);
        spinner_Motor_1.setSelection(0);
        spinner_Motor_2.setAdapter(adapter);
        spinner_Motor_2.setSelection(1);
        spinner_Motor_3.setAdapter(adapter);
        spinner_Motor_3.setSelection(2);
        spinner_Motor_4.setAdapter(adapter);
        spinner_Motor_4.setSelection(3);

        // add  the default configuration.
        setTaken4w(spinner_Motor_1.getSelectedItemPosition(),
                spinner_Motor_2.getSelectedItemPosition(),
                spinner_Motor_3.getSelectedItemPosition(),
                spinner_Motor_4.getSelectedItemPosition());
        setDistr();
        setDistR();

        spinner_Motor_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int m2=spinner_Motor_2.getSelectedItemPosition();
                int m3=spinner_Motor_3.getSelectedItemPosition();
                int m4=spinner_Motor_4.getSelectedItemPosition();
                if (position==m2){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_2.setSelection(i);


                }
                if (position==m3){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_3.setSelection(i);



                }
                if (position==m4){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_4.setSelection(i);

                }
                setTaken4w(spinner_Motor_1.getSelectedItemPosition(),
                        spinner_Motor_2.getSelectedItemPosition(),
                        spinner_Motor_3.getSelectedItemPosition(),
                        spinner_Motor_4.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_Motor_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int m1=spinner_Motor_1.getSelectedItemPosition();
                int m3=spinner_Motor_3.getSelectedItemPosition();
                int m4=spinner_Motor_4.getSelectedItemPosition();
                if (position==m1){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_1.setSelection(i);


                }
                if (position==m3){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_3.setSelection(i);



                }
                if (position==m4){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_4.setSelection(i);

                }
                setTaken4w(spinner_Motor_1.getSelectedItemPosition(),
                        spinner_Motor_2.getSelectedItemPosition(),
                        spinner_Motor_3.getSelectedItemPosition(),
                        spinner_Motor_4.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_Motor_3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int m2=spinner_Motor_2.getSelectedItemPosition();
                int m1=spinner_Motor_1.getSelectedItemPosition();
                int m4=spinner_Motor_4.getSelectedItemPosition();
                if (position==m2){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_2.setSelection(i);


                }
                if (position==m1){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_1.setSelection(i);



                }
                if (position==m4){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_4.setSelection(i);

                }
                setTaken4w(spinner_Motor_1.getSelectedItemPosition(),
                        spinner_Motor_2.getSelectedItemPosition(),
                        spinner_Motor_3.getSelectedItemPosition(),
                        spinner_Motor_4.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_Motor_4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int m2=spinner_Motor_2.getSelectedItemPosition();
                int m3=spinner_Motor_3.getSelectedItemPosition();
                int m1=spinner_Motor_1.getSelectedItemPosition();
                if (position==m2){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_2.setSelection(i);


                }
                if (position==m3){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_3.setSelection(i);



                }
                if (position==m1){
                    int i = 0 ;
                    for ( ; i < 7 ; i++){
                        if ( i != spinner_Motor_1.getSelectedItemPosition() &&
                                i!= spinner_Motor_2.getSelectedItemPosition() &&
                                i!= spinner_Motor_3.getSelectedItemPosition()&&
                                i!= spinner_Motor_4.getSelectedItemPosition()){
                            break;
                        }
                    }
                    spinner_Motor_1.setSelection(i);

                }
                setTaken4w(spinner_Motor_1.getSelectedItemPosition(),
                        spinner_Motor_2.getSelectedItemPosition(),
                        spinner_Motor_3.getSelectedItemPosition(),
                        spinner_Motor_4.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button save = popup.findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btn_save","hello");
                Editable R=RtextBox.getText() ;
                Editable r = rtextBox.getText();
                if (R.toString().isEmpty() && r.toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Default radius for robot and wheel", Toast.LENGTH_LONG).show();
                    setDistr();
                    setDistR();
                }
                else {
                    setDistR(R.toString());
                    setDistr(r.toString());
                }
                confDialog.dismiss();
                Toast.makeText(MainActivity.this, "Four wheel Robot configuration selected", Toast.LENGTH_LONG).show();
            }
        });
        alertdialogbuilder.setView(popup);
        confDialog = alertdialogbuilder.create();
        confDialog.show ();
    }

    public void setDistR(String distR ) {
        if (distR.isEmpty())
            Toast.makeText(this.getApplicationContext(), "please Enter the Radius of the Robot", Toast.LENGTH_SHORT).show();
        else {
            try {
                this.distR = Double.parseDouble(distR);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Robot radius is not valid", Toast.LENGTH_SHORT).show();
                setDistR();
            }
        }
    }
    public void setDistR(){
        this.distR = Rdef;
    }

    public void setDistr(String distr) {
        if ( distr.isEmpty()) Toast.makeText(getApplicationContext(), "please Enter the Radius of the wheel", Toast.LENGTH_SHORT).show();
        else{
            try {
                this.distr = Double.parseDouble(distr);

            }catch (Exception e ){
                Toast.makeText(getApplicationContext(), "wheel radius is not valid", Toast.LENGTH_SHORT).show();
                setDistr();
            }

        Log.d("btn_save","this is r value " +distr);
        }

    }
    public void setDistr(){
        this.distr =Rdef;
    }

    private void setTaken2w(int m1, int m2) {
        resetTaken();
        taken[m1] = 0 ;
        taken[m2] = 1 ;

    }
    private void setTaken3w(int m1 ,int  m2 ,int m3){
        resetTaken();
        taken[m1] = 0 ;
        taken[m2] = 1 ;
        taken[m3] = 2 ;
    }
    private void setTaken4w(int m1 , int m2 ,int m3 , int m4){
        resetTaken();
        taken[m1] = 0 ;
        taken[m2] = 1 ;
        taken[m3] = 2 ;
        taken[m4] = 3 ;
    }
    private void resetTaken(){
        for (int i =0 ; i<7 ; i++)
            taken[i] = -1 ;
    }
    private int[] solSaturation(double[] res){
        double max = Math.abs(res[0]);

        for ( int i = 0 ;i<res.length; i++){
                if ( max <Math.abs(res[i]) ) {
                    max = Math.abs(res[i]);
                }
            }
        if ( max >1) {
            double ratio = 100 / max;
            for (int i = 0; i < res.length; i++) res[i] = Math.round(ratio * res[i]);
        }
        else{
            for (int i = 0; i < res.length; i++) res[i] = Math.round( res[i]*100);
        }
        int ans[] = new int[res.length];
        for ( int i = 0 ; i<res.length;i++)ans[i] =(int)res[i] ;
        return ans ;
    }

    public static boolean isConfiguration2w(){
        return configuration2w;
    }

}



