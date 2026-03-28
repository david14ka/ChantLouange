package com.davidkazad.chantlouange.ui.activities;

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
import com.davidkazad.chantlouange.ui.fragment.ListFragment;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.config.utils.LogUtil;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.ButterKnife;
import org.greenrobot.eventbus.EventBus;

import static com.davidkazad.chantlouange.config.Common.PREFS_TABLE_MATIERES_ALPHABETIQUE;

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

        for (Book book : Book.bookList){

            tabLayout.addTab(tabLayout.newTab().setText(book.getAbbreviation()));
        }

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
        
        // Hide standard findItem dialog and wire up quick jump bar
        View quickJumpBar = findViewById(R.id.quick_jump_bar);
        android.widget.EditText editQuickJump = findViewById(R.id.edit_quick_jump);
        android.widget.Button btnQuickJumpGo = findViewById(R.id.btn_quick_jump_go);
        
        btnQuickJumpGo.setOnClickListener(v -> {
            String query = editQuickJump.getText().toString().trim();
            if (query.isEmpty()) return;
            
            // Search inside the current active book
            java.util.List<com.davidkazad.chantlouange.models.Page> results = bookItem.find(query);
            
            if (results.size() >= 1) {
                // To keep it simple, just navigate to the first match
                // (find() returns size 1 usually, unless "1a" / "1b")
                ItemActivity.currentBook = bookItem;
                ItemActivity.currentPage = results.get(0);
                startActivity(new Intent(ListActivity.this, ItemActivity.class));
                
                // Reset so it's clean next time
                editQuickJump.setText("");
                quickJumpBar.setVisibility(View.GONE);
                
                // Hide keyboard
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(editQuickJump.getWindowToken(), 0);
            } else {
                android.widget.Toast.makeText(getApplicationContext(), R.string.number_ot_exists, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_find) {
            // Toggle visibility of quick jump bar
            View quickJumpBar = findViewById(R.id.quick_jump_bar);
            if (quickJumpBar.getVisibility() == View.VISIBLE) {
                quickJumpBar.setVisibility(View.GONE);
                // hide keyboard
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(quickJumpBar.getWindowToken(), 0);
            } else {
                quickJumpBar.setVisibility(View.VISIBLE);
                android.widget.EditText editQuickJump = findViewById(R.id.edit_quick_jump);
                editQuickJump.requestFocus();
                // show keyboard
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(editQuickJump, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
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
        }if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
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
                // Let EventBus or the instance field handle it
                EventBus.getDefault().post(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                EventBus.getDefault().post(query);
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
