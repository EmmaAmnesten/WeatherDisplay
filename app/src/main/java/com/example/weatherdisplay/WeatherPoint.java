package com.example.weatherdisplay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
    public String weatherIconAsString;
    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView timeTextView;
    private LinearLayout weatherDataLinearLayout;

    static int minTemp = 100000;
    static int maxTemp = -100000;
    static int tempSpan;

    public WeatherPoint(Context context, String time, int temperature, String weatherIcon){
        super(context);

        this.temperature = temperature;
        this.weatherIconAsString = weatherIcon;
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
        temperatureTextView.setTextColor(Color.WHITE);

        weatherIconImageView = new ImageView(this.getContext());
        View v = new ImageView(this.getContext());
        weatherIconImageView.setImageDrawable(v.getResources().getDrawable(R.drawable.sun));
        //weatherIconImageView.setBackgroundColor(Color.GREEN);
        weatherIconImageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //weatherIconImageView.setScaleType(ImageView.ScaleType.FIT_START);
        //weatherIconImageView.setAdjustViewBounds(true);
        //weatherDataLinearLayout.setBackgroundColor(Color.BLUE);

        //weatherIconImageView.setOutlineAmbientShadowColor(Color.YELLOW);
        //weatherIconImageView.setOutlineSpotShadowColor(Color.RED);

        weatherDataLinearLayout = new LinearLayout(getContext());
        weatherDataLinearLayout.setOrientation(VERTICAL);

        weatherDataLinearLayout.addView(weatherIconImageView);
        weatherDataLinearLayout.addView(temperatureTextView);

        weatherDataLinearLayout.measure(0, 0);
        int weatherDataHeight = weatherDataLinearLayout.getMeasuredHeight();
        int timeTextViewHeight = timeTextView.getMeasuredHeight();

        // subtract one tempTextViewHeight to not make the bottommost temp below the scroll view
        double totalHeight = MainActivity.weatherColumnsHeight - weatherDataHeight - timeTextViewHeight;

        double singleTempHeight = totalHeight / tempSpan;
        double posY = totalHeight - (temperature-minTemp) * singleTempHeight;
        weatherDataLinearLayout.setY(Math.round(posY));

        this.addView(weatherDataLinearLayout);

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

    //private Drawable GetImageWeatherIcon() {
    //    View v = new ImageView(this.getContext());
    //    int drawableNumber = 0;
    //    switch (weatherIconAsString) {
    //        case "cloudy": return drawableNumber = R.drawable.ic_cloudy); break;
    //        case "fair_day": return v.getResources().getDrawable(R.drawable.ic_fair_day); break;
    //    }
    // }
}
