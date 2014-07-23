package com.slx.tsd_barcode.invertory;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataOutputStream;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;

import android_serialport_api.DeviceControl;


import java.io.File;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Manifest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android_serialport_api.DeviceControl;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;


public class MainActivity extends ActionBarActivity{

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    int T = 0;
    int back = 0;
    String bar = "";
    private boolean die = false;
    private DeviceControl DevCtrl;
    private Handler n_handler = null;
    public static final int KEY_SCAN = 111;
    private int KEY_POSITION = 0;
    private boolean key_start = true;
    private boolean Powered = false;
    private Timer timer = new Timer();
    private Timer retrig_timer = new Timer();



    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    boolean reading = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            DevCtrl = new DeviceControl("/proc/driver/scan");
        } catch (IOException e1) {

        }
        n_handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    try {
                        if (key_start == false) {
                            DevCtrl.TriggerOffDevice();
                            timer = new Timer();
                            key_start = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//
//        // Set up the dropdown list navigation in the action bar.
//        actionBar.setListNavigationCallbacks(
//                // Specify a SpinnerAdapter to populate the dropdown list.
//                new ArrayAdapter<String>(
//                        actionBar.getThemedContext(),
//                        android.R.layout.simple_list_item_1,
//                        android.R.id.text1,
//                        new String[] {
//
//                        }),
//                this);
    }
    class RetrigTask extends TimerTask {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            n_handler.sendMessage(message);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEY_SCAN:
                if (KEY_POSITION == 0) {
                    try {
                        if (key_start == true) {
                            if (Powered == false) {
                                Powered = true;
                                DevCtrl.PowerOnDevice();
                            }
                            timer.cancel();
                            DevCtrl.TriggerOnDevice();
                            key_start = false;
                            retrig_timer = new Timer();
                            retrig_timer.schedule(new RetrigTask(), 2000);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//          case KeyEvent.KEYCODE_CAMERA:
//            {if(event.getAction() == KeyEvent.ACTION_DOWN){Intent qrDroid = new Intent("la.droid.qr.scan");
//                try {startActivityForResult(qrDroid, 0);}
//                catch (Exception e){};
//                return true;}
//
//            }
//            case KeyEvent.KEYCODE_BACK: {
//              Intent result = new Intent("Complete");
//              setResult(Activity.RESULT_OK, result);
//              finish();
//              return true;
                // }
                //break;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }

//
//    public void RunAsRoot(String[] cmds){
//        try {Process p = Runtime.getRuntime().exec("su");
//
//        DataOutputStream os = new DataOutputStream(p.getOutputStream());
//        for (String tmpCmd : cmds) {
//            try {os.writeBytes(tmpCmd+"\n");}
//            catch(Exception e){}
//        }
//        os.writeBytes("exit\n");
//        os.flush();}
//        catch (Exception e){}
//    }
    public void ReadAll(View v){

//        try {
//        Process
//             process = Runtime.getRuntime().exec("/system/xbin/su -c echo off>/proc/driver/scan;/system/xbin/su -c echo on >/proc/driver/scan;/system/xbin/su -c echo trig >/proc/driver/scan;/system/bin/cat /dev/eser0");
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//            while (in.readLine().equals("")){
//            MainActivity.MakeToast(getApplicationContext(), in.readLine()).show();}
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            {
                if (Powered == false) {
                    Powered = true;
                    DevCtrl.PowerOnDevice();
                }
                timer.cancel();
                DevCtrl.TriggerOnDevice();
                key_start = false;
                retrig_timer = new Timer();
                retrig_timer.schedule(new RetrigTask(), 2000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */









    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size = 0;
                try {
                    byte[] buffer = new byte[128];
                    if (mInputStream == null)
                        return;

                    if (T == 0) {
                        if (mInputStream.available() > 0) {
                            size = mInputStream.read(buffer);
                        }
                    } else {
                        size = mInputStream.read(buffer);
                    }

                    if (die)
                        return;

                    if (size > 0) {
                        Log.v("BARCODE", "THREAD LIST META "+buffer[0]);
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    return;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    }










    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    key_start = true;
                    // //////////////////////////////////////////////////
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    String sizeW = dm.widthPixels + "x" + dm.heightPixels;
                    String barcode = new String(buffer, 0, size);
                    if (sizeW.equals("240x320")) {
                        if (barcode.length() > 1) {
                            if (reading) {
                                bar += barcode;
                            } else {
                                bar = "";
                                reading = true;
                                bar += barcode;
                                // //////////////////////////////
                                new CountDownTimer(100, 50) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        reading = false;
                                        ParseBarcode(bar);
                                        bar = "";
                                    }
                                }.start();
                                // //////////////////////////////
                            }
                        }
                    } else if (sizeW.equals("480x640")) {
                        if (barcode.contains("\n")) {
                            ParseBarcode(bar + barcode.replace("\n", ""));
                            bar = "";
                        } else {
                            bar += barcode;
                        }
                    } else {
                        ParseBarcode(barcode);
                    }
                    // /////////////////////////////////////////////////
                } catch (Exception e) {
                }
            }
        });
    }


    void ParseBarcode(String t) {


        if (!t.equals("AFANDACT")){
            MainActivity.MakeToast(getApplicationContext(), "" + t).show();}
        else{Intent goHome = new Intent(Intent.ACTION_MAIN);
            goHome.setClassName("com.android.launcher", "com.android.launcher2.Launcher");
            startActivity(goHome);
            MakeToast(getApplicationContext(), "Разблокированно!" ).show();
        }
    }

    public static Toast MakeToast(Context c, String t) {

        Toast toast = Toast.makeText(c, t, Toast.LENGTH_LONG);

        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout linearLayout = null;
        linearLayout = (LinearLayout) toast.getView();
        View childW = linearLayout.getChildAt(0);
        TextView messageTextView = null;
        messageTextView = (TextView) childW;

        messageTextView.setTextSize(30);
        messageTextView.setTextColor(Color.rgb(0, 255, 0));

        return toast;
    }
    protected void onPause() {
        die = true;
        Log.v("BARCODE", "META PAUSE");
        mReadThread.interrupt();
        mReadThread = null;
        Powered = false;
        try {
            DevCtrl.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer.cancel();
        retrig_timer.cancel();
        super.onPause();
    }
    protected void onResume() {
        die = false;
        try {
            if (Powered == false) {
                Powered = true;
                DevCtrl.PowerOffDevice();
                DevCtrl.PowerOnDevice();
            }
            mSerialPort = new SerialPort(new File("/dev/eser0"), 9600, 0);
        } catch (SecurityException e) {
        } catch (Exception e) {
        }

        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();

        try {
            DevCtrl = new DeviceControl("/proc/driver/scan");
        } catch (Exception e1) {
        }

//        if (Conf.needToUpdate) {
//            Conf.needToUpdate = false;
//            this.LoadData();
//        }
        back = 0;
        super.onResume();


    }









}
