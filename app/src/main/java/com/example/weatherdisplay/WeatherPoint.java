package com.example.weatherdisplay;

import static com.example.weatherdisplay.MainActivity.noWeatherPoints;
import static com.example.weatherdisplay.MainActivity.screenHeight;
import static com.example.weatherdisplay.MainActivity.screenWidth;
import static com.example.weatherdisplay.MainActivity.edgePadding;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherPoint extends LinearLayout {

    public String time;
    public int temperature;
    private TextView temperatureTextView;

    static int minTemp = 100000;
    static int maxTemp = -100000;
    static int tempSpan;

    public WeatherPoint(Context context, String time, int temperature){
        super(context);

        this.time = time;
        this.temperature = temperature;

        minTemp = Math.min(minTemp, temperature);
        maxTemp = Math.max(maxTemp, temperature);
        tempSpan = maxTemp-minTemp;

        setOrientation(VERTICAL);
        setBackgroundColor(Color.BLUE);
        LayoutParams lp = new LayoutParams(
                MainActivity.screenWidth/MainActivity.noColumnsInOneScreen,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        setLayoutParams(lp);

        /**
        Log.d("WeatherPoint", "MainActivity.screenHeight: " + MainActivity.screenHeight);
        Log.d("WeatherPoint", "MainActivity.edgePadding: " + MainActivity.edgePadding);
        Log.d("WeatherPoint", "tempArea: " + tempArea);
        Log.d("WeatherPoint", "tempSpan: " + tempSpan);
        Log.d("WeatherPoint", "singleTempHeight: " + singleTempHeight);
        Log.d("WeatherPoint", "temperature: " + temperature);
        Log.d("WeatherPoint", "minTemp: " + minTemp);
        Log.d("WeatherPoint", "maxTemp: " + maxTemp);
        Log.d("WeatherPoint", "height: " + height);
        Log.d("WeatherPoint", "");
         **/
        //setPadding(0, height, 0, 0);
    }

    public void generateTemperatureTextView() {
        temperatureTextView = new TextView(this.getContext());
        temperatureTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        temperatureTextView.setText(getTemperatureAsString());
        temperatureTextView.setBackgroundColor(Color.GREEN);
        this.addView(temperatureTextView);

        int middle = screenHeight / 2;

        int tempArea = screenHeight - 2 * edgePadding;
        int a = temperatureTextView.getMeasuredHeight();
        int singleTempHeight = Math.min(tempArea / tempSpan, 20);
        int lower = middle - singleTempHeight * noWeatherPoints / 2;
        int posY = screenHeight - (singleTempHeight * (temperature-minTemp) + edgePadding + lower);
        temperatureTextView.setY(posY);
    }

    public String getTemperatureAsString(){
        return temperature + "°";
    }

    public String toString(){

        return time + " - " + temperature;
    }

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
}
