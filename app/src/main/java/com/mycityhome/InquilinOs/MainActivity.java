package com.mycityhome.InquilinOs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

    /*----------------------botonos y vistas----------------------*/
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    Toolbar toolbar;
    Button btnInfo, btnAbout, btnServices;
    ImageButton btnContact;
    TextView txtDcbl;
    String language;
    LanguageManager languageManager;
    /*-------------------------Permisos----------------------------*/
    int PERMISSIONS_CODE = 1;
    String[] Permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /*---------------Medidor de decibelios---------------*/
    static final private double EMA_FILTER = 0.6;
    private static double mEMA = 0.0;
    MediaRecorder mRecorder;
    double decibels;
    double[] decibelsCounter;
    final Handler mHandler = new Handler();
    final Runnable updater = () -> measureDecibels();
    Thread runner;
    boolean isCounting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        /*----------------------botonos y vistas----------------------*/
        navigationView = findViewById(R.id.navigationView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        btnContact = findViewById(R.id.btnContact);
        btnAbout = findViewById(R.id.btnAbout);
        btnServices = findViewById(R.id.btnServices);
        txtDcbl = findViewById(R.id.txtDcbl);

        /*-----------Botones listeners-----------*/
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/34633335208?text=Hola"));
                startActivity(i);
            }
        });

        /*-----------Barra superior para el cambio de idioma-----------*/
        setToolBar();
        setListener();

        /*--------------------solicitud de permisos--------------------*/
        if (!hasPermissions(Permissions)) {
            requestPermissions();
        }

        /*---------------------Medidor de decibelios--------------------*/
        if (runner == null) {
            runner = new Thread(() -> {
                while (runner != null) {
                    try {
                        Thread.sleep(1000);
                        //Log.i("Noise", "Tock");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ;
                    mHandler.post(updater);
                }
            });
            runner.start();
            Log.d("Noise", "start runner()");
        }
    }

    /*Barra superior con menu desplegable para cambiar idiomas*/
    private void setListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.french:
                        language = "fr";
                        break;
                    case R.id.english:
                        language = "en";
                        break;
                    case R.id.spanish:
                        language = "es";
                        break;
                }
                languageManager.updateLanguge(language);
                return false;
            }
        });
    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //TODO: mejorar el tamaño del logo de idioma
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setLogo(R.drawable.logomch);
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
    //TODO: Cambio de string utilizado con distintos idiomas!

    /*----------------------Solicitando Permisos----------------------*/

    private boolean hasPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    private void requestPermissions() {
        EasyPermissions.requestPermissions(this, "Esta aplicación necestita este " +
                        "permiso para funcionar correctamente", PERMISSIONS_CODE,
                Manifest.permission.RECORD_AUDIO);
        EasyPermissions.requestPermissions(this, "Aceptar este permiso", PERMISSIONS_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permiso Aceptado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestPermissions();
        }
    }

    public void onResume() {
        super.onResume();
        startRecorder();
    }

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

    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }


    public double soundDb(double ampl) {
        return 20 * (float) Math.log10(getAmplitudeEMA() / ampl);
    }

    public double convertdDb(double amplitude) {
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
        // TODO: Averiguar la referencia minima en Motorola
        return 20 * (float) Math.log10((mEMAValue / 51805.5336) / 0.000028251);
    }

    public void measureDecibels() {
        double amplitude = mRecorder.getMaxAmplitude();
        if (amplitude > 0 && amplitude < 1000000) {
            decibels = convertdDb(amplitude);
            if (decibels > 30) {
                isCounting=true;
                txtDcbl.setText(String.format("%.2f", decibels) + " DB");
                decibelsCounter = new double[60];
                double dbl = convertdDb(mRecorder.getMaxAmplitude());
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
                    Toast.makeText(this, "Aqui hay fiesta", Toast.LENGTH_LONG).show();
                }
                //startCounting();
            }
        }
        //startCounting(decibels);
        //hardNoise(decibels);
    }

    private void sendDecibArray(double[] db){
        
    }

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
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String res = response.body().string();
                    if (res.compareTo("{\"code\":0}") == 0) {
                        res = "Puerta Abierta";
                    }
                    Log.d("OKHTTP", "onResponse: " + res);
                    Intent intent = new Intent();
                    intent.putExtra("result", res);
                    setResult(10, intent);
                }
            }
        });
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
}