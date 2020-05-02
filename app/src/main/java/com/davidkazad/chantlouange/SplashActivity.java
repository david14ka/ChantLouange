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
import com.davidkazad.chantlouange.common.Common;
import com.davidkazad.chantlouange.common.Login;
import com.davidkazad.chantlouange.models.User;
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

                else

                    startActivity(new Intent(getApplicationContext(), IntroSlider.class));

            }

        },2000);

        firebaseSignIn();

        LogUtil.d();
    }

    private void firebaseSignIn() {

        final FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        LogUtil.d("signInAnonymously:success");

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "signInAnonymously:success"+user.getUid());
                            User user1 = new User();
                            user1.setDefaultId(user.getUid());
                            user1.store();

                            Login login = new Login(user);
                            //Common.loginRef.child(user.getUid()).setValue(login);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());

                        }

                    }
                });
    }

    @OnClick({R.id.btn_enter})
    public void enter(){

       /* for (Favoris f :
                Favoris.getList()) {
            f.delete();
        }*/

        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

}
