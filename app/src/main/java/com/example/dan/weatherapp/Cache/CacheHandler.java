package com.example.dan.weatherapp.Cache;

import android.content.Context;

import com.example.dan.weatherapp.API.Common;
import com.example.dan.weatherapp.Model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CacheHandler {
    private CacheManager manager;

    public static CacheHandler getInstance(Context context)
    {
        return new CacheHandler(context);
    }

    public static CacheHandler getInstance(CacheManager manager)
    {
        return new CacheHandler(manager);
    }

    private CacheHandler(Context context)
    {
        manager = CacheManager.with(context);
    }

    private CacheHandler(CacheManager manager)
    {
        this.manager = manager;
    }

    /**
     * Retrieves the list of Cities from the Cache
     * @return - A list of Cities
     */
    public List<OpenWeatherMap> getCity()
    {
        List<OpenWeatherMap> cities = new ArrayList<>();
        try
        {
            String data = manager.readFromFile(Common.CITIES_LIST);
            if (StringUtils.isNotBlank(data))
            {
                Gson gson = new Gson();
                Type type = new TypeToken<List<OpenWeatherMap>>(){}.getType();
                cities = gson.fromJson(data, type);
            }
        }
        catch (NullPointerException | JsonParseException e)
        {
            e.printStackTrace();
        }
        return cities;
    }
}
