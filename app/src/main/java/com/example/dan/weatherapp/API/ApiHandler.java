package com.example.dan.weatherapp.API;

import com.example.dan.weatherapp.Model.CityList;
import com.example.dan.weatherapp.Model.OpenWeatherMap;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiHandler {

    // Retrieves the details of multiple cities
    @GET(Common.GROUP_URL)
    Call<CityList> getCities(@QueryMap Map<String, String> params);

    // Retrieves the details of one city
    @GET(Common.WEATHER_URL)
    Call<OpenWeatherMap> getCity(@QueryMap Map<String, String> params);
}
