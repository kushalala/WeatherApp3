package com.example.dan.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;


import com.example.dan.weatherapp.Cities.CityDetailsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFragment(CityDetailsFragment.class, false, "", new Bundle()); // Always show the City List everytime the app loads for the first time
    }

    /**
     * Displays the corresponding Fragment to the container
     * @param fragmentClass - The class to instantiate the Fragment with
     * @param addToBackStack - Whether to add this Fragment to the back stack or not
     * @param tag - An optional tag for the Fragment
     * @param bundle - An optional Bundle for the Fragment
     */
    public void showFragment(Class<?> fragmentClass, boolean addToBackStack, @Nullable String tag, @Nullable Bundle bundle) {
        try {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = Fragment.instantiate(this, fragmentClass.getName(), bundle);
            transaction.replace(R.id.container, fragment, tag);
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pops the back stack
     */
    public void popBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            fm.popBackStack();
        }
    }





}
