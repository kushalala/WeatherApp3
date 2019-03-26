package com.example.dan.weatherapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CityList {
    @SerializedName("list") @Expose
    private List<OpenWeatherMap> cityList;

    public List<OpenWeatherMap> getCityList() {
        return cityList;
    }

    public void setCityList(List<OpenWeatherMap> cityList) {
        this.cityList = cityList;
    }


}
