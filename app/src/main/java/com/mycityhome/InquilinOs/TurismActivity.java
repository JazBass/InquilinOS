package com.mycityhome.InquilinOs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TurismActivity extends AppCompatActivity {

    Button btnEvents, btnMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turism);
        btnEvents = findViewById(R.id.btnEvents);
        btnEvents.setOnClickListener(view ->{
            Intent i = new Intent(TurismActivity.this, EventsActivity.class);
            startActivity(i);
        });
        btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(view -> {
            Intent i = new Intent(TurismActivity.this, MapActivity.class);
            i.putExtra("kind", "common");
            startActivity(i);
        });
    }


}
