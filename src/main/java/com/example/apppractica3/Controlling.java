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
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Controlling extends Activity {
    private static final String TAG = "Controlling";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    public static BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    public static BluetoothDevice dev;

    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;


    private BluetoothAdapter mBTAdapter = null;
    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable
    private static final int SETTINGS = 20;
    public static BluetoothDevice enviaParaAct = null;
    public static int mBufferSize = 50000; //Default
    public static final String DEVICE_EXTRA = "com.example.apppractica3.SOCKET";
    public static final String DEVICE_UUID = "com.example.apppractica3.uuid";
    private static final String DEVICE_LIST = "com.example.apppractica3.devicelist";
    private static final String DEVICE_LIST_SELECTED = "com.example.apppractica3.devicelistselected";
    public static final String BUFFER_SIZE = "com.example.apppractica3.buffersize";




    private BluetoothDevice mDevice;

    final static char on='D';//Derecha
    final static char off='T';//Adelante
    char izquierda = 'I';//Izquierda
    char atras = 'A';//Atras
    char automatico = '$';//Automatico
    char limpiar = '*';//Limpiar
    char cleanEEPROM = '%';//Limpiar la EEPROM
    char activarModManual = '@'; //Dar inicio al modo manual
    char activarRecorrido = '?';
    boolean activacion = false;

    private ProgressDialog progressDialog;
    Button btnOn,btnOff, btnIzquierda, btnAbajo, btnLimpiarEEPROM, btnAutomatico, btnLimpieza, activarManual;
    Button recorrer;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlling);

        ActivityHelper.initialize(this);
        // mBtnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnOn=(Button)findViewById(R.id.onn);//Derecha
        btnOn.setEnabled(activacion);

        btnOff=(Button)findViewById(R.id.offf); //Adelante
        btnOff.setEnabled(activacion);

        btnIzquierda = (Button)findViewById(R.id.btnLft);
        btnIzquierda.setEnabled(activacion);

        btnAbajo = (Button)findViewById(R.id.btnDwn);
        btnAbajo.setEnabled(activacion);

        btnLimpiarEEPROM = (Button)findViewById(R.id.clrEEPROM);
        btnAutomatico = (Button)findViewById(R.id.btnAuto);
        btnLimpieza = (Button)findViewById(R.id.bntLimpiar);
        activarManual = (Button)findViewById(R.id.idActivar);
        recorrer = (Button)findViewById(R.id.btnRecorrido);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);

        Log.d(TAG, "Ready");



//Boton derecha para mover carro en modo manual

        btnOn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub


                try {
                    mBTSocket.getOutputStream().write(on);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg("Error");
                }
            }});



//Boton adelante para mover carro manual

        btnOff.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    String envio = "9";
                    mBTSocket.getOutputStream().write(off);
                    msg(""+off);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg("Error");
                }
            }});



//Boton izquierda para carro manual
        btnIzquierda.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    mBTSocket.getOutputStream().write(izquierda);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg("Error");
                }
            }});

//Boton abajo para carro manual
        btnAbajo.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    mBTSocket.getOutputStream().write(atras);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg("Error");
                }
            }});

        //Boton para pasar de carro manual a carro automatico
        btnAutomatico.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    mBTSocket.getOutputStream().write(automatico);
                    activacion = false;
                    btnOff.setEnabled(activacion);
                    btnOn.setEnabled(activacion);
                    btnIzquierda.setEnabled(activacion);
                    btnAbajo.setEnabled(activacion);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg("Error");
                }
            }});

        //Boton para pasar de carro manual a carro automatico
        btnLimpieza.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    mBTSocket.getOutputStream().write(limpiar);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg("Error");
                }
            }});

        //Habilitar el modo manual
        activarManual.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    mBTSocket.getOutputStream().write(activarModManual);
                    activacion = true;
                    btnOff.setEnabled(activacion);
                    btnOn.setEnabled(activacion);
                    btnIzquierda.setEnabled(activacion);
                    btnAbajo.setEnabled(activacion);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg("Error");
                }
            }});


        //Limpiar la EEPROM perro
        btnLimpiarEEPROM.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    mBTSocket.getOutputStream().write(cleanEEPROM);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg("Error");
                }
            }});

        recorrer.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    mBTSocket.getOutputStream().write(activarRecorrido);


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg("Error");
                }
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
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
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

            progressDialog = ProgressDialog.show(Controlling.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554

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
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }



}
