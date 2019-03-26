package com.example.dan.weatherapp.API.callback;

import android.content.Context;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.dan.weatherapp.API.ApiCallback;
import com.example.dan.weatherapp.API.ApiConnector;
import com.example.dan.weatherapp.API.ApiHandler;
import com.example.dan.weatherapp.API.Common;
import com.example.dan.weatherapp.Cache.CacheManager;
import com.example.dan.weatherapp.Model.CityList;
import com.example.dan.weatherapp.Model.OpenWeatherMap;
import com.example.dan.weatherapp.utils.AppLogger;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityCall {

    private static final String TAG = CityCall.class.getSimpleName();
    private Context context;
    private CacheManager manager;

    private Call<CityList> cityListCall;
    private Call<OpenWeatherMap> cityCall;

    public static CityCall with(Context context) {
        return new CityCall(context);
    }

    private CityCall(Context context) {
        this.context = context;
        manager = CacheManager.with(context);
    }

    /**
     * Retrieves the details of the cities Prague, San Francisco and London
     * @param listener - Listener for callback events
     */

  /*
    public void retrieveCities(@NonNull ApiCallback.GetCitiesListener listener) {

        ApiHandler api = ApiConnector.createService(ApiHandler.class);

        Map<String, String> params = new HashMap<>();
        params.put("units", "metric"); // Set to Metric to change the temperature values to Celsius
        params.put("id", TextUtils.join(",", Arrays.asList("3067696", "5391959", "2643743"))); // These IDs are from Prague, San Francisco and London, respectively
        params.put("appid", Common.API_KEY); // App ID is set to query the necessary data

        cityListCall = api.getCities(params);
        cityListCall.enqueue(new Callback<CityList>() {
            @Override
            public void onResponse(@NonNull Call<CityList> call, @NonNull Response<CityList> response) {
                AppLogger.info(TAG, "retrieveCities onResponse()");
                if (response.isSuccessful())
                {
                    CityList cityList = response.body();
                    if (cityList != null && !cityList.getCityList().isEmpty())
                    {
                        List<OpenWeatherMap> cities = cityList.getCityList();
                        Gson gson = new Gson();
                        String listStr = gson.toJson(cities);
                        manager.writeToFile(listStr, Common.CITIES_LIST);

                        listener.onSuccess(cities);
                    }
                    else
                    {
                        listener.onError();
                    }
                }
                else
                {
                    listener.onError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CityList> call, @NonNull Throwable t) {
                if (call.isCanceled())
                {
                    AppLogger.error(TAG, "retrieveCities canceled");
                }
                else
                {
                    t.printStackTrace();
                    AppLogger.error(TAG, "retrieveCities onFailure()");
                    listener.onError();
                }
            }
        });
    }
    */


    /**
     * Retrieves the details of one city, based on the City ID
     * @param id - The City ID
     * @param listener - The Listener for callback events
     */
    public void getCity(String id, @NonNull ApiCallback.GetCityListener listener)
    {
        ApiHandler api = ApiConnector.createService(ApiHandler.class);

        Map<String, String> params = new HashMap<>();
        params.put("units", "metric"); // Set to Metric to change the temperature values to Celsius
        params.put("id", id); // The ID of the city in question
        params.put("appid", Common.API_KEY); // App ID is set to query the necessary data

        cityCall = api.getCity(params);
        cityCall.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(@NonNull Call<OpenWeatherMap> call, @NonNull Response<OpenWeatherMap> response) {
                AppLogger.info(TAG, "getCity onResponse()");
                if (response.isSuccessful())
                {

                    OpenWeatherMap city = response.body();
                    if (city != null)                    {
                        OpenWeatherMap cities = city;
                        Gson gson = new Gson();
                        String listStr = gson.toJson(cities);
                        manager.writeToFile(listStr, Common.CITY_STR);
                        listener.onSuccess(city);
                    }
                    else
                    {
                        listener.onError();
                    }
                }
                else
                {
                    listener.onError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OpenWeatherMap> call, @NonNull Throwable t) {
                if (cityCall.isCanceled())
                {
                    AppLogger.error(TAG, "retrieveCity canceled");
                }
                else
                {
                    t.printStackTrace();
                    AppLogger.error(TAG, "getCity onFailure()");
                    listener.onError();
                }
            }
        });
    }

    /**
     * Cancels the call to retrieve a city's information
     */
    public void cancelCityCallRequest()
    {
        if (cityCall != null)
        {
            cityCall.cancel();
        }
    }

    /**
     * Cancels the call to retrieve information of multiple cities
     */
    public void cancelCityListCallRequest()
    {
        if (cityListCall != null)
        {
            cityListCall.cancel();
        }
    }
}
