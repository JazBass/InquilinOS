package com.mycityhome.InquilinOs.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.mycityhome.InquilinOs.R;

public class TurismActivity extends AppCompatActivity {

    CardView btnEvents, btnMap;
    Toolbar toolbar;

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
