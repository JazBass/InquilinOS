package com.mycityhome.InquilinOs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    LatLng ubicationMadrid = new LatLng(40.4198524186511, -3.702922076154419),
     plazaEspania = new LatLng(40.423385, -3.712180),
     catedralSantaMaria = new LatLng(40.415651, -3.714552),
     palacioReal = new LatLng(40.417955, -3.714311999999999),
     puertaAlcala = new LatLng(40.419992, -3.688737),
     cibeles = new LatLng(40.41933, -3.69308),
     puertaSol = new LatLng(40.4169473, -3.7035285),
     elRetiro = new LatLng(40.4152606, -3.6844995),
     sanJeronimo = new LatLng(40.4145455, -3.691040100000001),
     elPrado = new LatLng(40.4137818, -3.6921271),
     reinaSofia = new LatLng(40.40791229999999, -3.6945569),
     museoThyssen = new LatLng(40.41604059999999, -3.694925399999999),
     plazaToros = new LatLng(40.30912920000001, -3.0107693),
     plazaMayor = new LatLng(40.415511, -3.707400899999999),
     mercadoSanMiguel = new LatLng(40.4153794, -3.708969699999999),
     plazaVilla = new LatLng(40.4153651, -3.7105331),
     temploDebod = new LatLng(40.4240216, -3.717769499999999);

    LocationsMadrid[] locationsMadrid = new LocationsMadrid[]{
            new LocationsMadrid("Plaza España", plazaEspania),
            new LocationsMadrid("Catedral Santa María", catedralSantaMaria),
            new LocationsMadrid("Palacio Real", palacioReal),
            new LocationsMadrid("Museo del Prado", elPrado),
            new LocationsMadrid("Iglesia San Jerónimo", sanJeronimo),
            new LocationsMadrid("Parque del Retiro", elRetiro),
            new LocationsMadrid("Puerta del Sol", puertaSol),
            new LocationsMadrid("Cibeles", cibeles),
            new LocationsMadrid("Puerta de Alcalá", puertaAlcala),
            new LocationsMadrid("Templo Debod", temploDebod),
            new LocationsMadrid("Plaza Villa", plazaVilla),
            new LocationsMadrid("Mercado San Miguel", mercadoSanMiguel),
            new LocationsMadrid("Plaza Mayot", plazaMayor),
            new LocationsMadrid("Plaza de Toros", plazaToros),
            new LocationsMadrid("Museo Thyssen-Bornemisza", museoThyssen),
            new LocationsMadrid("Museo Reina Sofía", reinaSofia)
    };

    String[] Permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private boolean isCustom;
    private Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapView);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        /*Take the extra, if comes from event we take de event location and isCustom=true*/
        Intent i = getIntent();
        if (i.getStringExtra("kind").equals("custom")){
            isCustom = true;
            event = (Event) i.getSerializableExtra("ListElement");
        }else isCustom=false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.custom_map)
        );
        googleMap.setOnInfoWindowClickListener(this);
        CameraPosition position = new CameraPosition.Builder()
                .target(ubicationMadrid)
                .zoom(14)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        //Asking for permissions
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapActivity.this,
                        Permissions, 1);
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        /*If isCustom this activity is started from EventsActivity, so we take event's location for mark*/
        if (isCustom){
            LatLng mLocation = new LatLng(Double.parseDouble(event.getLatitude()),Double.parseDouble(event.getLongitude()));
            MarkerOptions mark = new MarkerOptions()
                    .position(mLocation)
                    .title(event.getName());
            googleMap.addMarker(mark);
        }
        /*If isn't Custom, we mark all the locations saved in LocationsMadrid*/
        else {
            for (LocationsMadrid location :
                    locationsMadrid) {
                MarkerOptions mark = new MarkerOptions()
                        .position(location.getLocation())
                        .title(location.getName());
                googleMap.addMarker(mark);
            }
        }
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

    }
}
