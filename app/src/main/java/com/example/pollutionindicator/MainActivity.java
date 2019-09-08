package com.example.pollutionindicator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private TextView textView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button recorder;
    private TextView locations;

    private double currentLocx;
    private double currentLocy;

    private double loc1x;
    private double loc1y;
    private double loc2x;
    private double loc2y;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.button);
        textView = findViewById(R.id.startNoti);
        recorder = findViewById(R.id.recorder);
        locations = findViewById(R.id.locations);




        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                textView.setText("It started!");
                currentLocx = location.getLatitude();
                currentLocy = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);

                return;
            } else {
                configureButtons();
            }
        }



    }
    int count = 0;
    private void configureButtons() {
        start.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                locationManager.requestLocationUpdates("gps", 5, 0, locationListener);
            }
        });

        recorder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                count++;
                if (count == 1) {
                    loc1x = currentLocx;
                    loc1y = currentLocy;
                    locations.setText("\n" + loc1x + ", " + loc1y);

                } else {
                    loc2x = currentLocx;
                    loc2y = currentLocy;
                    locations.append("\n" + loc2x + ", " + loc2y);
                    locations.append("\ndifference: " + (loc2x - loc1x) + ", " + (loc2y - loc1y));
                }
            }
        });

    }

}