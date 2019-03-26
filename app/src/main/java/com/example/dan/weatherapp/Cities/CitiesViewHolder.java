package com.example.dan.weatherapp.Cities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dan.weatherapp.API.Common;
import com.example.dan.weatherapp.MainActivity;
import com.example.dan.weatherapp.Model.Coord;
import com.example.dan.weatherapp.Model.OpenWeatherMap;
import com.example.dan.weatherapp.Model.Weather;
import com.example.dan.weatherapp.R;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CitiesViewHolder extends RecyclerView.ViewHolder {
    private Context context;

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.weather) TextView weather;
    @BindView(R.id.temp) TextView temp;
    @BindView(R.id.coords) TextView coords;
    @BindView(R.id.view)
    Button btnView;

    public CitiesViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void setData(OpenWeatherMap city) {
        if (city != null)
        {
            name.setText(city.getName());



            List<Weather> weatherList = city.getWeather();
            if (!weatherList.isEmpty())
            {
                weather.setText(weatherList.get(0).getMain());
            }

            Coord coordinates = city.getCoord();
            if (coordinates != null)
            {
                coords.setText(String.format(Locale.getDefault(), "[%s, %s]", String.valueOf(coordinates.getLat()), String.valueOf(coordinates.getLon())));
            }

            btnView.setOnClickListener(view -> {
                try
                {
                    MainActivity mainActivity = (MainActivity) context;
                    Gson gson = new Gson();
                    String cityStr = gson.toJson(city);
                    Bundle bundle = new Bundle();
                    bundle.putString(Common.CITY_STR, cityStr);

                    mainActivity.showFragment(CityDetailsFragment.class, true, "", bundle);
                }
                catch (NullPointerException | ClassCastException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }
}
