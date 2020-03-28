package com.davidkazad.chantlouange;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.davidkazad.chantlouange.activities.IntroActivity;
import com.davidkazad.chantlouange.models.Favoris;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

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

                else

                    startActivity(new Intent(getApplicationContext(), IntroActivity.class));

            }

        },2000);
    }

    @OnClick({R.id.btn_enter})
    public void enter(){

       /* for (Favoris f :
                Favoris.getList()) {
            f.delete();
        }*/

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }


}
