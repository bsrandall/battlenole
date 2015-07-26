package com.android.battlenoleproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;


/**
 * Created by Julian on 7/24/2015.
 */
public class WaitingRoom extends Activity {

    private ImageButton back;
    private ProgressBar progressBar;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String> BTArrayAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        back = (ImageButton) findViewById(R.id.btnBack);
        text = (TextView) findViewById(R.id.headerTextView);

        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            text.setText("Not supported");

            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        turnOn();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bReceiver, filter); // Don't forget to unregister during onDestroy

        //make discoverable
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);


    }

    public void turnOn() {
        //Turn on bluetooth if it is off
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            Toast.makeText(getApplicationContext(), "Bluetooth turned on",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is on",
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            if(myBluetoothAdapter.isEnabled()) {
                text.setText("Waiting for players...");
            } else {
                text.setText("Bluetooth disabled");
            }
        }
    }



    //When recieve a connection
    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }

    /* ONCE CONNECTED USE THIS

    void openBT() throws IOException {
     UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard                //SerialPortService ID
     mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
     mmSocket.connect();
     mmOutputStream = mmSocket.getOutputStream();
     mmInputStream = mmSocket.getInputStream();
     beginListenForData();
     myLabel.setText("Bluetooth Opened");
}

void beginListenForData() {
     final Handler handler = new Handler();
     final byte delimiter = 10; //This is the ASCII code for a newline character

     stopWorker = false;
     readBufferPosition = 0;
     readBuffer = new byte[1024];
     workerThread = new Thread(new Runnable() {
          public void run() {
               while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        mmInputStream.read(packetBytes);
                        for(int i=0;i<bytesAvailable;i++) {
                             byte b = packetBytes[i];
                             if(b == delimiter) {
                                  byte[] encodedBytes = new byte[readBufferPosition];
                                  System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                  final String data = new String(encodedBytes, "US-ASCII");
                                  readBufferPosition = 0;

                                  handler.post(new Runnable() {
                                       public void run() {
                                            myLabel.setText(data);
                                       }
                                  });
                             }else {
                                  readBuffer[readBufferPosition++] = b;
                             }
                        }
                   }
              }catch (IOException ex) {
                   stopWorker = true;
              }
         }
    }
});

workerThread.start();
}

void sendData() throws IOException {
     String msg = myTextbox.getText().toString();
     msg += "\n";
     //mmOutputStream.write(msg.getBytes());
     mmOutputStream.write('A');
     myLabel.setText("Data Sent");
}
     */
}
