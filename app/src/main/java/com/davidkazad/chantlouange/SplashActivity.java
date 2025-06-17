package com.davidkazad.chantlouange;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.davidkazad.chantlouange.ui.activities.HomeActivity;
import com.davidkazad.chantlouange.config.utils.LogUtil;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "Splash";
    @BindView(R.id.btn_enter)
    Button buttonEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Prefs.getBoolean("firstRun",false))

                    buttonEnter.setVisibility(View.VISIBLE);
                    //startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                else

                    startActivity(new Intent(getApplicationContext(), IntroSlider.class));

            }

        },2000);



        LogUtil.d();
    }


    @OnClick({R.id.btn_enter})
    public void enter(){

        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    public void slogan(View view) {
        //startActivity(new Intent(getApplicationContext(), MusicActivity.class));
    }



}
