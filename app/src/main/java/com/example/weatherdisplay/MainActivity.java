package com.example.weatherdisplay;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    LinearLayout weatherColumns;
    static int weatherColumnsHeight;
    static int noOfDays = 3;
    static int noWeatherPoints; // Calculated from the number of days
    static int noWeatherPointsInOneScreen = 18;

    static int screenWidth;
    static int screenHeight;
    static int edgePadding; // Padding at the top and bottom of the screen

    double locLatitude = 0;
    double locLongitude = 0;

    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherColumns = (LinearLayout) findViewById(R.id.WeatherColumns);
        FloatingActionButton refreshButton = (FloatingActionButton) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(view -> { refreshWeatherData(false); } );

        noWeatherPoints = noOfDays * 24;

        calculateAllDistances();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        refreshWeatherData(true);
    }

    private void calculateAllDistances() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        edgePadding = (int)Math.floor(screenHeight * 0.05);
        GetScrollbarHeight();
    }

    private void GetScrollbarHeight() {
        final ViewTreeObserver observer= weatherColumns.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(() -> weatherColumnsHeight = weatherColumns.getHeight());
    }

    private void refreshWeatherData(boolean getTestData) {
        Thread thread = new Thread(() -> {
            WeatherPoint.resetTemps();

            ArrayList<WeatherPoint> weatherPoints;
            if (getTestData) {
                weatherPoints = GetTestWeatherPoints();
            } else {
                getLocationData();
                String url = "https://api.met.no/weatherapi/locationforecast/2.0/compact";
                String query = "?lat=" + locLatitude + "&lon=" + locLongitude;
                String response = makeRequest("GET",url + query);
                weatherPoints = parseTemperatures(response);
            }

            runOnUiThread(() -> { generateWeatherColumns(weatherPoints); });
        });
        thread.start();
    }

    private String getLocationData() {

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

    private String makeRequest(String method, String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("User-Agent", "WeatherDisplay/0.5 https://github.com/EmmaAmnesten/WeatherDisplay");

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
    private ArrayList<WeatherPoint> parseTemperatures(String weatherResponse){
        ArrayList<WeatherPoint> arrayWeatherPoints = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(weatherResponse);
            JSONArray timeseries = jsonObject.getJSONObject("properties").getJSONArray("timeseries");
            for (int i = 0; i < noWeatherPoints; i++) {
                JSONObject timePoint = timeseries.getJSONObject(i);
                String time = timePoint.getString("time");
                int temperature = timePoint.getJSONObject("data").getJSONObject("instant")
                        .getJSONObject("details").getInt("air_temperature");
                WeatherPoint weatherPoint = new WeatherPoint(this, time, temperature);
                arrayWeatherPoints.add(weatherPoint);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayWeatherPoints;

    }

    private void generateWeatherColumns(ArrayList<WeatherPoint> weatherPoints) {
        weatherColumns.removeAllViews();
        for (int i = 0; i < noWeatherPoints; i++) {
            weatherPoints.get(i).generateTemperatureTextView();
            weatherColumns.addView(weatherPoints.get(i));
        }
    }

    private String weatherPointsToString(ArrayList<WeatherPoint> weatherPoints){
        StringBuilder stringBuilder = new StringBuilder();
        for ( WeatherPoint weatherPoint : weatherPoints) {
            stringBuilder.append(weatherPoint.toString() + "\n");

        }
        return stringBuilder.toString();
    }

    private ArrayList<WeatherPoint> GetTestWeatherPoints() {
        ArrayList<WeatherPoint> weatherPoints = new ArrayList<WeatherPoint>();
        for (int i = 0; i < noWeatherPoints; i++) {
            String hh = String.format("%02d", i);
            weatherPoints.add(new WeatherPoint(this, "YYYY-MM-DDT" + hh + ":00", i));
        }
        return weatherPoints;
    }
}