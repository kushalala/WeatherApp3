package com.example.dan.weatherapp.Cities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dan.weatherapp.API.ApiCallback;
import com.example.dan.weatherapp.API.callback.CityCall;
import com.example.dan.weatherapp.Cache.CacheHandler;
import com.example.dan.weatherapp.Model.OpenWeatherMap;
import com.example.dan.weatherapp.R;
import com.example.dan.weatherapp.utils.AppLogger;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;

public class CitiesFragment extends Fragment {

    private static final String TAG = CitiesFragment.class.getSimpleName();
    private Unbinder unbinder;
    private CityCall cityCall;

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;


    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.error) TextView error;
    @BindString(R.string.city_list) String cityListStr;


    public CitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_CHECK_SETTINGS:
                AppLogger.info(TAG, "User agreed to make required location settings changes");
                mRequestingLocationUpdates = true;
                break;

            case RESULT_CANCELED:
                AppLogger.error(TAG, "User chose not to make required location settings changes");
                mRequestingLocationUpdates = false;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (getActivity() != null)
        {
            if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
                if (grantResults.length <= 0) {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    AppLogger.info(TAG, "User interaction was cancelled.");
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppLogger.info(TAG, "Permission granted, updates requested, starting location updates");
                    mRequestingLocationUpdates = true;
                } else {
                    // Permission has been explicitly denied
                    AppLogger.error(TAG, "Permission explicitly denied");
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cities, container, false);
        mRequestingLocationUpdates = true;
        unbinder = ButterKnife.bind(this, view);
        back.setVisibility(View.INVISIBLE);
        title.setText(cityListStr);

        if (getContext() != null)
        {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            mSettingsClient = LocationServices.getSettingsClient(getContext());

            CacheHandler handler = CacheHandler.getInstance(getContext());
            List<OpenWeatherMap> cities = handler.getCity();
            if (!cities.isEmpty())
            {
                // If the cache exists, use the cache data to immediately display the data.
                setupData(cities);
            }
            else
            {
                // Otherwise, retrieve the cities using an API call.
                //retrieveCities();
            }
        }

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        return view;
    }

    @OnClick(R.id.refresh)
    public void onRefresh() {
        retrieveCities();
    }


    /**
     * Retrieves the information of the three cities (London, Prague and San Francisco)
     */


    private void retrieveCities() {
        Context ctx = getContext();
        if (ctx != null) {

            // If an existing request is being processed, cancel it
            if (cityCall != null)
            {
                cityCall.cancelCityListCallRequest();
            }
            else
            {
                cityCall = CityCall.with(ctx);
            }

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            error.setVisibility(View.GONE);


        }
    }



    /**
     * Sets the received data to the Adapter
     * @param cities - The list of cities
     */
    private void setupData(List<OpenWeatherMap> cities)
    {
        progressBar.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        Context ctx = getContext();
        if (ctx != null)
        {
            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager llm = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(llm);
            CitiesAdapter adapter = new CitiesAdapter(ctx, cities);
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        if (getActivity() != null) {
            int permissionState = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            return permissionState == PackageManager.PERMISSION_GRANTED;
        }
        else
        {
            return false;
        }
    }

    private void requestPermissions()
    {
        if (getActivity() != null)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);

        }
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                if (mCurrentLocation == null)
                {
                    AppLogger.error(TAG, "Null Current Location");
                }
                else
                {
                    AppLogger.info(TAG, "Latitude: " + String.valueOf(mCurrentLocation.getLatitude()) +
                            ", Longitude: " + String.valueOf(mCurrentLocation.getLongitude()));
                }
            }
        };
    }

    private void stopLocationUpdates()
    {
        if (getActivity() != null)
        {
            if (!mRequestingLocationUpdates)
            {
                AppLogger.info(TAG, "Updates never requested, no-op");
                return;
            }

            // It is a good practice to remove location requests when the activity is in a paused or
            // stopped state. Doing so helps battery performance and is especially
            // recommended in applications that request frequent location updates.
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener(getActivity(), task -> {
                        AppLogger.info(TAG, "Updates have been completely removed");
                        mRequestingLocationUpdates = false;
                    });
        }
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        if (getActivity() != null)
        {
            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(getActivity(), locationSettingsResponse -> {
                        AppLogger.info(TAG, "All location settings are satisfied.");

                        try
                        {
                            // Check for missing permissions
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                    mLocationCallback, Looper.myLooper());

                        }
                        catch (SecurityException e)
                        {
                            e.printStackTrace();
                        }
                    })
                    .addOnFailureListener(getActivity(), e -> {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                AppLogger.info(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    startIntentSenderForResult(rae.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null);
                                } catch (IntentSender.SendIntentException sie) {
                                    AppLogger.info(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                AppLogger.error(TAG, errorMessage);
                                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                    });
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
}
