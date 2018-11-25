package com.example.mehme.smartroom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class odaLampActivity extends AppCompatActivity {


    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread  mConnectedThread;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address;
    ImageView iv ;
    Button tg,tgdesk;
    boolean acikmi=false,acikmidesk=false;
    String []bitisArray={"Süre Limiti Yok","5 Dk","10 Dk","15 Dk","30 Dk","1 Saat","2 Saat","3 Saat"};
    Spinner sureSp;
    TextView txTarih,txSaat,txGun,txIsik,txIsikdesk;
    Date date;
    SimpleDateFormat sdf1,sdf2;
    String saat,tarih;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oda_lamp);
        iv=(ImageView)findViewById(R.id.imageGG);
        tg=(Button)findViewById(R.id.toggleButton);
        tgdesk=(Button)findViewById(R.id.toggleButtondesk);
        sureSp=(Spinner)findViewById(R.id.spinner);
        txTarih=(TextView)findViewById(R.id.tarih);
        txSaat=(TextView)findViewById(R.id.saat);
        txGun=(TextView)findViewById(R.id.gun);
        txIsik=(TextView)findViewById(R.id.isik);
        txIsikdesk=(TextView)findViewById(R.id.isikdesk);


        date=new Date();
        sdf1=new SimpleDateFormat("yyyy-MM-dd");
        sdf2=new SimpleDateFormat("HH:mm");
        saat=sdf2.format(date);
        tarih=sdf1.format(date);
        tg.setBackgroundResource(R.drawable.ic_lamp_off);


        Calendar calendar=Calendar.getInstance();
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,bitisArray);
        sureSp.setAdapter(adapter);
        txSaat.setText(saat);
        txGun.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.getDefault()));
        txTarih.setText(tarih);
        txIsik.setText("IŞIK YANMIYOR");


        if(calendar.get(Calendar.HOUR_OF_DAY)>7 && calendar.get(Calendar.HOUR_OF_DAY)<19){
            iv.setImageResource(R.drawable.gunduz_);

        }
        else if (calendar.get(Calendar.HOUR_OF_DAY)>19 && calendar.get(Calendar.HOUR_OF_DAY)<7){
            iv.setImageResource(R.drawable.gece_);
        }

        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string

                        int dataLength = dataInPrint.length();                          //get length of data received

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);


                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

    }

    public void ackapaClicked (View view){
        if(!acikmi){
            tg.setBackgroundResource(R.drawable.ic_lamp_on);
            mConnectedThread.write("K");
            txIsik.setText("ODA IŞIĞI AÇIK");

            acikmi=true;
        }else {
            tg.setBackgroundResource(R.drawable.ic_lamp_off);
            mConnectedThread.write("A");
            txIsik.setText("ODA IŞIĞI KAPALI");
            acikmi=false;
        }

    }

    public void ackapadeskClicked (View view){
        if(!acikmidesk){
            tgdesk.setBackgroundResource(R.drawable.ic_lamp_desk_on);
            mConnectedThread.write("k");
            txIsikdesk.setText("MASA IŞIĞI AÇIK");


            acikmidesk=true;
        }else {
            tgdesk.setBackgroundResource(R.drawable.ic_lamp_desk_off);
            mConnectedThread.write("a");
            txIsikdesk.setText("MASA IŞIĞI KAPALI");
            acikmidesk=false;
        }

    }

    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }


}

