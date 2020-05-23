package com.davidkazad.chantlouange.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.davidkazad.chantlouange.chat.CommentActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.common.Login;
import com.davidkazad.chantlouange.fragment.BookFragment;
import com.davidkazad.chantlouange.fragment.DashFragment;
import com.davidkazad.chantlouange.utils.LogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pixplicity.easyprefs.library.Prefs;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_WRITE_POST = "write";
    private FirebaseAuth mAuth;

    private Toolbar toolbar;
    private FragmentManager fm;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showFragment(new DashFragment());
                    toolbar.setTitle(R.string.favorities);

                    return true;
                case R.id.navigation_dashboard:
                    showFragment(new BookFragment());
                    toolbar.setTitle(R.string.home_title);
                    return true;
                case R.id.navigation_notifications:
                    //showFragment(new BookFragment());
                    startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home_title);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_dashboard);

        fm = getSupportFragmentManager();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.container, new BookFragment()).commit();

        navigationDrawer(savedInstanceState, toolbar);

        firebaseSignIn();

        LogUtil.d();
        //if (Prefs.getBoolean("show_cc",true)) promptCollection();


    }
    private void promptCollection() {
        new MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.cc)
                .setPrimaryText("Collection des cantiques")
                .setSecondaryText("Nouvelle version du livre avec plus de 600 chansons")
                .setPromptBackground(new RectanglePromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            // User has pressed the prompt target

                        }
                        Prefs.putBoolean("show_cc",false);
                    }
                })
                .show();
    }
    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
                .replace(R.id.container, fragment, fragment.getTag())
                .show(fragment)
                .commit();
    }

    protected void hideFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentByTag(fragment.getTag());
        if (frag != null) {
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
                    .remove(frag)
                    .commitAllowingStateLoss();
            toolbar.setTitle(R.string.home_title);
        }
    }
    public void writePost1(View view) {
        startActivity(new Intent(getApplicationContext(), CommentActivity.class).putExtra(EXTRA_WRITE_POST,true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_bar_search) {
            //findSongDialog();
            findItem();
            return true;
        }if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }if (id == R.id.action_helps) {
            //startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            openBrowser("help");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void firebaseSignIn() {

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
                            Login login = new Login(user);
                            //Common.loginRef.child(user.getUid()).setValue(login);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());

                        }

                    }
                });
    }



}


