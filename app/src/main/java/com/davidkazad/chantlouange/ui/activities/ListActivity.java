package com.davidkazad.chantlouange.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.ui.fragment.ListFragment;
import com.davidkazad.chantlouange.config.utils.LogUtil;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;

import static com.davidkazad.chantlouange.config.Common.PREFS_TABLE_MATIERES_ALPHABETIQUE;

public class ListActivity extends BaseActivity {
    private static final String TAG = ListActivity.class.getSimpleName();

    public static Book bookItem;

    // ── Book image map keyed by book id ──────────────────────────────────────
    // Assign existing drawables to each book id (1-based)
    private static final int[] BOOK_IMAGES = {
            R.drawable.music_book_6168179_640, // 1
            R.drawable.music_notes_3221097_640, // 2
            R.drawable.bground,                 // 3
            R.drawable.bground_3,               // 4
            R.drawable.music_sheet_5117328_640, // 5
            R.drawable.guitar_3283649_640,      // 6
            R.drawable.ob_piano,                // 7
            R.drawable.book_1,                  // 8
            R.drawable.book_3,                  // 9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        LogUtil.d();

        // ── Standard Toolbar setup for Drawer ──────────────────────────────
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        navigationDrawer(savedInstanceState, toolbar);

        // ── Compact UI elements setup ─────────────────────────────────────────
        TextView heroCount   = findViewById(R.id.hero_count_chip);
        TextView heroName    = findViewById(R.id.hero_book_name);
        ImageView btnBack    = findViewById(R.id.btn_back);
        
        if (bookItem != null) {
            if (heroName != null) heroName.setText(bookItem.getName());
            if (heroCount != null) heroCount.setText(String.valueOf(bookItem.getPages().size()));
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> onBackPressed());

        // ── Inline search ─────────────────────────────────────────────────────
        EditText searchEdit = findViewById(R.id.search_edit);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                EventBus.getDefault().post(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // ── Pager (no TabLayout — single book view) ───────────────────────────
        initPager();

        // ── Setup Bottom Navigation Global ────────────────────────────────────
        setupBottomNavigation(R.id.navigation_home);
    }

    private void initPager() {
        ViewPager viewPager = findViewById(R.id.pager);
        int bookIdx = bookItem != null ? bookItem.getId() - 1 : 0;
        viewPager.setAdapter(new SingleBookPagerAdapter(getSupportFragmentManager(), bookIdx));
        viewPager.setCurrentItem(0);
    }

    /** Single-page pager that shows only the current book's ListFragment. */
    private static class SingleBookPagerAdapter extends FragmentStatePagerAdapter {
        private final int bookIdx;

        SingleBookPagerAdapter(FragmentManager fm, int bookIdx) {
            super(fm);
            this.bookIdx = bookIdx;
        }

        @Override
        public Fragment getItem(int position) {
            return ListFragment.getInstance(bookIdx);
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // minimal menu — sort option remains
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_sort) {
            boolean isAlpha = Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false);
            Prefs.putBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, !isAlpha);
            // reload fragment via EventBus reload trigger (re-init pager)
            initPager();
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
        if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
