package com.example.dan.weatherapp.Cities;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dan.weatherapp.API.ApiCallback;
import com.example.dan.weatherapp.API.Common;
import com.example.dan.weatherapp.API.callback.CityCall;
import com.example.dan.weatherapp.MainActivity;
import com.example.dan.weatherapp.Model.OpenWeatherMap;
import com.example.dan.weatherapp.R;
import com.example.dan.weatherapp.utils.AppLogger;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CityDetailsFragment extends Fragment {

    private static final String TAG = CityDetailsFragment.class.getSimpleName();

    private Unbinder unbinder;
    private CityCall cityCall;

    @BindView(R.id.txtcity)
    TextView txtcity;
    @BindView(R.id.txtLastUpdate)
    TextView txtLastUpdate;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.txtDescription)
    TextView txtDescription;
    @BindView(R.id.txtHumidity)
    TextView txtHumidity;
    @BindView(R.id.txtTime)
    TextView txtTime;
    @BindView(R.id.txtCelcius)
    TextView txtCelcius;


    @BindView(R.id.back)
    ImageButton ibBack;
    @BindView(R.id.refresh)
    ImageButton ibRefresh;


    //
    /*
    @BindView(R.id.name)
    TextView txtName;
    @BindView(R.id.image)
    ImageView imgCity;
    @BindView(R.id.temperature) TextView txtTemp;
    @BindView(R.id.pressure) TextView txtPressure;
    @BindView(R.id.humidity) TextView txtHumidity;
    @BindView(R.id.avg_temp) TextView txtAvgTemp;

    @BindView(R.id.title) TextView txtTitle;
    @BindView(R.id.group)
    Group group;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.error) TextView txtError;

    @BindString(R.string.pressure) String strPressure;
    @BindString(R.string.humidity) String strHumidity;
    @BindString(R.string.avg_temp) String strAvgTemp;
    @BindString(R.string.city_details) String strCityDetails;
    @BindString(R.string.hpa) String strHpa;
    */


    private OpenWeatherMap mainCity;

    LocationManager locationManager;
    String provider;
    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    public CityDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            String cityStr = bundle.getString(Common.CITY_STR);
            mainCity = gson.fromJson(cityStr, OpenWeatherMap.class);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_city_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupData();
        return view;
    }


    @OnClick(R.id.back)
    public void onBack() {

        try {
            if (getContext() != null) {
                MainActivity mainActivity = (MainActivity) getContext();
                if (mainActivity != null) {
                    mainActivity.popBackStack();
                }
            }
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.refresh)
    public void onRefresh() {
        getCity();
    }


    /**
     * Retrieves the mainCity information
     */


    private void getCity() {
        Context ctx = getContext();
        if (ctx != null && mainCity != null) {

            // If an existing request is being processed, cancel it
            if (cityCall != null) {
                cityCall.cancelCityCallRequest();
            } else {
                cityCall = CityCall.with(ctx);
            }

         /*   progressBar.setVisibility(View.VISIBLE);
            txtError.setVisibility(View.GONE);
            group.setVisibility(View.GONE);
         */
            cityCall.getCity(String.valueOf(mainCity.getId()), new ApiCallback.GetCityListener() {
                @Override
                public void onSuccess(OpenWeatherMap city) {
                    AppLogger.info(TAG, "getCity onSuccess()");


                    mainCity = city;
                    setupData();
                }

                @Override
                public void onError() {
                    AppLogger.error(TAG, "getCity onError()");

                }
            });
        }
    }


    private void setupData() {
        // txtTitle.setText(strCityDetails);


        txtcity.setText(String.format("%s,%s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
        txtLastUpdate.setText(String.format("Last Updated: %s", Common.getDateNow()));
        txtDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
        txtHumidity.setText(String.format("%d%%", openWeatherMap.getMain().getHumidity()));
        txtTime.setText(String.format("%s/%s", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()), Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
        txtCelcius.setText(String.format("%.2f °C", openWeatherMap.getMain().getTemp()));
        Picasso.get().load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                .into(imageView);


    }



}
