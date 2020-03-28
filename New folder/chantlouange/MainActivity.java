package com.davidkazad.chantlouange;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.davidkazad.chantlouange.activities.BaseActivity;
import com.davidkazad.chantlouange.activities.FavorisActivity;
import com.davidkazad.chantlouange.activities.SearchFragment;
import com.davidkazad.chantlouange.activities.SettingsActivity;
import com.davidkazad.chantlouange.fragment.SongListFragment;
import com.davidkazad.chantlouange.fragment.SongsBookFragment;
import com.davidkazad.chantlouange.songs.SongsBook;
import com.davidkazad.chantlouange.utils.LogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialize.color.Material;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity {


    private static final String TAG = "MainActivity";
    @BindView(R.id.bmb)
    BoomMenuButton bmb;

    private FragmentManager fm;
    private boolean isListVisible;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private void showFragment(Fragment fragment, String tag){

        fm.beginTransaction()
                .setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
                .replace(R.id.container, fragment, tag)
                .show(fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
    protected void showFragment(String f){

        if (f.equals("list") && !isListVisible){

            isListVisible = true;

            SongListFragment newFragment = (SongListFragment) fm.findFragmentByTag(f);
            if (newFragment == null) {
                newFragment = new SongListFragment();
            }

            showFragment(newFragment, f);
            toolbar.setTitle("List des chants");
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    protected void hideFragment(String fragment) {

        if (fragment.equals("list")) {

            isListVisible = false;

            FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag(fragment);
            if (frag != null) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_left,
                                R.anim.slide_right,
                                R.anim.slide_left,
                                R.anim.slide_right)
                        .remove(frag)
                        .commitAllowingStateLoss();
                toolbar.setTitle("Chant & Louange");
                Prefs.putBoolean("frag_book",false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isListVisible){
            hideFragment("list");
        }
        //hideFragment("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(String s){

        //showFragment(s);
        //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fm = getSupportFragmentManager();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.container, new SongsBookFragment()).commit();

        bookMenu();
        navigationDrawer(savedInstanceState, toolbar);

        firebaseSignIn();

    }

    private void bookMenu() {
        Rect rect = new Rect();
        rect.bottom = 30;
        rect.top = 30;
        rect.left = 30;
        rect.right = 30;

        HamButton.Builder builder1 = new HamButton.Builder()
                .normalImageRes(R.drawable.fav)
                .normalColorRes(R.color.md_red_500)
                .normalTextRes(R.string.chant_fav)
                .imagePadding(rect)
                .subNormalText("Mes chansons favorites");
        bmb.addBuilder(builder1);

        HamButton.Builder builder2 = new HamButton.Builder()
                .normalImageRes(R.drawable.share)
                .normalColorRes(R.color.md_light_blue_A700)
                .normalTextRes(R.string.share)
                .imagePadding(rect)
                .subNormalText("Partager avec");
        bmb.addBuilder(builder2);

        HamButton.Builder builder3 = new HamButton.Builder()
                .normalImageRes(R.drawable.ic_view_comfy_black_24dp)
                .normalColorRes(R.color.app_primary_dark)
                .normalTextRes(R.string.research)
                .imagePadding(rect)
                .subNormalText("Rechercher une chanson");
        bmb.addBuilder(builder3);

        HamButton.Builder builder4 = new HamButton.Builder()
                .normalImageRes(R.drawable.ic_error_outline_black_24dp)
                .normalColorRes(R.color.md_yellow_50)
                .imagePadding(rect)
                .normalTextColor(getResources().getColor(R.color.black))
                .subNormalTextColor(getResources().getColor(R.color.black))
                .normalTextRes(R.string.signaler)
                .subNormalText("Signaler des erreurs dans l'application");
        bmb.addBuilder(builder4);


        bmb.setOnBoomListener(new OnBoomListener() {

            @Override
            public void onClicked(int index, BoomButton boomButton) {
                switch (index) {
                    case 0: {

                        startActivity(new Intent(getApplicationContext(), FavorisActivity.class));
                        break;
                    }

                    case 1: {
                        shareApp();
                        break;
                    }

                    case 2: {
                        findSongDialog();
                        break;
                    }case 3: {
                        sendEmail();
                        //Toast.makeText(MainActivity.this, "coming soon!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            @Override
            public void onBackgroundClick() {

            }

            @Override
            public void onBoomWillHide() {

            }

            @Override
            public void onBoomDidHide() {

            }

            @Override
            public void onBoomWillShow() {

            }

            @Override
            public void onBoomDidShow() {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main0, menu);
        search(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            findSongDialog();
            return true;
        }if (id == R.id.action_theme) {
            Prefs.putBoolean("Theme",true);

            return true;
        }if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void search(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFragment(new SearchFragment(),"search");
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onBackPressed();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                SongListFragment.query = query;
                invalidateOptionsMenu();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                SongListFragment.query = query;
                EventBus.getDefault().post(query);
                //Arrays.
                Log.d(TAG, "onQueryTextChange: ");
                return false;
            }


        });
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
                            Log.d(TAG, "signInAnonymously:success"+user.getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());

                        }

                    }
                });
    }

}
