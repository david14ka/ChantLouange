package com.davidkazad.chantlouange.activities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.fragment.ListFragment;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.utils.LogUtil;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.davidkazad.chantlouange.common.Common.PREFS_TABLE_MATIERES_ALPHABETIQUE;

public class ListActivity extends BaseActivity {
    private static final String TAG = ListFragment.class.getName();

    public static Book bookItem;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.list_of_songs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        navigationDrawer(savedInstanceState, null);

        LogUtil.d();

        initView(bookItem.getId() - 1);
    }

    private void initView(int currentTab) {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("C.C"));
        tabLayout.addTab(tabLayout.newTab().setText("C.V"));
        tabLayout.addTab(tabLayout.newTab().setText("N.M"));
        tabLayout.addTab(tabLayout.newTab().setText("N.W"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        toolbar.setSubtitle(bookItem.getName());

        PagerAdapter1 adapter = new PagerAdapter1(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setScrollPosition(currentTab, 0f, true);
        viewPager.setCurrentItem(currentTab);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                bookItem = Book.bookList.get(tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
                Log.d(TAG, "onTabSelected: tab " + tab.getPosition());
                toolbar.setSubtitle(bookItem.getName());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        LogUtil.d();
    }

    public void trie(View view) {

        new MaterialDialog.Builder(ListActivity.this)
                .title(R.string.table_matieres)
                .items(R.array.table_des_matieres)
                .itemsCallbackSingleChoice((Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false) ? 0 : 1), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Prefs.putBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, which == 0);
                        initView(bookItem.getId() - 1);
                        return false;
                    }
                })
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .show();
    }

    public class PagerAdapter1 extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        PagerAdapter1(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            //int bookId = Book.bookList.get(position).getId();
            return ListFragment.getInstance(position);

        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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

        if (id == R.id.action_find) {
            findItem();
            return true;
        }
        if (id == R.id.action_sort) {
            trie(null);
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_helps) {

            openHelp();
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

    @Override
    protected void onStop() {
        super.onStop();
        ListFragment.query = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListFragment.query = null;
    }
}
