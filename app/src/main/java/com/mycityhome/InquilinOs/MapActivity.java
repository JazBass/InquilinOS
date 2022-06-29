package com.mycityhome.InquilinOs;

import android.Manifest;
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

    LatLng ubicationMadrid = new LatLng(40.4198524186511, -3.702922076154419);
    LatLng plazaEspaña = new LatLng(40.423385, -3.712180);
    LatLng catedralSantaMaria = new LatLng(40.415651, -3.714552);
    LatLng palacioReal = new LatLng(40.417955, -3.714311999999999);
    LatLng puertaAlcala = new LatLng(40.419992, -3.688737);
    LatLng cibeles = new LatLng(40.41933, -3.69308);
    LatLng puertaSol = new LatLng(40.4169473, -3.7035285);
    LatLng elRetiro = new LatLng(40.4152606, -3.6844995);
    LatLng sanJeronimo = new LatLng(40.4145455, -3.691040100000001);
    LatLng elPrado = new LatLng(40.4137818, -3.6921271);
    LatLng reinaSofia = new LatLng(40.40791229999999, -3.6945569);
    LatLng museoThyssen = new LatLng(40.41604059999999, -3.694925399999999);
    LatLng plazaToros = new LatLng(40.30912920000001, -3.0107693);
    LatLng plazaMayor = new LatLng(40.415511, -3.707400899999999);
    LatLng mercadoSanMiguel = new LatLng(40.4153794, -3.708969699999999);
    LatLng plazaVilla = new LatLng(40.4153651, -3.7105331);
    LatLng temploDebod = new LatLng(40.4240216, -3.717769499999999);

    LocationsMadrid[] locationsMadrid = new LocationsMadrid[]{
            new LocationsMadrid("String", plazaEspaña),
            new LocationsMadrid("catedralSantaMaria", catedralSantaMaria),
            new LocationsMadrid("palacioReal", palacioReal),
            new LocationsMadrid("elPrado", elPrado),
            new LocationsMadrid("sanJeronimo", sanJeronimo),
            new LocationsMadrid("elRetiro", elRetiro),
            new LocationsMadrid("puertaSol",puertaSol),
            new LocationsMadrid("cibeles", cibeles),
            new LocationsMadrid("puertaAlcala", puertaAlcala),
            new LocationsMadrid("catedralSantaMaria", catedralSantaMaria),
            new LocationsMadrid("temploDebod", temploDebod),
            new LocationsMadrid("plazaVilla", plazaVilla),
            new LocationsMadrid("mercadoSanMiguel", mercadoSanMiguel),
            new LocationsMadrid("plazaMayor", plazaMayor),
            new LocationsMadrid("plazaToros", plazaToros),
            new LocationsMadrid("museoThyssen", museoThyssen),
            new LocationsMadrid("reinaSofia", reinaSofia)
    };

    String[] Permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapView);
        assert mapFragment != null;
        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.custom_map)
        );
        googleMap.setOnInfoWindowClickListener(this);
        CameraPosition position = new CameraPosition.Builder()
                .target(ubicationMadrid)
                .zoom(16)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
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
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        MarkerOptions mark =new MarkerOptions()
                .position(plazaEspaña)
                .title(String.valueOf(R.string.plazaEspania));
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

    }

}
