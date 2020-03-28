package com.davidkazad.chantlouange.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.fragment.ItemFragment;
import com.davidkazad.chantlouange.fragment.ListFragment;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Favoris;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.models.Recent;
import com.davidkazad.chantlouange.songs.CV;
import com.davidkazad.chantlouange.utils.LogUtil;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ItemActivity extends BaseActivity {
    private static final String TAG = ListFragment.class.getName();

    public static Page currentPage;
    public static Book currentBook;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_details);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_song_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        navigationDrawer(savedInstanceState, null);
        onCreateFabMenu();


        LogUtil.d();

        initPageContent();

        try {
            Recent recent = new Recent(currentPage);
            recent.add();
        } catch (Exception ex) {
            Log.e(TAG, "onViewCreated: ", ex);
        }
    }

    private void initPageContent() {

        setDisplaySong(currentPage);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter1 adapter = new PagerAdapter1(getSupportFragmentManager(), currentBook.count());

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPage.getId() - 1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = currentBook.getPage(position);
                checkFavoris(currentPage);
                toolbar.setSubtitle(currentPage.getNumber() + currentPage.getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        LogUtil.d();
    }

    private void setDisplaySong(Page currentPage) {

        try {

            toolbar.setSubtitle(currentPage.getNumber() + currentPage.getTitle());
            toolbar.setTitle(currentBook.getName());

            checkFavoris(currentPage);

        } catch (Exception ax) {

            Toast.makeText(this, "error fatal!", Toast.LENGTH_SHORT).show();
        }
    }

    public class PagerAdapter1 extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter1(FragmentManager fm, int NumOfTabs) {
            super(fm);
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

    @Override
    protected void onCreateFabMenu() {

        fabMenu = findViewById(R.id.menu_red);

        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fab4 = findViewById(R.id.fab4);

        fab1.setLabelText(getString(R.string.jaime));
        fab2.setLabelText(getString(R.string.share_song));
        fab3.setLabelText(getString(R.string.add_to_favorites));
        fab4.setLabelText(getString(R.string.corriger_erreur));

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

    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab1:
                    fab1.setImageDrawable(getResources().getDrawable(R.drawable.fav_full));
                    //addToLike(bookId, mSong.getId());
                    break;
                case R.id.fab2:

                    sendText(currentBook.getName() + "\n" + currentPage.getNumber() + currentPage.getTitle() + "\n\n" + currentPage.getContent());

                    break;
                case R.id.fab3:
                    //fab1.setVisibility(View.GONE);
                    fab1.setImageDrawable(getResources().getDrawable(R.drawable.fav_full));
                    fab3.setImageDrawable(getResources().getDrawable(R.drawable.star_filed));

                    addToFavoris(currentPage);

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


        if (id == R.id.action_find) {
            findItem();
            return true;
        }
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

    private void checkFavoris(Page currentPage) {
        if (Favoris.exists(currentPage)) {
            fab1.setImageDrawable(getResources().getDrawable(R.drawable.fav_full));
            fab3.setImageDrawable(getResources().getDrawable(R.drawable.star_filed));
        } else {
            fab1.setImageDrawable(getResources().getDrawable(R.drawable.fav));
            fab3.setImageDrawable(getResources().getDrawable(R.drawable.star0));
        }
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

                                        if (bookId == 1) {
                                            Book book = new CV();
                                            final List<Page> pageList = book.find("" + page);
                                            final List<String> numberList = new ArrayList<>();

                                            if (pageList.size() > 1) {

                                                for (Page page1 :
                                                        pageList) {
                                                    numberList.add(page1.getNumber());
                                                }
                                                new MaterialDialog.Builder(ItemActivity.this)
                                                        .title(text)
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

                                        } else {
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


}
