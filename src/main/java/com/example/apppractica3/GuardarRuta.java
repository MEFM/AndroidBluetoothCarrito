package com.example.apppractica3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class GuardarRuta extends Activity {
    //Variables utilizadas para conexcion con el modulo Bluetooth
    private static final String TAG = "GuardarRuta";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    private BluetoothDevice mDevice;

    //Variables de la clase guardar, funcionamieto
    Button btnGuardar;
    Spinner cbxDireccion, cbxTiempo;
    ListView movRuta;
    //PlainText nombreRuta;
    String movimientoo = "";
    String tiempoo = "";
    ArrayList<Ruta> guardarRuta = new ArrayList<Ruta>();
    ArrayList<MovimientosRuta> movimientos = new ArrayList<MovimientosRuta>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_ruta);
        ActivityHelper.initialize(this);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);

        Log.d(TAG, "Ready");


        cbxDireccion = (Spinner)findViewById(R.id.idDireccion);
        cbxTiempo = (Spinner)findViewById(R.id.idSegundos);

        btnGuardar = (Button)findViewById(R.id.btnSave);

        movRuta = (ListView)findViewById(R.id.listView);

        ArrayList<String> direcciones = new ArrayList<>();
        ArrayList<String> tiempo = new ArrayList<>();

        direcciones.add("Adelante");
        direcciones.add("Izquierda");
        direcciones.add("Derecha");
        direcciones.add("Atras");

        tiempo.add("1");
        tiempo.add("2");
        tiempo.add("3");
        tiempo.add("4");
        tiempo.add("5");
        tiempo.add("6");
        tiempo.add("7");
        tiempo.add("8");
        tiempo.add("9");

        ArrayAdapter adprM = new ArrayAdapter(GuardarRuta.this, android.R.layout.simple_spinner_dropdown_item, direcciones);
        ArrayAdapter adprT = new ArrayAdapter(GuardarRuta.this, android.R.layout.simple_dropdown_item_1line, tiempo);

        cbxDireccion.setAdapter(adprM);
        cbxTiempo.setAdapter(adprT);





        cbxDireccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String elemento = (String) cbxDireccion.getAdapter().getItem(position);
                switch(elemento){
                    case "Izquierda":
                        movimientoo = "6";
                        break;
                    case "Derecha":
                        movimientoo = "9";
                        break;
                    case "Adelante":
                        movimientoo = "7";
                        break;
                    case "Atras":
                        movimientoo = "5";
                        break;
                }

            }
        });


        cbxTiempo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String elemento = (String) cbxDireccion.getAdapter().getItem(position);
                tiempoo = elemento;

            }
        });

        movimientos.add(new MovimientosRuta(movimientoo, tiempoo));


        btnGuardar.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                String nombre = "";
                guardarRuta.add(new Ruta(nombre, movimientos));
                movimientos = new ArrayList<>();

            }});

    }


    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                // msg("Mire esta cosa "+inputStream);
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;

                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */



                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new GuardarRuta.DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new GuardarRuta.ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            //progressDialog = ProgressDialog.show(Controlling.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554

        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;



            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new GuardarRuta.ReadInput(); // Kick off input reader
            }

            //progressDialog.dismiss();
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }



}
