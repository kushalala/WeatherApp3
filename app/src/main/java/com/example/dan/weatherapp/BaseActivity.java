package com.example.dan.weatherapp;

import android.Manifest;
import android.content.Context;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dan.weatherapp.API.ApiCallback;
import com.example.dan.weatherapp.API.Common;
import com.example.dan.weatherapp.API.callback.CityCall;
import com.example.dan.weatherapp.Helper.Helper;
import com.example.dan.weatherapp.Model.OpenWeatherMap;
import com.example.dan.weatherapp.utils.AppLogger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static java.security.AccessController.getContext;

public class BaseActivity extends AppCompatActivity implements LocationListener {


    @BindView(R.id.title)
    TextView txtTitle;
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
    @BindView(R.id.txtID)
    TextView txtID;

    @BindView(R.id.refresh)
    ImageButton ibRefresh;

    String strTitle, strCity, strLastUpdate, strDescription, strHumidity, strTime, strCelcius;


    private static final String TAG = "BaseActivity";

    private OpenWeatherMap mainCity;

    LocationManager locationManager;
    String provider, s;
    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();


    int MY_PERMISSION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);


        //Get Coordinates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null)
            Log.e("TAG","No Location");
    }

 /*   private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

              return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, BaseActivity.this);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }
*/
    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    public BaseActivity() {
        super();
    }



    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        new GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));

    }

    @OnClick(R.id.refresh)
    public void onRefresh() {
        new GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



     private class GetWeather extends AsyncTask<String,Void,String> {
        ProgressDialog pd = new ProgressDialog(BaseActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please wait..");
            pd.show();
        }


        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return stream;

        }


        @Override
        protected void onPostExecute(String s) {
            txtTitle.setText("Current Weather");
            super.onPostExecute(s);
            if (s.contains("Error: Not found city")) {
                pd.dismiss();
                return;
            }
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>() {
            }.getType();
            openWeatherMap = gson.fromJson(s, mType);
            pd.dismiss();



            txtcity.setText(String.format("%s,%s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
            txtLastUpdate.setText(String.format("Last Updated: %s", Common.getDateNow()));
            txtID.setText(String.format("ID: %s", openWeatherMap.getId()));
            txtDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
            txtHumidity.setText(String.format("%d%%", openWeatherMap.getMain().getHumidity()));
            txtTime.setText(String.format("%s/%s", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()), Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            txtCelcius.setText(String.format("%.2f °C", openWeatherMap.getMain().getTemp()));
            Picasso.get().load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);

            FileCacher<String> stringCacher = new FileCacher<>(BaseActivity.this, "sometext.txt");

            strCity = txtcity.getText().toString();
            strLastUpdate = txtLastUpdate.getText().toString();
            strDescription = txtDescription.getText().toString();
            strHumidity = txtHumidity.getText().toString();
            strTime = txtTime.getText().toString();
            strCelcius = txtTime.getText().toString();

            try {
                stringCacher.writeCache(strCity);
                stringCacher.writeCache(strLastUpdate);
                stringCacher.writeCache(strDescription);
                stringCacher.writeCache(strHumidity);
                stringCacher.writeCache(strTime);
                stringCacher.writeCache(strCelcius);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(stringCacher.hasCache())
            {
                try {
                    stringCacher.readCache();
                    Log.d(TAG, "Some text inserted: ");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassCastException e){
                    e.printStackTrace();
                }
            }


        }


        /**
         * Retrieves the mainCity information
         */





    }
}
