package com.example.pollutionindicator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

import org.json.JSONArray;
import org.json.JSONObject;

import static android.provider.UserDictionary.Words.APP_ID;

public class MainActivity extends AppCompatActivity {
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
        locations = findViewById(R.id.locations);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocx = location.getLatitude();
                currentLocy = location.getLongitude();

                String url = "https://www.airnowapi.org/aq/observation/latLong/current/?format=application/json&latitude="+currentLocx+"&longitude="+currentLocy+"&distance=25&API_KEY=D37868FB-D5AD-4A2D-B055-490AA645ECA6";

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    double sum = 0;
                                    for(int i = 0; i < 3; i++) {
                                        sum += Integer.parseInt(response.getJSONObject(i).get("AQI").toString());
                                    }

                                    sum /= 3;

                                    locations.setText("Average AQI: " + sum);

                                    View someView = findViewById(R.id.coordinatorLayout);
                                    if(sum > 0) { //Good
                                        someView.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                                        System.out.println("oof");
                                    } if (sum > 50) { //Moderate
                                        someView.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                                    } if (sum > 100) { //Unhealthy for Sensitive Groups
                                        someView.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                                    } if (sum > 150) { //Unhealthy
                                        someView.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                    } if (sum > 200) { //Very Unhealthy
                                        someView.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                                    } if (sum > 300) { //Hazardous
                                        someView.setBackgroundColor(getResources().getColor(R.color.colorMaroon));
                                    }


                                } catch(Exception e) {

                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                System.out.println(error);

                            }
                        });

                // Access the RequestQueue through your singleton class.
                MySingleton.getInstance(getBaseContext()).addToRequestQueue(jsonObjectRequest);
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

        locationManager.requestLocationUpdates("gps", 60000, 0, locationListener);

    }

    int count = 0;

    private void configureButtons() {

        /*
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

                String url = "https://www.airnowapi.org/aq/observation/zipCode/current/?format=application/json&zipCode=40207&distance=25&API_KEY=D37868FB-D5AD-4A2D-B055-490AA645ECA6";

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    double sum = 0;
                                    for(int i = 0; i < 3; i++) {
                                        sum += Integer.parseInt(response.getJSONObject(i).get("AQI").toString());
                                    }

                                    sum /= 3;

                                    locations.setText("Average AQI: " + sum);
                                } catch(Exception e) {

                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                System.out.println(error);
                            }
                        });

                // Access the RequestQueue through your singleton class.
                MySingleton.getInstance(view.getContext()).addToRequestQueue(jsonObjectRequest);

            }
        });
        */
    }

    public void openLink(View view) {
        Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
        openURL.setData(Uri.parse("https://airnow.gov/index.cfm?action=aqi_brochure.index"));
        startActivity(openURL);
    }
}