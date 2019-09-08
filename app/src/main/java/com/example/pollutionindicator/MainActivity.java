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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.provider.UserDictionary.Words.APP_ID;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button mode;
    private TextView locations;

    private double currentLocx;
    private double currentLocy;

    private double loc1x;
    private double loc1y;
    private double loc2x;
    private double loc2y;

    int choice = 0;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locations = findViewById(R.id.locations);
        mode = findViewById(R.id.mode);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (choice == 0) {
                    currentLocx = location.getLatitude();
                    currentLocy = location.getLongitude();

                    String url = "https://www.airnowapi.org/aq/observation/latLong/current/?format=application/json&latitude=" + currentLocx + "&longitude=" + currentLocy + "&distance=25&API_KEY=D37868FB-D5AD-4A2D-B055-490AA645ECA6";

                    JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        double sum = 0;
                                        for (int i = 0; i < 3; i++) {
                                            sum += Integer.parseInt(response.getJSONObject(i).get("AQI").toString());
                                        }

                                        sum /= 3;

                                        locations.setText("Average AQI: " + sum);

                                        View someView = findViewById(R.id.coordinatorLayout);
                                        if (sum > 0) { //Good
                                            someView.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                                            System.out.println("oof");
                                        }
                                        if (sum > 50) { //Moderate
                                            someView.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                                        }
                                        if (sum > 100) { //Unhealthy for Sensitive Groups
                                            someView.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                                        }
                                        if (sum > 150) { //Unhealthy
                                            someView.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                        }
                                        if (sum > 200) { //Very Unhealthy
                                            someView.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                                        }
                                        if (sum > 300) { //Hazardous
                                            someView.setBackgroundColor(getResources().getColor(R.color.colorMaroon));
                                        }


                                    } catch (Exception e) {

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
                } else if (choice == 1) {

                    currentLocx = location.getLatitude();
                    currentLocy = location.getLongitude();

                    String url = "https://data.louisvilleky.gov/api/action/datastore/search.json?resource_id=41e007f7-d755-4f02-b9fe-4b8454c44624&limit=100&offset=0";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    System.out.println("Ummm");
                                    try {
                                        double sum = 0;
                                        double lat;
                                        double lon;
                                        double distAvg = 0;
                                        for (int i = 0; i < 100; i++) {
                                            if (i != 74) {
                                                //System.out.println(i);
                                                lat = Double.parseDouble(response.getJSONObject("result").getJSONArray("records").getJSONObject(i).getString("lat"));
                                                lon = Double.parseDouble(response.getJSONObject("result").getJSONArray("records").getJSONObject(i).getString("long_"));
                                                //System.out.println(lat);
                                                distAvg += Math.hypot(lat - currentLocx, lon - currentLocy);
                                            }
                                        }
                                        distAvg /= 100;
                                        System.out.println(response.getJSONObject("result").getJSONArray("records").getJSONObject(74).getString("lat") == null);
                                        locations.setText("Average distance from 311 call: " + degreesToMiles(distAvg));



                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    System.out.println(error);

                                }
                            });
                    MySingleton.getInstance(getBaseContext()).addToRequestQueue(jsonObjectRequest);


                }

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
        locationManager.requestLocationUpdates("gps",7200,0,locationListener);
    }

    public static double degreesToMiles(double degrees) {
        return degrees * 69.169444;
    }

    private String getRequest(String url) throws Exception {

        final URL obj;
            obj = new URL(url);
            final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        if (con.getResponseCode() != 200) {
            return null;

        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    int count = 0;

    public void yo(View view) {
        if (choice == 0) {
            choice = 1;
            View someView = findViewById(R.id.coordinatorLayout);
            someView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            locations.setText("Loading Position...");
            mode.setText("AQI");
        } else if (choice == 1) {
            choice = 0;
            locations.setText("Loading Position...");
            mode.setText("311");
        }
    }

    public void configureButtons() {

    }
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


    public void openLink(View view) {
        Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
        openURL.setData(Uri.parse("https://airnow.gov/index.cfm?action=aqi_brochure.index"));
        startActivity(openURL);
    }
}