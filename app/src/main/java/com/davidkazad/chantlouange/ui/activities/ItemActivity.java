package com.davidkazad.chantlouange.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.datas.CC;
import com.davidkazad.chantlouange.models.Favorites;
import com.davidkazad.chantlouange.ui.fragment.ItemFragment;
import com.davidkazad.chantlouange.ui.fragment.ListFragment;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.models.Recent;
import com.davidkazad.chantlouange.datas.CV;
import com.davidkazad.chantlouange.config.utils.LogUtil;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;

public class ItemActivity extends BaseActivity {
    private static final String TAG = ListFragment.class.getName();

    public static Page currentPage;
    public static Book currentBook;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ImageButton btnPrev;
    private ImageButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ButterKnife.bind(this);

        // Keep screen awake setting
        if (Prefs.getBoolean("keep_screen_on", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        navigationDrawer(savedInstanceState, null);

        setFabMenu();

        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);

        initPageContent();

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < currentBook.count() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });

        try {
            currentPage.addToRecent();
        } catch (Exception ex) {
            Log.e(TAG, "onViewCreated: ", ex);
        }

        // Setup du menu en bas (aucun ne s'allume explicitement pour rester discret en mode lecture)
        setupBottomNavigation(0);
    }

    private void initPageContent() {

        setDisplaySong(currentPage);

        viewPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter1 adapter = new PagerAdapter1(getSupportFragmentManager(), currentBook.count());

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPage.getId() - 1);
        
        updateNavigationButtons(viewPager.getCurrentItem());

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = currentBook.getPage(position);
                try {
                    currentPage.addToRecent();
                } catch (Exception ignored) {}
                updateIconFabMenuButton();
                updateDisplay(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        LogUtil.d();
    }

    private void updateDisplay(int position) {
        updateNavigationButtons(position);
    }

    private void updateNavigationButtons(int position) {
        btnPrev.setEnabled(position > 0);
        btnPrev.setAlpha(position > 0 ? 1.0f : 0.5f);
        btnNext.setEnabled(position < currentBook.count() - 1);
        btnNext.setAlpha(position < currentBook.count() - 1 ? 1.0f : 0.5f);
    }

    private void setDisplaySong(Page currentPage) {

        try {
            updateIconFabMenuButton();

        } catch (Exception ax) {

            Toast.makeText(this, "error fatal!", Toast.LENGTH_SHORT).show();
        }
    }

    public class PagerAdapter1 extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter1(FragmentManager fm, int NumOfTabs) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            ItemFragment.currentBook = currentBook;

            return ItemFragment.getInstance(position);

        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    private FloatingActionMenu fabMenu;
    private Handler mUiHandler = new Handler();
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;


    private void setFabMenu() {

        fabMenu = findViewById(R.id.menu_red);

        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fab4 = findViewById(R.id.fab4);

        fab1.setLabelText(getString(R.string.jaime));
        fab2.setLabelText(getString(R.string.share_song));
        fab3.setLabelText(getString(R.string.add_to_favorites));
        fab4.setLabelText(getString(R.string.corriger_erreur));

        updateIconFabMenuButton();

        fabMenu.showMenuButton(false);
        fabMenu.setClosedOnTouchOutside(true);

        fab1.setOnClickListener(fabClickListener);
        fab2.setOnClickListener(fabClickListener);
        fab3.setOnClickListener(fabClickListener);
        fab4.setOnClickListener(fabClickListener);

        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                fabMenu.showMenuButton(true);
            }
        }, 400);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            createCustomAnimation();
        }

        fabMenu.getMenuIconView().setImageResource(fabMenu.isOpened()
                ? R.drawable.ic_close : R.drawable.ic_menu);
    }

    public void updateIconFabMenuButton(){
        if (!currentPage.isFavorite()) {
            fab1.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_black_24dp));
            fab3.setImageDrawable(getDrawable(R.drawable.star0));
        }else  {
            fab1.setImageDrawable(getDrawable(R.drawable.baseline_favorite_24));
            fab3.setImageDrawable(getDrawable(R.drawable.star_filed));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void createCustomAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fabMenu.getMenuIconView().setImageResource(fabMenu.isOpened()
                        ? R.drawable.ic_menu : R.drawable.ic_close);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        fabMenu.setIconToggleAnimatorSet(set);
    }

    private final View.OnClickListener fabClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab1:
                case R.id.fab3:

                    Toast.makeText(ItemActivity.this,
                            (currentPage.isFavorite())
                                    ?"Song Removed!"
                                    : "Song Added!"
                            , Toast.LENGTH_SHORT).show();
                    currentPage.toggleFavorite();

                    updateIconFabMenuButton();

                    break;
                case R.id.fab2:

                    sendText(currentBook.getName() + "\n" + currentPage.getNumber() + currentPage.getTitle() + "\n\n" + currentPage.getContent());

                    break;

                case R.id.fab4:
                    EditActivity.mItem = currentPage;
                    EditActivity.mBook = currentBook;
                    startActivity(new Intent(getApplicationContext(), EditActivity.class).putExtra("edit", true));

                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement



        if (id == R.id.app_bar_switch) {

            if (Prefs.getBoolean("night_mode", false)) {
                Prefs.putBoolean("night_mode", false);
            } else {
                Prefs.putBoolean("night_mode", true);
            }

            initPageContent();
            //findItem();
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    @Override
    public void findItem() {

        new MaterialDialog.Builder(this)
                .title(R.string.search_in)
                .items(R.array.book_list2)
                .itemsCallbackSingleChoice(currentBook.getId() - 1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, final int bookId, final CharSequence text) {

                        new MaterialDialog.Builder(ItemActivity.this)
                                .title(text)
                                .input(R.string.number, R.string.action_search, false, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence page) {

                                        currentBook = Book.bookList.get(bookId);
                                        Book bookWithPageMarkedAB = (bookId==1)? new CV(): (bookId == 0) ? new CC() : null;

                                        if (bookWithPageMarkedAB != null){
                                            findPageSongWith_A_B(bookWithPageMarkedAB, page);
                                        }
                                        else {
                                            currentPage = currentBook.getPage(Integer.valueOf(String.valueOf(page)) - 1);

                                            if (currentPage != null) {

                                                initPageContent();

                                            } else {
                                                Toast.makeText(getApplicationContext(), R.string.number_ot_exists, Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    }
                                })
                                .inputType(InputType.TYPE_CLASS_NUMBER)
                                .negativeText(R.string.cancel)
                                .positiveText(R.string.research)
                                .show();
                        return true; // allow selection
                    }
                })
                .show();

    }

    private void findPageSongWith_A_B(Book book, CharSequence page) {

            final List<Page> pageList = book.find("" + page);
            final List<String> numberList = new ArrayList<>();

            if (pageList.size() > 1) {

                for (Page page1 :
                        pageList) {
                    numberList.add(page1.getNumber());
                }
                new MaterialDialog.Builder(ItemActivity.this)
                        .title(book.getName())
                        .items(numberList)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                currentPage = pageList.get(which);

                                if (currentPage != null) {

                                    initPageContent();

                                } else {

                                    Toast.makeText(getApplicationContext(), R.string.number_ot_exists, Toast.LENGTH_SHORT).show();
                                }

                                return false;
                            }
                        })
                        .show();

            } else if (pageList.size() == 1) {

                currentPage = pageList.get(0);
                if (currentPage != null) {

                    initPageContent();

                } else {

                    Toast.makeText(getApplicationContext(), R.string.number_ot_exists, Toast.LENGTH_SHORT).show();
                }
            }

        }
}
