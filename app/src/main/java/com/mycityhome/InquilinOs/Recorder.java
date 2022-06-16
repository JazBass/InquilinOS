package com.mycityhome.InquilinOs;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.text.DecimalFormat;

public class Recorder extends Thread {
    MediaRecorder recorder;
    double decibels;
    private static double ambiente = 0.0;
    static final private double filtro = 0.6;
    Activity activity;

    public Recorder(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        recorder = new MediaRecorder();
        //Origen del medio(micro)
        recorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(96000);
        //Formato de salida
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //CODEC de audio utilizado
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //Sin file de salida
        recorder.setOutputFile("/dev/null");
        //Iniciamos la grabacion
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        soundDb(20);
        highVolume();
    }

    public void highVolume() {
        DecimalFormat df1 = new DecimalFormat("####.0");
        double decib = soundDb(20);
         if (decib > 30 && decib < 70) {
            launchNotification(String.valueOf(decib));
        }else if (decib > 70){
             launchNotification(String.valueOf(decib));
         }
        Log.i("decib", "launchNotification: "+decibels);
    }

    public void launchNotification(String decibels){
        String channelID = "decibeles";
        NotificationChannel notificationChannel = new NotificationChannel(channelID,
                "Mucho Ruido", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = (NotificationManager) activity.getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(activity.getApplicationContext(), channelID)
                .setChannelId(channelID)
                .setSmallIcon(R.drawable.ic_mycity_background)
                .setContentText("more than 30 decibeles");
        manager.notify(1, builder.build());
    }

    //Medidores de sonido ambiente y presion para mayor exactitud en el calculo
    public double soundDb(double ampl) {
        double dbSPL = 20 * Math.log10(soundPressure() / ampl);
        if (dbSPL < 0) {
            return 0;
        } else {
            return dbSPL;
        }
    }

    public double soundPressure() {
        int amplitude = recorder.getMaxAmplitude();
        Log.i("decib", "ambientSound: "+ recorder.getMaxAmplitude());
        return 20 * Math.log10((double)Math.abs(amplitude));
    }

    public double ambientSound() {
        if (recorder != null) {
            Log.i("decib", "ambientSound: "+ recorder.getMaxAmplitude());
            return (recorder.getMaxAmplitude());
        }else
            return 0;
    }
}
