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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import static android.provider.UserDictionary.Words.APP_ID;

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

        String url = "https://data.louisvilleky.gov/api/action/datastore/search.json?resource_id=8150a3fd-cd95-4ebc-b59e-4fd5d94e2886&limit=10";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //latView.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

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
        