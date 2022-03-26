package com.davidkazad.chantlouange;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.davidkazad.chantlouange.ui.activities.BaseActivity;
import com.davidkazad.chantlouange.ui.activities.SettingsActivity;
import com.davidkazad.chantlouange.ui.activities.CommentActivity;
import com.davidkazad.chantlouange.ui.fragment.SearchFragment;
import com.davidkazad.chantlouange.ui.fragment.BookFragment;
import com.davidkazad.chantlouange.ui.fragment.ListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_WRITE_POST = "write";

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

            ListFragment newFragment = (ListFragment) fm.findFragmentByTag(f);
            if (newFragment == null) {
                newFragment = new ListFragment();
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
            Fragment frag = fm.findFragmentByTag(fragment);
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
        manager.beginTransaction().replace(R.id.container, new BookFragment()).commit();

        navigationDrawer(savedInstanceState, toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //search(menu);
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

                ListFragment.query = query;
                invalidateOptionsMenu();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                ListFragment.query = query;
                EventBus.getDefault().post(query);
                //Arrays.
                Log.d(TAG, "onQueryTextChange: ");
                return false;
            }

        });
    }


    public void writePost1(View view) {
        startActivity(new Intent(getApplicationContext(), CommentActivity.class).putExtra(EXTRA_WRITE_POST,true));
    }
}
