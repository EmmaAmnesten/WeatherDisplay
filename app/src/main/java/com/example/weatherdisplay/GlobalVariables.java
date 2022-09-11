package com.example.weatherdisplay;

import android.graphics.Color;

public class GlobalVariables {
    static int dayBlue = Color.argb(255, 0, 170, 255);
    static int nightBlue = Color.argb(255, 4, 142, 233);
    static int separatorBlue = Color.argb(255, 0, 151, 237);

    /** When useFullHeight is false then we use Emma's logic which adds spaces at the bottom when the
     * temperatures are high, and space at the top when the temperatures are low to make it easier
     * to see if it is a warm day or not.
     **/
    static boolean useFullHeight = true;
}
