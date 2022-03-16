package com.example.weatherdisplay;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    TextView locationTextView;

    static int weatherColumnsHeight;
    static int noOfDays = 3;
    static int noWeatherPoints; // Calculated from the number of days
    static int noWeatherPointsInOneScreen = 18;

    static int screenWidth;
    static int screenHeight;
    static int edgePadding; // Padding at the top and bottom of the screen

    double locLatitude;
    double locLongitude;
    static int dayBlue = Color.argb(255, 0, 170, 255);
    static int nightBlue = Color.argb(255, 4, 142, 233);
    static int separatorBlue = Color.argb(255, 0, 151, 237);

    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setBackgroundColor(dayBlue);

        locationTextView = findViewById(R.id.LocationTextView);
        locationTextView.setTextColor(Color.WHITE);
        weatherColumns = findViewById(R.id.WeatherColumns);
        FloatingActionButton refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(view -> {
            refreshWeatherData(false);
        });

        noWeatherPoints = noOfDays * 24;

        calculateAllDistances();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationData();
        //refreshWeatherData(false);
        startAutoRefresh();
    }

    private void calculateAllDistances() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        edgePadding = (int) Math.floor(screenHeight * 0.05);
        GetScrollbarHeight();
    }

    private void GetScrollbarHeight() {
        final ViewTreeObserver observer = weatherColumns.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(() -> weatherColumnsHeight = weatherColumns.getHeight());
    }


    private void refreshWeatherData(boolean getTestData) {
        Thread thread = new Thread(() -> {
            WeatherPoint.resetTemps();

            ArrayList<WeatherPoint> weatherPoints;
            if (getTestData) {
                weatherPoints = GetTestWeatherPoints();
            } else {
                //getLocationData();
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
                        locLatitude = location.getLatitude();
                        locLongitude = location.getLongitude();
                        LocationAddress.getAddressFromLocation(locLatitude, locLongitude, this, new GeocoderHandler());
                        refreshWeatherData(false);
                    }else{
                        noLocation();
                    }
                });

        return "?lat=" + locLatitude + "&lon=" + locLongitude;
        //return "Latitude: " + locLatitude + "\n" + "Longitude: " + locLongitude;
    }

    private void noLocation(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
            JSONArray timeSeries = jsonObject.getJSONObject("properties").getJSONArray("timeseries");
            for (int i = 0; i < noWeatherPoints; i++) {
                JSONObject timePoint = timeSeries.getJSONObject(i);
                String time = timePoint.getString("time");
                JSONObject data = timePoint.getJSONObject("data");
                int temperature = data.getJSONObject("instant").getJSONObject("details")
                        .getInt("air_temperature");

                if (data.has("next_1_hours")) {
                    String weatherIcon = data.getJSONObject("next_1_hours").getJSONObject("summary")
                            .getString("symbol_code");
                    WeatherPoint weatherPoint = new WeatherPoint(this, time, temperature, weatherIcon);
                    arrayWeatherPoints.add(weatherPoint);
                }
            }
            noWeatherPoints = arrayWeatherPoints.size();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayWeatherPoints;

    }

    private void generateWeatherColumns(ArrayList<WeatherPoint> weatherPoints) {
        weatherColumns.removeAllViews();
        for (int i = 0; i < noWeatherPoints; i++) {
            View hourSeparator = CreateHourSeparator(weatherPoints.get(i));
            weatherColumns.addView(hourSeparator);
            weatherPoints.get(i).generateTemperatureTextView();
            weatherColumns.addView(weatherPoints.get(i));
        }
    }

    private View CreateHourSeparator(WeatherPoint weatherPoint) {
        View hourSeparator = new View(this);
        hourSeparator.setLayoutParams(new LinearLayout.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT));
        int color = weatherPoint.displayedTime.equals("01") ? Color.WHITE : separatorBlue;
        hourSeparator.setBackgroundColor(color);
        return hourSeparator;
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
            weatherPoints.add(new WeatherPoint(this, "YYYY-MM-DDT" + hh + ":00", i, "cloudy"));
        }
        return weatherPoints;
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            locationTextView.setText(locationAddress);
        }
    }

    final Handler timerHandler = new Handler();
    Runnable updater;
    void startAutoRefresh() {
        updater = () -> {
            refreshWeatherData(false);
            int time = 1000*60*30; // every 30min
            timerHandler.postDelayed(updater,time);
        };
        timerHandler.post(updater);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updater);
    }
}