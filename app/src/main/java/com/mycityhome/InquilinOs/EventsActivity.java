package com.mycityhome.InquilinOs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        new JsonTask().execute();
    }

    class JsonTask extends AsyncTask<Void, Void, String> {

        String url = "https://tcpmch.herokuapp.com/events";
        String name, description, eventUrl, dtStart, dtEnd,
                stHour, endHour, latitude, longitude, locality, streetAddress, postalCode, price;
        String[] myAdress;
        LatLng location;
        List<Event> events;

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
                readJsonStream(streamReader);
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
                if (line.equals("title")) {
                    name = reader.nextString();
                } else if (line.equals("description")) {
                    description = reader.nextString();
                } else if (line.equals("startDate")) {
                    dtStart = reader.nextString();
                } else if (line.equals("endDate")) {
                    dtEnd = reader.nextString();
                } else if (line.equals("startHour")) {
                    stHour = reader.nextString();
                } else if (line.equals("endHour")) {
                    endHour = reader.nextString();
                } else if (line.equals("price")) {
                    price = reader.nextString();
                } else if (line.equals("url")) {
                    eventUrl = reader.nextString();
                } else if (line.equals("latitude")) {
                    latitude = reader.nextString();
                } else if (line.equals("longitude")) {
                    longitude = reader.nextString();
                } else if (line.equals("address")) {
                    myAdress = readAddress(reader);
                } else {
                    reader.skipValue();
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
            showList();
            Log.i("json", "Se han guardado " + events.size() + "eventos");
        }
    }

    public void showList() {

    }

}
