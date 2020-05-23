package com.davidkazad.chantlouange;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.davidkazad.chantlouange.activities.HomeActivity;
import com.davidkazad.chantlouange.activities.ItemActivity;
import com.davidkazad.chantlouange.common.Common;
import com.davidkazad.chantlouange.common.Login;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.models.User;
import com.davidkazad.chantlouange.songs.CC;
import com.davidkazad.chantlouange.utils.LogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        /*ItemActivity.currentBook = new CC();
        ItemActivity.currentPage = ItemActivity.currentBook.getPage(1);
        startActivity(new Intent(getApplicationContext(), ItemActivity.class));*/

        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

}
