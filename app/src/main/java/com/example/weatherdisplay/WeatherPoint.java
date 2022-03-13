package com.example.weatherdisplay;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

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
        if (22 <= Integer.parseInt(displayedTime) || Integer.parseInt(displayedTime) <= 7) {
            this.setBackgroundColor(MainActivity.nightBlue);
        }

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
        Integer weatherIconNumber = weatherIconNumbers.get(weatherIconAsString);
        if (weatherIconNumber != null) {
            weatherIconImageView.setImageDrawable(v.getResources().getDrawable(weatherIconNumber));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 80);
            layoutParams.gravity=Gravity.CENTER;
            weatherIconImageView.setLayoutParams(layoutParams);
        }

        weatherDataLinearLayout = new LinearLayout(getContext());
        weatherDataLinearLayout.setOrientation(VERTICAL);
        weatherDataLinearLayout.addView(temperatureTextView);
        weatherDataLinearLayout.addView(weatherIconImageView);
        double posY = calculateYPos();
        weatherDataLinearLayout.setY(Math.round(posY));

        this.addView(weatherDataLinearLayout);

    }

    private double calculateYPos() {
        weatherDataLinearLayout.measure(0, 0);
        int weatherDataHeight = weatherDataLinearLayout.getMeasuredHeight();
        int timeTextViewHeight = timeTextView.getMeasuredHeight();

        // subtract one tempTextViewHeight to not make the bottommost temp below the scroll view
        double totalHeight = MainActivity.weatherColumnsHeight - weatherDataHeight - timeTextViewHeight;

        double singleTempHeight = totalHeight / tempSpan;
        return totalHeight - (temperature-minTemp) * singleTempHeight;
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

    Map<String, Integer> weatherIconNumbers = new HashMap<String, Integer>() {{
        put("clearsky_day", (Integer)R.drawable.clearsky_day);
        put("clearsky_night", (Integer)R.drawable.clearsky_night);
        put("clearsky_polartwilight", (Integer)R.drawable.clearsky_polartwilight);
        put("cloudy", (Integer)R.drawable.cloudy);
        put("fair_day", (Integer)R.drawable.fair_day);
        put("fair_night", (Integer)R.drawable.fair_night);
        put("fair_polartwilight", (Integer)R.drawable.fair_polartwilight);
        put("fog", (Integer)R.drawable.fog);
        put("heavyrain", (Integer)R.drawable.heavyrain);
        put("heavyrainandthunder", (Integer)R.drawable.heavyrainandthunder);
        put("heavyrainshowers_day", (Integer)R.drawable.heavyrainshowers_day);
        put("heavyrainshowers_night", (Integer)R.drawable.heavyrainshowers_night);
        put("heavyrainshowers_polartwilight", (Integer)R.drawable.heavyrainshowers_polartwilight);
        put("heavyrainshowersandthunder_day", (Integer)R.drawable.heavyrainshowersandthunder_day);
        put("heavyrainshowersandthunder_night", (Integer)R.drawable.heavyrainshowersandthunder_night);
        put("heavyrainshowersandthunder_polartwilight", (Integer)R.drawable.heavyrainshowersandthunder_polartwilight);
        put("heavysleet", (Integer)R.drawable.heavysleet);
        put("heavysleetandthunder", (Integer)R.drawable.heavysleetandthunder);
        put("heavysleetshowers_day", (Integer)R.drawable.heavysleetshowers_day);
        put("heavysleetshowers_night", (Integer)R.drawable.heavysleetshowers_night);
        put("heavysleetshowers_polartwilight", (Integer)R.drawable.heavysleetshowers_polartwilight);
        put("heavysleetshowersandthunder_day", (Integer)R.drawable.heavysleetshowersandthunder_day);
        put("heavysleetshowersandthunder_night", (Integer)R.drawable.heavysleetshowersandthunder_night);
        put("heavysleetshowersandthunder_polartwilight", (Integer)R.drawable.heavysleetshowersandthunder_polartwilight);
        put("heavysnow", (Integer)R.drawable.heavysnow);
        put("heavysnowandthunder", (Integer)R.drawable.heavysnowandthunder);
        put("heavysnowshowers_day", (Integer)R.drawable.heavysnowshowers_day);
        put("heavysnowshowers_night", (Integer)R.drawable.heavysnowshowers_night);
        put("heavysnowshowers_polartwilight", (Integer)R.drawable.heavysnowshowers_polartwilight);
        put("heavysnowshowersandthunder_day", (Integer)R.drawable.heavysnowshowersandthunder_day);
        put("heavysnowshowersandthunder_night", (Integer)R.drawable.heavysnowshowersandthunder_night);
        put("heavysnowshowersandthunder_polartwilight", (Integer)R.drawable.heavysnowshowersandthunder_polartwilight);
        put("lightrain", (Integer)R.drawable.lightrain);
        put("lightrainandthunder", (Integer)R.drawable.lightrainandthunder);
        put("lightrainshowers_day", (Integer)R.drawable.lightrainshowers_day);
        put("lightrainshowers_night", (Integer)R.drawable.lightrainshowers_night);
        put("lightrainshowers_polartwilight", (Integer)R.drawable.lightrainshowers_polartwilight);
        put("lightrainshowersandthunder_day", (Integer)R.drawable.lightrainshowersandthunder_day);
        put("lightrainshowersandthunder_night", (Integer)R.drawable.lightrainshowersandthunder_night);
        put("lightrainshowersandthunder_polartwilight", (Integer)R.drawable.lightrainshowersandthunder_polartwilight);
        put("lightsleet", (Integer)R.drawable.lightsleet);
        put("lightsleetandthunder", (Integer)R.drawable.lightsleetandthunder);
        put("lightsleetshowers_day", (Integer)R.drawable.lightsleetshowers_day);
        put("lightsleetshowers_night", (Integer)R.drawable.lightsleetshowers_night);
        put("lightsleetshowers_polartwilight", (Integer)R.drawable.lightsleetshowers_polartwilight);
        put("lightsnow", (Integer)R.drawable.lightsnow);
        put("lightsnowandthunder", (Integer)R.drawable.lightsnowandthunder);
        put("lightsnowshowers_day", (Integer)R.drawable.lightsnowshowers_day);
        put("lightsnowshowers_night", (Integer)R.drawable.lightsnowshowers_night);
        put("lightsnowshowers_polartwilight", (Integer)R.drawable.lightsnowshowers_polartwilight);
        put("lightssleetshowersandthunder_day", (Integer)R.drawable.lightssleetshowersandthunder_day);
        put("lightssleetshowersandthunder_night", (Integer)R.drawable.lightssleetshowersandthunder_night);
        put("lightssleetshowersandthunder_polartwilight", (Integer)R.drawable.lightssleetshowersandthunder_polartwilight);
        put("lightssnowshowersandthunder_day", (Integer)R.drawable.lightssnowshowersandthunder_day);
        put("lightssnowshowersandthunder_night", (Integer)R.drawable.lightssnowshowersandthunder_night);
        put("lightssnowshowersandthunder_polartwilight", (Integer)R.drawable.lightssnowshowersandthunder_polartwilight);
        put("partlycloudy_day", (Integer)R.drawable.partlycloudy_day);
        put("partlycloudy_night", (Integer)R.drawable.partlycloudy_night);
        put("partlycloudy_polartwilight", (Integer)R.drawable.partlycloudy_polartwilight);
        put("rain", (Integer)R.drawable.rain);
        put("rainandthunder", (Integer)R.drawable.rainandthunder);
        put("rainshowers_day", (Integer)R.drawable.rainshowers_day);
        put("rainshowers_night", (Integer)R.drawable.rainshowers_night);
        put("rainshowers_polartwilight", (Integer)R.drawable.rainshowers_polartwilight);
        put("rainshowersandthunder_day", (Integer)R.drawable.rainshowersandthunder_day);
        put("rainshowersandthunder_night", (Integer)R.drawable.rainshowersandthunder_night);
        put("rainshowersandthunder_polartwilight", (Integer)R.drawable.rainshowersandthunder_polartwilight);
        put("sleet", (Integer)R.drawable.sleet);
        put("sleetandthunder", (Integer)R.drawable.sleetandthunder);
        put("sleetshowers_day", (Integer)R.drawable.sleetshowers_day);
        put("sleetshowers_night", (Integer)R.drawable.sleetshowers_night);
        put("sleetshowers_polartwilight", (Integer)R.drawable.sleetshowers_polartwilight);
        put("sleetshowersandthunder_day", (Integer)R.drawable.sleetshowersandthunder_day);
        put("sleetshowersandthunder_night", (Integer)R.drawable.sleetshowersandthunder_night);
        put("sleetshowersandthunder_polartwilight", (Integer)R.drawable.sleetshowersandthunder_polartwilight);
        put("snow", (Integer)R.drawable.snow);
        put("snowandthunder", (Integer)R.drawable.snowandthunder);
        put("snowshowers_day", (Integer)R.drawable.snowshowers_day);
        put("snowshowers_night", (Integer)R.drawable.snowshowers_night);
        put("snowshowers_polartwilight", (Integer)R.drawable.snowshowers_polartwilight);
        put("snowshowersandthunder_day", (Integer)R.drawable.snowshowersandthunder_day);
        put("snowshowersandthunder_night", (Integer)R.drawable.snowshowersandthunder_night);
        put("snowshowersandthunder_polartwilight", (Integer)R.drawable.snowshowersandthunder_polartwilight);
    }};
}
