package com.davidkazad.chantlouange.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.fragment.BaseFragment;
import com.davidkazad.chantlouange.fragment.SongListFragment;
import com.davidkazad.chantlouange.fragment.SongsItemFragment;
import com.davidkazad.chantlouange.songs.SongsBook;
import com.pixplicity.easyprefs.library.Prefs;

import static com.davidkazad.chantlouange.activities.SongListActivity.songBookId;

public class SearchFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.search_view,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("C.C"));
        tabLayout.addTab(tabLayout.newTab().setText("C.V"));
        tabLayout.addTab(tabLayout.newTab().setText("N.M"));
        tabLayout.addTab(tabLayout.newTab().setText("N.W"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

        viewPager.setCurrentItem(1);

        PagerAdapter1 adapter = new PagerAdapter1(getChildFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        int tabIndex = 1;
        tabLayout.setScrollPosition(tabIndex, 0f, true);
        viewPager.setCurrentItem(tabIndex);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

    }

    public static class PagerAdapter1 extends FragmentStatePagerAdapter {
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
}
