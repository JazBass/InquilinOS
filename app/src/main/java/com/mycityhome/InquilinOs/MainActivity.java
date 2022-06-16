package com.mycityhome.InquilinOs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    Toolbar toolbar;
    LanguageManager languageManager;
    Button btnContact, btnInfo, btnAbout, btnServices;
    String language;
    Recorder recorder;
    int PERMISSIONS_CODE = 1;
    String[] Permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        navigationView = findViewById(R.id.navigationView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        btnContact = findViewById(R.id.btnContact);
        btnAbout = findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnServices = findViewById(R.id.btnServices);
        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder = new Recorder(MainActivity.this);
                recorder.start();
            }
        });
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/34633335208?text=Hola"));
                startActivity(i);
            }
        });
        setToolBar();
        setListener();
        if (!hasPermissions(Permissions)) {
            requestPermissions();
        }
    }

    /*Barra superior con menu desplegable para cambiar idiomas*/
    private void setListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.french:
                        language = "fr";
                        break;
                    case R.id.english:
                        language = "en";
                        break;
                    case R.id.spanish:
                        language = "es";
                        break;
                }
                languageManager.updateLanguge(language);
                return false;
            }
        });
    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //TODO: mejorar el tamaño del logo de idioma
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setLogo(R.drawable.logomch);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_lang);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
    //TODO: Cambio de string utilizado con distintos idiomas!

    private boolean hasPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    private void requestPermissions() {
        EasyPermissions.requestPermissions(this, "Esta aplicación necestita este " +
                        "permiso para funcionar correctamente", PERMISSIONS_CODE,
                Manifest.permission.RECORD_AUDIO);
        EasyPermissions.requestPermissions(this, "Aceptar este permiso", PERMISSIONS_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**************Solicitando Permisos**************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permiso Aceptado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestPermissions();
        }
    }
}