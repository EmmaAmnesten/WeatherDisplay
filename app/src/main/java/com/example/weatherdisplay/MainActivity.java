package com.example.weatherdisplay;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int i = 1;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    double locLatitude;
    double locLongitude;

    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HideStatusAndActionBar();

        FloatingActionButton refreshButton = (FloatingActionButton) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this.refreshOnCLickListener);

        locLatitude = 0;
        locLongitude = 0;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        GenerateWeatherColumns();
    }

    private void GenerateWeatherColumns() {
        // Make columns
        LinearLayout weatherColumns = (LinearLayout) findViewById(R.id.WeatherColumns);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        int[] colors = new int[] {
                Color.argb(255, 0, 0, 125),
                Color.argb(255, 0, 0, 150),
                Color.argb(255, 0, 0, 175),
                Color.argb(255, 0, 0, 200),
                Color.argb(255, 0, 0, 225),
                Color.argb(255, 0, 0, 250),
                Color.argb(255, 50, 50, 250),
                Color.argb(255, 100, 100, 250),
                Color.argb(255, 150, 150, 250),
                Color.argb(255, 200, 200, 250),
        };
        int noColumns = 10;
        for (int i = 0; i < noColumns; i++) {
            TextView view = new TextView(this);
            view.setBackgroundColor(colors[i]);
            view.setLayoutParams(new LinearLayout.LayoutParams(width/noColumns, LinearLayout.LayoutParams.MATCH_PARENT));
            view.setText(i + "C");
            view.setGravity(Gravity.CENTER_HORIZONTAL);
            view.setPadding(0, height/(noColumns+1)*(noColumns-i), 0, 0);
            weatherColumns.addView(view);
        }
    }

    private void HideStatusAndActionBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    View.OnClickListener refreshOnCLickListener = view -> {
        Thread thread = new Thread(() -> {
            Log.d(this.getClass().getName(), "You pressed on refresh!: ");
            GetLocationData();
            String response = MakeRequest("GET",
             "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=" + locLatitude +
                     "&lon=" + locLongitude);

            String weatherData = weatherPointsToString(parseTemperatures(response));

            runOnUiThread(() -> {
                i++;
                TextView data = findViewById(R.id.dataView);
                data.setText("Hello hello " + i + "\n" + weatherData);
            });

        });
        thread.start();
    };

    private String weatherPointsToString(ArrayList<WeatherPoint> weatherPoints){
        StringBuilder stringBuilder = new StringBuilder();
        for ( WeatherPoint weatherPoint : weatherPoints) {
            stringBuilder.append(weatherPoint.toString() + "\n");

        }
        return stringBuilder.toString();
    }


    private ArrayList<WeatherPoint> parseTemperatures(String weatherResponse){
        ArrayList<WeatherPoint> arrayWeatherPoints = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(weatherResponse);
            JSONArray timeseries = jsonObject.getJSONObject("properties").getJSONArray("timeseries");

            for (int i = 0 ; i < timeseries.length() ; i++) {
                JSONObject timePoint = timeseries.getJSONObject(i);
                String time = timePoint.getString("time");
                int temperature = timePoint.getJSONObject("data").getJSONObject("instant")
                        .getJSONObject("details").getInt("air_temperature");
                WeatherPoint weatherPoint = new WeatherPoint(time, temperature);
                arrayWeatherPoints.add(weatherPoint);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayWeatherPoints;

    }

    private String GetLocationData() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "We need your location to give you weather";
        }

        mFusedLocationClient.getCurrentLocation(100, cancellationToken)
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    Log.d(this.getClass().getName(), "GetLocationData: Location is not null");
                    locLatitude = location.getLatitude();
                    locLongitude = location.getLongitude();
                } else {
                    Log.d(this.getClass().getName(), "GetLocationData: Location is null");
                }
            });

        return "Latitude: " + locLatitude + "\n" +
                "Longitude: " + locLongitude;
    }

    CancellationToken cancellationToken = new CancellationToken() {
        @Override
        public boolean isCancellationRequested() {
            return false;
        }

        @Override
        public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
            return null;
        }
    };

    private String MakeRequest(String method, String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("User-Agent", "WeatherDisplay/0.5 https://github.com/jeppei/WeatherDisplay");

            int statusCode = connection.getResponseCode();
            InputStream responseStream;
            if (statusCode < 200 || 299 < statusCode) {
                responseStream = connection.getErrorStream();
            } else {
                responseStream = connection.getInputStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(responseStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();
            connection.disconnect();
            return response.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }
}