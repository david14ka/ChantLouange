package com.davidkazad.chantlouange.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.fragment.SongListFragment;
import com.davidkazad.chantlouange.fragment.SongsItemFragment;
import com.davidkazad.chantlouange.models.Favoris;
import com.davidkazad.chantlouange.songs.SongsBook;
import com.davidkazad.chantlouange.songs.SongsItem;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class SlideDetails extends BaseActivity {
    private static final String TAG = SongListFragment.class.getName();

    public static int songId;
    public static int bookId;
    private Toolbar toolbar;
    private Activity main;
    private SongsItem mSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_details);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("List des chants");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        navigationDrawer(savedInstanceState, null);
        onCreateFabMenu();

        main = this;

        setDisplaySong(bookId, songId);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter1 adapter = new PagerAdapter1(getSupportFragmentManager(), SongsBook.getSongBook(bookId - 1).getTitle().length);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(songId);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSong = SongsBook.getSong(bookId, position);
                toolbar.setSubtitle(mSong.getNumber() + mSong.getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

            return SongsItemFragment.getInstance(bookId, position);

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
                    addToLike(bookId, mSong.getId());
                    break;
                case R.id.fab2:

                    sendText(mSong.getNumber() + mSong.getTitle() + "\n\n" + mSong.getContent());

                    break;
                case R.id.fab3:
                    //fab1.setVisibility(View.GONE);
                    fab1.setImageDrawable(getResources().getDrawable(R.drawable.fav_full));
                    fab3.setImageDrawable(getResources().getDrawable(R.drawable.star_filed));

                    addToFavoris(bookId, songId);

                    break;

                case R.id.fab4:
                    SongDetailsActivity.mItem = SongsBook.getSong(bookId, songId);
                    SongDetailsActivity.bookId = bookId;
                    startActivity(new Intent(getApplicationContext(), SongDetailsActivity.class).putExtra("edit", true));

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
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_help) {

            openHelp();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void checkFavoris() {
        if (Favoris.isAdded(bookId, songId)) {
            fab1.setImageDrawable(getResources().getDrawable(R.drawable.fav_full));
            fab3.setImageDrawable(getResources().getDrawable(R.drawable.star_filed));
        } else {
            fab1.setImageDrawable(getResources().getDrawable(R.drawable.fav));
            fab3.setImageDrawable(getResources().getDrawable(R.drawable.star));
        }
    }

    private void setDisplaySong(int bookId, int songId) {
        try {
            mSong = SongsBook.getSong(bookId, songId);
            toolbar.setSubtitle(mSong.getNumber() + mSong.getTitle());

            SongsBook book = SongsBook.getSongBook(bookId - 1);
            toolbar.setTitle(book.getSongsBookName());

            checkFavoris();

        } catch (Exception ax) {
            Toast.makeText(this, "error fatal!", Toast.LENGTH_SHORT).show();
        }
    }

}
