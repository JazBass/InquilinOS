package com.mycityhome.InquilinOs;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {


    private List<Event> listEvents;
    ProgressBar progressBar;
    TextView txtEvents, txtWait;
    RecyclerView recyclerView;
    String TAG = "json";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.my_toolbar_2);
        materialToolbar.setTitle("hola");
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        txtEvents = findViewById(R.id.txtEvents);
        txtWait = findViewById(R.id.txtWait);
        txtWait.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.INVISIBLE);
        new JsonTask().execute();
    }
    private class JsonTask extends AsyncTask<Void, Void, String> {

        private String name, longitude, latitude, eventUrl, description,dtStart,dtEnd,stHour,endHour,
                locality,streetAddress,postalCode,price;
        private String[] myAddress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
    //La puta que te pario Github de mierda
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                final String url = "https://tcpmch.herokuapp.com/events";
                final URL myUrl = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
                InputStreamReader streamReader = new InputStreamReader(urlConnection.getInputStream());
                listEvents = readJsonStream(streamReader);
            } catch (IOException malformedURLException) {
                malformedURLException.printStackTrace();
            }
            return null;
        }

        public List<Event> readJsonStream(InputStreamReader streamReader) throws IOException {
            try (JsonReader reader = new JsonReader(streamReader)) {
                return readEventArray(reader);
            }
        }

        public List<Event> readEventArray(JsonReader reader) throws IOException {
            List<Event> events = new ArrayList<>();
            reader.beginArray();
            while (reader.hasNext()) {
                events.add(readEvent(reader));
            }
            reader.endArray();
            return events;
        }

        public Event readEvent(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                String line = reader.nextName();
                switch (line) {
                    case "title":
                        name = reader.nextString();
                        break;
                    case "description":
                        description = reader.nextString();
                        break;
                    case "startDate":
                        dtStart = reader.nextString();
                        break;
                    case "endDate":
                        dtEnd = reader.nextString();
                        break;
                    case "startHour":
                        stHour = reader.nextString();
                        break;
                    case "endHour":
                        endHour = reader.nextString();
                        break;
                    case "price":
                        price = reader.nextString();
                        break;
                    case "link":
                        eventUrl = reader.nextString();
                        break;
                    case "latitude":
                        latitude = reader.nextString();
                        break;
                    case "longitude":
                        longitude = reader.nextString();
                        break;
                    case "address":
                        streetAddress = readArea(reader);
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
            return new Event(name, description, eventUrl, myAddress, dtStart, dtEnd, stHour,
                    endHour, latitude, longitude, price, streetAddress);
        }

        private String readArea(JsonReader reader) throws IOException {
            String address ="";
            reader.beginObject();
            while (reader.hasNext()) {
                String line = reader.nextName();
                if (line.equals("area"))
                        address = readAddress(reader);
                else
                    reader.skipValue();
            }
            Log.i(TAG, "streetAddress: " + streetAddress);
            reader.endObject();
            return address;
        }

        private String readAddress(JsonReader reader) throws IOException {
            String address = "";
            reader.beginObject();
            while (reader.hasNext()) {
                String line = reader.nextName();
                if (line.equals("street-address"))
                    address = reader.nextString();
                else
                    reader.skipValue();
            }
            reader.endObject();
            return address;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            init();
        }
    }

    public void init() {
        progressBar.setVisibility(View.INVISIBLE);
        txtWait.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        ListAdapter listAdapter = new ListAdapter(listEvents, this, this::moveToMap, this::goToWebsite);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void moveToMap(Event event) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("kind", "custom");
        intent.putExtra("ListElement", (Serializable) event);
        startActivity(intent);
    }

    private void goToWebsite(Event event){
        Intent intent = new Intent(this, WebsiteActivity.class);
        intent.putExtra("url",event.getUrl());
        Log.i("url", "event " + event.getUrl());
        startActivity(intent);
    }
}
