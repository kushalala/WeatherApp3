package com.example.dan.weatherapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity {

    @BindView(R.id.textView2)
    TextView txtView;
    @BindView(R.id.imageView)
    ImageView imageView;

    @BindString(R.string.loading_test_app)
    String strTxt;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        txtView.setText(BuildConfig.CONFIG_TYPE);
        if (StringUtils.equals(BuildConfig.CONFIG_TYPE, "Test Build"))
        {
            txtView.setText("");

        }
        if (StringUtils.equals(BuildConfig.CONFIG_TYPE, "Development Build")) {
            txtView.setText(strTxt);
            txtView.setTypeface(txtView.getTypeface(), Typeface.BOLD_ITALIC);
        }


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {

                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    Intent intent = new Intent(SplashScreen.this,BaseActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Process.killProcess(Process.myPid());
    }
}



