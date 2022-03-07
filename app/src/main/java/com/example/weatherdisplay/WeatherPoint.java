package com.example.weatherdisplay;

public class WeatherPoint {

    public String time;
    public int temperature;

    public WeatherPoint(String time, int temperature){
        this.time = time;
        this.temperature = temperature;
    }

    public String toString(){

        return time + " - " + temperature;
    }
}
