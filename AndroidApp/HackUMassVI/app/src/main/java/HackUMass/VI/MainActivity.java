package HackUMass.VI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    Button startButton, sendButton, clearButton, stopButton;
    TextView textView;
    EditText editText, lapText;
    CheckBox horse1, horse2, horse3;
    RadioButton slowB, mediumB, highB;
    ImageView rHorse, bHorse, gHorse;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                tvAppend(textView, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
    };
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            tvAppend(textView, "Begin");
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            tvAppend(textView,"Serial Connection Opened!\n");

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                            tvAppend(textView, "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                        tvAppend(textView, "PORT NOT null");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                    tvAppend(textView, "PORT NOT Granted");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickStop(stopButton);

            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (Button) findViewById(R.id.buttonStart);
        sendButton = (Button) findViewById(R.id.buttonSend);
        clearButton = (Button) findViewById(R.id.buttonClear);
        stopButton = (Button) findViewById(R.id.buttonStop);
        editText = (EditText) findViewById(R.id.editText);
        lapText = (EditText) findViewById(R.id.lapText);
        textView = (TextView) findViewById(R.id.textView);
        horse1 = (CheckBox) findViewById(R.id.horse1);
        horse2 = (CheckBox) findViewById(R.id.horse2);
        horse3 = (CheckBox) findViewById(R.id.horse3);
        slowB = (RadioButton) findViewById(R.id.slowB);
        mediumB = (RadioButton) findViewById(R.id.mediumB);
        highB = (RadioButton) findViewById(R.id.highB);
        rHorse = (ImageView) findViewById(R.id.rHorse);
        bHorse = (ImageView) findViewById(R.id.bHorse);
        gHorse = (ImageView) findViewById(R.id.gHorse);
        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);


    }

    public void setUiEnabled(boolean bool) {
        //startButton.setEnabled(!bool);
        sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);

        //horse1.setEnabled(bool);
        //horse2.setEnabled(bool);
        //horse3.setEnabled(bool);

    }
    // set up UI by making the selections visable
    // also gets rid if begin
    public void setUpUI(){
        View a = findViewById(R.id.buttonSend);
        a.setVisibility(View.VISIBLE);
        View b = findViewById(R.id.buttonStart);
        b.setVisibility(View.INVISIBLE);
        View c = findViewById(R.id.buttonClear);
        c.setVisibility(View.VISIBLE);
        View d = findViewById(R.id.textView1);
        d.setVisibility(View.VISIBLE);
        View e = findViewById(R.id.horse1);
        e.setVisibility(View.VISIBLE);
        View f = findViewById(R.id.horse2);
        f.setVisibility(View.VISIBLE);
        View g = findViewById(R.id.horse3);
        g.setVisibility(View.VISIBLE);
        View h = findViewById(R.id.textView2);
        h.setVisibility(View.VISIBLE);
        View i = findViewById(R.id.speedGroup);
        i.setVisibility(View.VISIBLE);
//        View j = findViewById(R.id.textView);
//        j.setVisibility(View.VISIBLE);
        View k = findViewById(R.id.lapText);
        k.setVisibility(View.VISIBLE);
        View l = findViewById(R.id.bHorse);
        l.setVisibility(View.VISIBLE);
        View m = findViewById(R.id.gHorse);
        m.setVisibility(View.VISIBLE);
        View n = findViewById(R.id.rHorse);
        n.setVisibility(View.VISIBLE);
    }

    public void onClickStart(View view) {
        tvAppend(textView, "Pressed");
//        View b = findViewById(R.id.buttonSend);
//        b.setVisibility(View.VISIBLE);
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            tvAppend(textView, "Device Exists");
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                tvAppend(textView,String.valueOf(deviceVID));
                if (deviceVID == 0x1A86)//Arduino Vendor ID
                {
                    tvAppend(textView, "Arduino Found");
                    setUpUI();
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }


    }

    // check what horses are in play
    public boolean isChecked1(){
        return(horse1.isChecked());
    }
    public boolean isChecked2(){
        return(horse2.isChecked());
    }
    public boolean isChecked3(){
        return(horse3.isChecked());
    }
    // transfers horse data into string
    public String checkAllHorses(){
        String horses = "";
        // if no horses are selected, it will chose the first one and run it
        if(isChecked1() == false && isChecked2() == false && isChecked3() == false){
            tvAppend(textView, "No Horses Entered");
            horses = "[" + "true" + "]" + "[" + "false" + "]" +"[" + "false" + "]";
        }
        // else it will choose the horses selected
        else {
            horses = "[" + String.valueOf(isChecked1()) + "]" + "[" + String.valueOf(isChecked3()) + "]" + "[" + String.valueOf(isChecked2()) + "]";
        }
        return horses;
    }

    // check what speed
    public boolean checkLOW(){
        return(slowB.isChecked());
    }
    public boolean checkMED(){
        return(mediumB.isChecked());
    }
    public boolean checkHIGH(){
        return(highB.isChecked());
    }

    // put speed into a string
    public String checkSpeed(){
        String speed = "";
        if(checkLOW()){
            speed = "[" + 600 + "]";
        }
        else if(checkMED()){
            speed = "[" + 475 + "]";
        }
        else if(checkHIGH()){
            speed = "[" + 150 + "]";
        }
        else{
            tvAppend(textView, "No Speed Entered");
            // sets the default speed to medium if no input
            speed = "[" + 475 + "]";
        }
        return speed;
    }
    // check num of laps
    public String numLaps(){
        String numL = lapText.getText().toString();
        String laps = "[" + numL + "]";
        return laps;
    }
    // needs to send string of format String below to arduino for parsing
    public void onClickSend(View view) {
        //String string = editText.getText().toString();
        //String info = "[true][false][true][400][5]!";
        String info = checkAllHorses() + checkSpeed() + numLaps() + "!";
        serialPort.write(info.getBytes());
        tvAppend(textView, "\nData Sent : " + info + "\n");

    }

    public void onClickStop(View view) {
        setUiEnabled(false);
        serialPort.close();
        tvAppend(textView,"\nSerial Connection Closed! \n");

    }

    private void clearUI(){
        // clears checked data
        if(isChecked1()){
            horse1.setChecked(false);
        }
        if(isChecked2()){
            horse2.setChecked(false);
        }
        if(isChecked3()){
            horse3.setChecked(false);
        }
        if(checkLOW()) {
            slowB.setChecked(false);
        }
        if(checkMED()) {
            mediumB.setChecked(false);
        }
        if(checkHIGH()) {
            highB.setChecked(false);
        }
        // clears lap number
        lapText.setText("");

        // resets to just the begin button
        View a = findViewById(R.id.buttonSend);
        a.setVisibility(View.INVISIBLE);
        View b = findViewById(R.id.buttonStart);
        b.setVisibility(View.VISIBLE);
        View c = findViewById(R.id.buttonClear);
        c.setVisibility(View.INVISIBLE);
        View d = findViewById(R.id.textView1);
        d.setVisibility(View.INVISIBLE);
        View e = findViewById(R.id.horse1);
        e.setVisibility(View.INVISIBLE);
        View f = findViewById(R.id.horse2);
        f.setVisibility(View.INVISIBLE);
        View g = findViewById(R.id.horse3);
        g.setVisibility(View.INVISIBLE);
        View h = findViewById(R.id.textView2);
        h.setVisibility(View.INVISIBLE);
        View i = findViewById(R.id.speedGroup);
        i.setVisibility(View.INVISIBLE);
        View j = findViewById(R.id.textView);
        j.setVisibility(View.INVISIBLE);
        View k = findViewById(R.id.lapText);
        k.setVisibility(View.INVISIBLE);
        View l = findViewById(R.id.bHorse);
        l.setVisibility(View.INVISIBLE);
        View m = findViewById(R.id.gHorse);
        m.setVisibility(View.INVISIBLE);
        View n = findViewById(R.id.rHorse);
        n.setVisibility(View.INVISIBLE);
    }

    public void onClickClear(View view) {
        textView.setText(" ");
        clearUI();
        View n = findViewById(R.id.buttonStart);
        n.setVisibility(View.VISIBLE);
    }

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }

}
