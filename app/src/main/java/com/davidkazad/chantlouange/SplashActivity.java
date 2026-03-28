package com.davidkazad.chantlouange;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.davidkazad.chantlouange.databinding.ActivitySplashBinding;
import com.davidkazad.chantlouange.datas.OB;
import com.davidkazad.chantlouange.ui.activities.HomeActivity;
import com.davidkazad.chantlouange.config.utils.LogUtil;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            enter();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler.postDelayed(runnable, 1500);

        binding.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
