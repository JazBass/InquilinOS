package com.mycityhome.InquilinOs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private List <Event> listEvents;
    ProgressBar progressBar;
    TextView txtEvents;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        txtEvents = findViewById(R.id.txtEvents);
        txtEvents.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.INVISIBLE);
        new JsonTask().execute();
    }

    private class JsonTask extends AsyncTask<Void, Void, String> {

        private final String url = "https://tcpmch.herokuapp.com/events";
        private String name, description, eventUrl, dtStart, dtEnd,
                stHour, endHour, latitude, longitude, locality, streetAddress, postalCode, price;
        private String[] myAdress;
        private LatLng location;
        private List<Event> events;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL myUrl = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
                InputStreamReader streamReader = new InputStreamReader(urlConnection.getInputStream());
                listEvents = readJsonStream(streamReader);
            } catch (IOException malformedURLException) {
                malformedURLException.printStackTrace();
            }
            return null;
        }

        public List<Event> readJsonStream(InputStreamReader streamReader) throws IOException {
            JsonReader reader = new JsonReader(streamReader);
            try {
                return readEventArray(reader);
            } finally {
                reader.close();
            }
        }

        public List<Event> readEventArray(JsonReader reader) throws IOException {
            events = new ArrayList<Event>();
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
                    case "url":
                        eventUrl = reader.nextString();
                        break;
                    case "latitude":
                        latitude = reader.nextString();
                        break;
                    case "longitude":
                        longitude = reader.nextString();
                        break;
                    case "address":
                        myAdress = readAddress(reader);
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
            location = new LatLng(30.434324, 34.342432);
            return new Event(name, description, url, myAdress, dtStart, dtEnd, stHour,
                    endHour, location, price);
        }

        private String[] readAddress(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                String line = reader.nextName();
                if (line.equals("locality")) {
                    locality = reader.nextString();
                } else if (line.equals("street-address")) {
                    streetAddress = reader.nextString();
                } else if (line.equals("postal-code")) {
                    postalCode = reader.nextString();
                } else reader.skipValue();
            }
            reader.endObject();
            return new String[]{streetAddress, locality, postalCode};
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            init();
            Log.i("json", "Se han guardado " + events.size() + "eventos");
        }
    }

    public void init(){
        progressBar.setVisibility(View.INVISIBLE);
        txtEvents.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        ListAdapter listAdapter = new ListAdapter((listEvents), this, new ListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Event item) {}
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }

}
