package com.example.dan.weatherapp.API;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static String API_KEY = "47cd62bb53f51be7a8a0b18bb9386043";
    public static final String API_LINK = "http://api.openweathermap.org/data/2.5/weather";
    public static final String CITY_STR = "cityStr";
    public static final String CITIES_LIST = "cities_list";

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String GROUP_URL = Common.BASE_URL + "group";
    public static final String WEATHER_URL = Common.BASE_URL + "weather";

    @NonNull
    public static String apiRequest(String lat, String lng){
        StringBuilder sb = new StringBuilder(API_LINK);
        sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric",lat,lng,API_KEY));
        return sb.toString();
    }

    public static String unixTimeStampToDateTime(double unixTimeStamp){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateFormat.format(date);
    }

    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png",icon);
    }

    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
