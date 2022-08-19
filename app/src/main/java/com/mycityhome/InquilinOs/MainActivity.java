package com.mycityhome.InquilinOs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    /*----------------------Buttons and views----------------------*/
    Toolbar toolbar;
    CardView btnInfo, btnAbout, btnContact;
    FloatingActionButton btnServices;
    TextView txtDecibels;
    TextView txtInfo, txtWebsite, txtEvents;
    /*-------------------------Permissions----------------------------*/
    int PERMISSIONS_CODE = 1;
    String[] Permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    MediaRecorder mRecorder;
    double decibels;
    Stack<Double> decibelsHistory = new Stack<>();
    final Handler mHandler = new Handler();
    final Runnable updater = this::measureDecibels;
    Thread runner;
    /*------------------Language change-----------------*/
    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_layout);

        /*----------------------Buttons and views----------------------*/
        toolbar = findViewById(R.id.my_toolbar);
        btnContact = findViewById(R.id.btnContact);
        btnAbout = findViewById(R.id.btnAbout);
        btnInfo = findViewById(R.id.btnInfo);
        btnServices = findViewById(R.id.btnServices);
        txtEvents = findViewById(R.id.txtEvents);
        txtInfo = findViewById(R.id.txtInfo);
        txtWebsite = findViewById(R.id.txtWebsite);
        //txtDecibels = findViewById(R.id.txtDcbl);

        setToolBar();

        /*--------------------------Listeners--------------------------*/
        btnInfo.setOnClickListener(view -> {
            Intent i = new Intent(this, TurismActivity.class);
            startActivity(i);
        });
        btnServices.setOnClickListener(view -> launchLanguageFragment());
        btnAbout.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/34633335208?text=Hola"));
            startActivity(i);
        });

        btnContact.setOnClickListener(view ->{
            Intent i = new Intent(MainActivity.this, WebsiteActivity.class);
            i.putExtra("url","https://www.mycityhome.es");
            startActivity(i);
        });

        /*--------------------Permissions request--------------------*/
        if (!hasPermissions(Permissions)) {
            requestPermissions();
        }
        //TODO: Add location permissions request

        /*---------------------Decibels measure--------------------*/
        if (runner == null) {
            runner = new Thread(() -> {
                while (runner != null) {
                    try {
                        Thread.sleep(333);
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

    private void setToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Hola");
        }
    }

    private void launchLanguageFragment() {
        String lang = Locale.getDefault().getLanguage();
        Log.i("LANG", lang);
        BlankFragment blankFragment = new BlankFragment(lang);
        findViewById(R.id.txtWelcome).setVisibility(View.GONE);
        btnAbout.setVisibility(View.GONE);
        btnContact.setVisibility(View.GONE);
        btnInfo.setVisibility(View.GONE);
        btnServices.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left);
        fragmentManager.popBackStack();
        fragmentTransaction
                .replace(R.id.constraintLayout, blankFragment)
                .addToBackStack(null)
                .commit();
    }

    public void setLanguage(String lang){
        switch (lang) {
            case "French":
                context = LanguageManager.setLocale(MainActivity.this, "fr");
                resources = context.getResources();
                break;
            case "English":
                context = LanguageManager.setLocale(MainActivity.this, "en");
                resources = context.getResources();
                break;
            case "Spanish":
                context = LanguageManager.setLocale(MainActivity.this, "es");
                resources = context.getResources();
                break;
        }
        refreshLanguage();
        Toast.makeText(MainActivity.this, resources.getString(R.string.newLanguage), Toast.LENGTH_SHORT).show();
    }

    public void refreshLanguage() {
        txtWebsite.setText(resources.getString(R.string.yourStay));
        txtInfo.setText(resources.getString(R.string.about));
        txtEvents.setText(resources.getString(R.string.info));
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            constraintLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }*/

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
            } catch (java.io.IOException | java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "IOException: " +
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
        //SharedPreferences sp = this.getSharedPreferences("device-base", MODE_PRIVATE);
        //double amp = (double) sp.getFloat("amplitude", 0);
        /*---------------Decibels measure---------------*/
        //static final private double EMA_FILTER = 0.6;
        double mEMA = 0.0;
        double mEMAValue = EMA_FILTER * amplitude + (1.0 - EMA_FILTER) * mEMA;
        //Asumiendo que la presión de referencia mínima es 0.000085 Pascal
        // (en la mayoría de los teléfonos) es igual a 0 db
        // TODO: Find out the minimum reference in Motorola
        return 20 * (float) Math.log10((mEMAValue / 51805.5336) / 0.000028251);
    }

    public void measureDecibels() {
        double amplitude = mRecorder.getMaxAmplitude();
        if (amplitude > 0 && amplitude < 1000000) {
            decibels = convertDb(amplitude);
            if (decibels > 10) {
                //txtDecibels.setText(String.format(Locale.US,"%.2f", decibels));
                decibels = convertDb(mRecorder.getMaxAmplitude());
                if (decibelsHistory.size()>=12){
                    if (decibelMedia(decibelsHistory)>10){
                        //turnOffLight();
                        Log.i("dec", "turnOffLight: Aqui se apago la luz de sus ojos");
                        decibelsHistory.clear();
                    }
                    Log.i("dec", "pop: "+ decibelsHistory.remove(0));
                }
                Log.i("dec", "push: "+decibelsHistory.push(decibels));
            }
            //startCounting();
        }
    }

    private double decibelMedia(Stack<Double> decHist) {
        Double med = 0.00;
        for (int i = 0; i < decHist.size(); i++) {
            med += decHist.get(i);
            Log.i("dec", "media: "+med);
        }
        Log.i("dec", "decibelMedia: "+med/decHist.size());
        return Math.round(((med/decHist.size())*100d)/100d);
    }
    //startCounting(decibels);
    //hardNoise(decibels);

    /*public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;

    }*/

    /*public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }*/
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