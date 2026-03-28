package com.davidkazad.chantlouange;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.davidkazad.chantlouange.databinding.ActivitySplashBinding;
import com.davidkazad.chantlouange.datas.OB;
import com.davidkazad.chantlouange.ui.activities.HomeActivity;
import com.davidkazad.chantlouange.config.utils.LogUtil;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    OB obBook = new OB();
    private static final String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }

        }, 2000);

        binding.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter();
            }
        });

        LogUtil.d();
    }

    public void enter() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    public void slogan(View view) {
        //startActivity(new Intent(getApplicationContext(), MusicActivity.class));
    }
}
