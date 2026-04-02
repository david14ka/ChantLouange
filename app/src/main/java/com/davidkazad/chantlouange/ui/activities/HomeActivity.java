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
import com.davidkazad.chantlouange.ui.fragment.AllSongsFragment;
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
                    showFragment(new BookFragment());
                    ((android.widget.TextView) findViewById(R.id.toolbar_title)).setText("Chant Louange");
                    return true;
                case R.id.navigation_songs:
                    showFragment(new AllSongsFragment());
                    ((android.widget.TextView) findViewById(R.id.toolbar_title)).setText("Nos Chants");
                    return true;
                case R.id.navigation_favorites:
                    showFragment(new FavFragment());
                    ((android.widget.TextView) findViewById(R.id.toolbar_title)).setText(R.string.favorities);
                    return true;
                case R.id.navigation_settings:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
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
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        if (navigation != null) {
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        }

        fm = getSupportFragmentManager();

        navigationDrawer(savedInstanceState, toolbar);

        firebaseSignIn();

        LogUtil.d();
        
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        int targetId = intent.getIntExtra("targetFragment", R.id.navigation_home);
        if (navigation != null) {
            navigation.setSelectedItemId(targetId);
        } else {
            // Optionnel : cas où le menu n'est pas encore prêt.
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.app_bar_search) {
            startActivity(new Intent(this, GlobalSearchActivity.class));
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
