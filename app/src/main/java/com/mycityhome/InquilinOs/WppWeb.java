package com.mycityhome.InquilinOs;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WppWeb extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wppweb);
        WebView wv = (WebView) findViewById(R.id.vw);
        wv.loadUrl("https://wa.me/34633335208?text=Hola");
    }
}
