package com.example.weatherdisplay;

import static com.example.weatherdisplay.MainActivity.skyBlue;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherPoint extends LinearLayout {

    public String time;
    public String displayedTime;
    public int temperature;
    public String weatherIcon;
    private TextView temperatureTextView;
    private TextView timeTextView;

    static int minTemp = 100000;
    static int maxTemp = -100000;
    static int tempSpan;

    public WeatherPoint(Context context, String time, int temperature, String weatherIcon){
        super(context);

        this.temperature = temperature;
        this.weatherIcon = weatherIcon;
        this.time = time;
        displayedTime = time.substring(11, 13);

        minTemp = Math.min(minTemp, temperature);
        maxTemp = Math.max(maxTemp, temperature);
        tempSpan = maxTemp - minTemp;

        setOrientation(VERTICAL);
        LayoutParams lp = new LayoutParams(
                MainActivity.screenWidth/MainActivity.noWeatherPointsInOneScreen,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        setLayoutParams(lp);

        timeTextView = new TextView(this.getContext());
        timeTextView.setGravity(Gravity.CENTER);
        timeTextView.setText(displayedTime);
        timeTextView.setTextSize(10);
        timeTextView.setTextColor(Color.WHITE);
        timeTextView.setY(0);
        timeTextView.measure(0, 0);
        this.addView(timeTextView);
    }

    public static void resetTemps() {
        minTemp = 100000;
        maxTemp = -100000;
    }

    public void generateTemperatureTextView() {
        temperatureTextView = new TextView(this.getContext());
        temperatureTextView.setGravity(Gravity.CENTER);
        temperatureTextView.setText(getTemperatureAsString());
        //temperatureTextView.setText(getTemperatureAsString() + "\n" + weatherIcon);
        temperatureTextView.setTextColor(Color.WHITE);

        temperatureTextView.measure(0, 0);
        int tempTextViewHeight = temperatureTextView.getMeasuredHeight();
        int timeTextViewHeight = timeTextView.getMeasuredHeight();

        // subtract one tempTextViewHeight to not make the bottommost temp below the scroll view
        double totalHeight = MainActivity.weatherColumnsHeight - tempTextViewHeight - timeTextViewHeight;

        double singleTempHeight = totalHeight / tempSpan;
        double posY = totalHeight - (temperature-minTemp) * singleTempHeight;
        temperatureTextView.setY(Math.round(posY));

        this.addView(temperatureTextView);
    }

    public String getTemperatureAsString(){
        return temperature + "°";
    }

    public String toString() {
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
