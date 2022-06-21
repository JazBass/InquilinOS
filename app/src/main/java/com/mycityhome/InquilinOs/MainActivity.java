package com.mycityhome.InquilinOs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    /*----------------------Buttons and views----------------------*/
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    Toolbar toolbar;
    Button btnInfo, btnAbout, btnServices;
    ImageButton btnContact;
    TextView txtDecibels, txtSelectLanguage;
    /*-------------------------Permissions----------------------------*/
    int PERMISSIONS_CODE = 1;
    String[] Permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /*---------------Decibels measure---------------*/
    static final private double EMA_FILTER = 0.6;
    private static double mEMA = 0.0;
    MediaRecorder mRecorder;
    double decibels;
    double[] decibelsCounter;
    final Handler mHandler = new Handler();
    final Runnable updater = this::measureDecibels;
    Thread runner;
    boolean isCounting;
    /*------------------Language change-----------------*/
    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        /*----------------------Buttons and views----------------------*/
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        btnContact = findViewById(R.id.btnContact);
        btnAbout = findViewById(R.id.btnAbout);
        btnInfo = findViewById(R.id.btnInfo);
        btnServices = findViewById(R.id.btnServices);
        txtDecibels = findViewById(R.id.txtDcbl);
        txtSelectLanguage = findViewById(R.id.txtSelectLanguage);

        /*--------------------------Listeners--------------------------*/
        btnAbout.setOnClickListener(view -> {

        });
        btnServices.setOnClickListener(view -> {

        });
        btnContact.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/34633335208?text=Hola"));
            startActivity(i);
        });

        /*-----------Toolbar for change language-----------*/
        setToolBar();
        setListener();

        /*--------------------Permissions request--------------------*/
        if (!hasPermissions(Permissions)) {
            requestPermissions();
        }

        /*---------------------Decibels measure--------------------*/
        if (runner == null) {
            runner = new Thread(() -> {
                while (runner != null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.post(updater);
                }
            });
            runner.start();
            Log.d("Noise", "start runner()");
        }
    }

    /*------------------------Left Menu------------------------*/
    private void setListener() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.french:
                    context = LanguageManager.setLocale(MainActivity.this, "fr");
                    resources = context.getResources();
                    break;
                case R.id.english:
                    context = LanguageManager.setLocale(MainActivity.this, "en");
                    resources = context.getResources();
                    break;
                case R.id.spanish:
                    context = LanguageManager.setLocale(MainActivity.this, "es");
                    resources = context.getResources();
                    break;
            }
            refreshLanguage();
            drawerLayout.close();
            Toast.makeText(MainActivity.this, resources.getString(R.string.newLanguage), Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    public void refreshLanguage(){
        btnAbout.setText(resources.getString(R.string.about));
        btnInfo.setText(resources.getString(R.string.info));
        btnServices.setText(resources.getString(R.string.services));
    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //TODO: Change logo size
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_lang);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /*----------------------Permissions request----------------------*/

    private boolean hasPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    private void requestPermissions() {
        EasyPermissions.requestPermissions(this, "The app need this permission for works correctly", PERMISSIONS_CODE,
                Manifest.permission.RECORD_AUDIO);
        EasyPermissions.requestPermissions(this, "Accept this permission please", PERMISSIONS_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permission accepted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestPermissions();
        }
    }

    /*----------------------Starting recorder----------------------*/

    public void onPause() {
        super.onPause();
        //stopRecorder();
    }

    public void startRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
        }
    }
    public void onResume() {
        super.onResume();
        startRecorder();
    }

    /*
    public void stopRecorder() {

        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }
     */
    /*----------------------------function for take ambient decibels----------------------------*/
    /*
    public double soundDb(double ampl) {
        return 20 * (float) Math.log10(getAmplitudeEMA() / ampl);
    }
    */
    public double convertDb(double amplitude) {
        /*
         *Los teléfonos celulares pueden alcanzar hasta 90 db + -
         *getMaxAmplitude devuelve un valor entre 0 y 32767 (en la mayoría de los teléfonos). eso
         *significa que si el db máximo es 90, la presión en el micrófono es 0.6325 Pascal. hace una
         *comparación con el valor anterior de getMaxAmplitude. necesitamos dividir maxAmplitude con
         *(32767/0.6325) 51805.5336 o si 100db entonces 46676.6381
         */
        double EMA_FILTER = 0.6;
        SharedPreferences sp = this.getSharedPreferences("device-base", MODE_PRIVATE);
        double amp = (double) sp.getFloat("amplitude", 0);
        double mEMAValue = EMA_FILTER * amplitude + (1.0 - EMA_FILTER) * mEMA;
        //Log.i("db", amp+"");
        //Asumiendo que la presión de referencia mínima es 0.000085 Pascal
        // (en la mayoría de los teléfonos) es igual a 0 db
        // TODO: Find out the minimum reference in Motorola
        return 20 * (float) Math.log10((mEMAValue / 51805.5336) / 0.000028251);
    }

    public void measureDecibels() {
        double amplitude = mRecorder.getMaxAmplitude();
        if (amplitude > 0 && amplitude < 1000000) {
            decibels = convertDb(amplitude);
            if (decibels > 30) {
                isCounting=true;
                txtDecibels.setText(String.format("%.2f", decibels) + " DB");
                decibelsCounter = new double[60];
                convertDb(mRecorder.getMaxAmplitude());
                /*
                while (dbl >= 30 && isCounting) {
                    for (int i = 0; i < decibelsCounter.length; i++) {
                        decibelsCounter[i]=decibels;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (i == 59) {
                            sendDecibArray(decibelsCounter);
                        }
                        dbl = convertdDb(mRecorder.getMaxAmplitude());
                        Log.i("decib", "decibeles: "+dbl);

                    }
                }*/
                //startCounting();
            }
        }
        //startCounting(decibels);
        //hardNoise(decibels);
    }

    private void sendDecibArray(double[] db){
        Toast.makeText(this, "Aqui hay fiesta", Toast.LENGTH_LONG).show();
    }
    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    /*----------------------------Start counting decibels----------------------------*/
   /*
   public void startCounting() {
        Runnable mRunnable = () -> {

        };
    }
    */
    private void hardNoise(double dbl) {
        //TODO: fun Enviar mensaje a CRM
        turnOffLight();
    }

    /*----------------------Sending http request for turn off lights----------------------*/
    private void turnOffLight() {
        String http = "https://tcpmch.herokuapp.com/?client=Oficina&cmd=toggleLight";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(http)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String res = response.body().string();
                    if (res.compareTo("{\"code\":0}") == 0) {
                        res = "Interruptor luz";
                    }
                    Log.d("OKHTTP", "onResponse: " + res);
                    Intent intent = new Intent();
                    intent.putExtra("result", res);
                    setResult(10, intent);
                }
            }
        });
    }


}