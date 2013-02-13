package com.enhanceye.mindwavelogger;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.os.Environment;
//import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import com.neurosky.thinkgear.*;
//import com.neurosky.thinkgear.TGDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;


public class MainActivity extends Activity {

	TGDevice tgDevice;
	BluetoothAdapter btAdapter;

	TextView tv;
	Button button;
	TGRawMulti tgRaw;
	
	double att = 0;
	double med = 0;
	
	
    Calendar t = Calendar.getInstance();
    int buttonState = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	btAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(btAdapter != null) {
    	tgDevice = new TGDevice(btAdapter, handler);
//        tgDevice.connect(true);
//        tgDevice.start();
    	}
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.textView1);
        tv.append("Android version: " + android.os.Build.VERSION.RELEASE + "\n" );
        button = (Button)findViewById(R.id.button1);
        tgRaw = new TGRawMulti();
    }




    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case TGDevice.MSG_STATE_CHANGE:

                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
	                	tv.append("Connecting...\n");
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
	                	tv.append("Connected.\n");
	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	tv.append("Can't find\n");
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	tv.append("not paired\n");
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	tv.append("Disconnected\n");
                }

                break;
            case TGDevice.MSG_POOR_SIGNAL:
            		//signal = msg.arg1;
            		tv.append("PoorSignal: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_RAW_DATA:	  
            		//raw1 = msg.arg1;
            		//tv.append("Got raw: " + msg.arg1 + "\n");
    
            	break;
            case TGDevice.MSG_HEART_RATE:
        		tv.append("Heart rate: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_ATTENTION:
            		att = msg.arg1 * 2.55;
            		tv.append("Attention: " + msg.arg1 + "\n");            		
            		//tv.append("Attention Color: " + att + "\n");
            		t = Calendar.getInstance();
            		writeExtFile("Attention: " + msg.arg1 + " @" + t.get(Calendar.HOUR) + ":" + t.get(Calendar.MINUTE) + ":" + t.get(Calendar.SECOND) + t.get(Calendar.AM_PM) + " " + "\n");
            		//Log.v("HelloA", "Attention: " + att + "\n");
            		//tv.setBackgroundColor(Color.argb(255, (int)att, 0, 0));
            	break;
            case TGDevice.MSG_MEDITATION:
            	med = msg.arg1 * 2.55;
            	tv.append("Meditation: " + msg.arg1 + "\n");
        		t = Calendar.getInstance();
        		writeExtFile("Meditation: " + msg.arg1 + " @" + t.get(Calendar.HOUR) + ":" + t.get(Calendar.MINUTE) + ":" + t.get(Calendar.SECOND) + t.get(Calendar.AM_PM) + " " + "\n");
            	break;
            case TGDevice.MSG_BLINK:
            		tv.append("Blink: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_RAW_COUNT:
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	break;
            case TGDevice.MSG_RAW_MULTI:
            	//TGRawMulti rawM = (TGRawMulti)msg.obj;
            	//tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
            	tgRaw = (TGRawMulti)msg.obj;
            	
            	tv.append("raw: " + 
            	tgRaw.ch1 + ", " + 
            	tgRaw.ch2 + ", " + 
            	tgRaw.ch3 + ", " + 
            	tgRaw.ch4 + ", " + 
            	tgRaw.ch5 + ", " + 
            	tgRaw.ch6 + ", " +
            	tgRaw.ch7 + ", " + 
            	tgRaw.ch8 + ", " + 
            			"\n");
            default:
            	break;
        }
        		tv.setBackgroundColor(Color.argb(255, (int)att, 0, (int)med));
        }  
    };

    public void doStuff(View view) {
    	//should know state and update button to close connection

    	if (buttonState == 0){
    	if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
    		tgDevice.connect(true);
//    	writeExtFile("Logging session started: " + DateFormat.getTimeFormat(this).format(t.getTime()) + "\n");
    	t = Calendar.getInstance();
    	writeExtFile("Logging session started: " + DateFormat.getTimeFormat(this).format(t.getTime()) + " " + DateFormat.getDateFormat(this).format(t.getTime()) + "\n");
    	button.setText("Disconnect");
    	buttonState = 1;
    	}
    	else if(buttonState == 1){
        	if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() == TGDevice.STATE_CONNECTED)
        		tgDevice.close();
        	t = Calendar.getInstance();
        	writeExtFile("Logging session stopped: " + DateFormat.getTimeFormat(this).format(t.getTime()) + " " + DateFormat.getDateFormat(this).format(t.getTime()) + "\n");
        	button.setText("Connect");
        	buttonState = 0;
    	}
    }

    public void writeFile(String string){
        String FILENAME = "hello_file";
        //String string = "hello world!";

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write(string.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void writeExtFile(String string){
    	String filename = "mindwavelog.txt";
    	File file = new File(Environment.getExternalStorageDirectory(), filename);
    	FileOutputStream fos;
    	byte[] data = new String(string).getBytes();
    	try {
    	    fos = new FileOutputStream(file, true);
    	    fos.write(data);
    	    fos.flush();
    	    fos.close();
    	} catch (FileNotFoundException e) {
    	    // handle exception
    	} catch (IOException e) {
    	    // handle exception
    	}
    }
    
    
}