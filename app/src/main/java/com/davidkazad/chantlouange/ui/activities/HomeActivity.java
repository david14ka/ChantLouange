package com.davidkazad.chantlouange.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.config.Login;
import com.davidkazad.chantlouange.ui.fragment.BookFragment;
import com.davidkazad.chantlouange.ui.fragment.DashFragment;
import com.davidkazad.chantlouange.config.utils.LogUtil;
import com.davidkazad.chantlouange.ui.fragment.FavFragment;
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
                case R.id.navigation_dashboard:
                    showFragment(new BookFragment());
                    toolbar.setTitle(R.string.home_title);
                    return true;
                case R.id.navigation_home:
                    showFragment(new FavFragment());
                    toolbar.setTitle(R.string.favorities);
                    return true;
                case R.id.navigation_audio:
                    // Navigate to Audio Activity or show fragment if available
                    // For now, staying on current fragment or opening an activity
                    return true;
                case R.id.navigation_community:
                    startActivity(new Intent(getApplicationContext(), ChatActivity.class));
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
        getNotification(toolbar);
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

    public void writePost1(View view) {
        startActivity(new Intent(getApplicationContext(), CommentActivity.class).putExtra(EXTRA_WRITE_POST,true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.app_bar_search) {
            findItem();
            return true;
        }if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }if (id == R.id.action_helps) {
            openBrowser("help");
            return true;
        }if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void firebaseSignIn() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }
}
