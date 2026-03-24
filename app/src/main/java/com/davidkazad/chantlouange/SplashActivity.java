package com.davidkazad.chantlouange;

import static com.davidkazad.chantlouange.config.Common.FIRST_RUN;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.davidkazad.chantlouange.datas.OB;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.ui.activities.HomeActivity;
import com.davidkazad.chantlouange.config.utils.LogUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    //public static List<Book> bookList;
    OB obBook = new OB();
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

                startActivity(new Intent(getApplicationContext(), HomeActivity.class));

//                if (Prefs.getBoolean(FIRST_RUN,false))
//                    buttonEnter.setVisibility(View.VISIBLE);
//                    //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                else
//                    startActivity(new Intent(getApplicationContext(), IntroSlider.class));


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
