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

        // Load animations
        android.view.animation.Animation fadeInScale = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        android.view.animation.Animation fadeInSlideUp = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up);

        // Staggered starts
        binding.logoContainer.startAnimation(fadeInScale);

        fadeInSlideUp.setStartOffset(400);
        binding.txtChant.startAnimation(fadeInSlideUp);

        android.view.animation.Animation fadeInSlideUp2 = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up);
        fadeInSlideUp2.setStartOffset(600);
        binding.txtLouange.startAnimation(fadeInSlideUp2);

        android.view.animation.Animation fadeInSlogan = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up);
        fadeInSlogan.setStartOffset(900);
        binding.txtSlogan.startAnimation(fadeInSlogan);

        android.view.animation.Animation fadeInButton = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        fadeInButton.setStartOffset(1200);
        binding.btnEnter.startAnimation(fadeInButton);

        android.view.animation.Animation fadeInFooter = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up);
        fadeInFooter.setStartOffset(1500);
        binding.footerContainer.startAnimation(fadeInFooter);

        // Transitions to Home
        handler.postDelayed(runnable, 3500); // 3.5s for full animation sequence

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
