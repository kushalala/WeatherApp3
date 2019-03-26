package com.example.dan.weatherapp.API;

import com.example.dan.weatherapp.Model.OpenWeatherMap;

import java.util.List;

public class ApiCallback {

  /*
    public interface GetCitiesListener
    {
        void onSuccess(List<OpenWeatherMap> cities);
        void onError();
    }
    */

    public interface GetCityListener
    {
        void onSuccess(OpenWeatherMap city);
        void onError();
    }
}
