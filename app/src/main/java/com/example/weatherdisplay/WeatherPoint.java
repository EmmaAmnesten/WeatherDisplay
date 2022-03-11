package com.example.weatherdisplay;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherPoint extends LinearLayout {

    public String time;
    public int temperature;
    private TextView temperatureTextView;
    private TextView timeTextView;

    static int minTemp = 100000;
    static int maxTemp = -100000;
    static int tempSpan;

    public WeatherPoint(Context context, String time, int temperature){
        super(context);

        this.time = time;
        this.temperature = temperature;

        minTemp = Math.min(minTemp, temperature);
        maxTemp = Math.max(maxTemp, temperature);
        tempSpan = maxTemp - minTemp;

        setOrientation(VERTICAL);
        setBackgroundColor(Color.BLUE);
        LayoutParams lp = new LayoutParams(
                MainActivity.screenWidth/MainActivity.noWeatherPointsInOneScreen,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        setLayoutParams(lp);

        timeTextView = new TextView(this.getContext());
        timeTextView.setGravity(Gravity.CENTER);
        timeTextView.setText(time.substring(11, 16));
        timeTextView.setTextSize(10);
        timeTextView.setBackgroundColor(Color.YELLOW);
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
        temperatureTextView.setBackgroundColor(Color.GREEN);
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
