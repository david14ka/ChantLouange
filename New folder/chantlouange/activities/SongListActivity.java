package com.davidkazad.chantlouange.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.davidkazad.chantlouange.MainActivity;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.fragment.SongListFragment;
import com.davidkazad.chantlouange.songs.SongsBook;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class SongListActivity extends BaseActivity {
    private static final String TAG = SongListFragment.class.getName();

    public static Integer songBookId;
    RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    @BindView(R.id.bmb)
    BoomMenuButton bmb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("List des chants");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        navigationDrawer(savedInstanceState, null);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("C.C"));
        tabLayout.addTab(tabLayout.newTab().setText("C.V"));
        tabLayout.addTab(tabLayout.newTab().setText("N.M"));
        tabLayout.addTab(tabLayout.newTab().setText("N.W"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setCurrentItem(songBookId);
        Log.d(TAG, "onCreate-songsBookId: " + songBookId);
        SongsBook book = SongsBook.getSongBook(songBookId-1);
        toolbar.setSubtitle(book.getSongsBookName());

        PagerAdapter1 adapter = new PagerAdapter1(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        int tabIndex = songBookId;
        tabLayout.setScrollPosition(tabIndex, 0f, true);
        viewPager.setCurrentItem(tabIndex);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
                Log.d(TAG, "onTabSelected: tab " + tab.getPosition());
                toolbar.setSubtitle(SongsBook.getBookList().get(tab.getPosition()).getSongsBookName());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        bookMenu();
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

                        startActivity(new Intent(getApplicationContext(),FavorisActivity.class));
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

    public class PagerAdapter1 extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter1(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            return SongListFragment.getInstance(position);

        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }
        if (id == R.id.app_bar_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void search(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

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

    @Override
    protected void onStop() {
        super.onStop();
        SongListFragment.query = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SongListFragment.query = null;
    }
}
