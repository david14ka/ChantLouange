package com.davidkazad.chantlouange.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.davidkazad.chantlouange.R;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroBase;
import com.github.paolorotolo.appintro.util.LogHelper;

import java.util.ArrayList;

abstract class CustomIntro extends AppIntroBase {

    protected android.support.v7.widget.Toolbar toolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.custom_appintro;
    }

    private static final String TAG = LogHelper.makeLogTag(AppIntro2.class);

    protected View customBackgroundView;
    protected FrameLayout backgroundFrame;
    private ArrayList<Integer> transitionColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backgroundFrame = findViewById(com.github.paolorotolo.appintro.R.id.background);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pager.setCurrentItem(3);
    }

    /**
     * Shows or hides Done button, replaced with setProgressButtonEnabled
     *
     * @deprecated use {@link #setProgressButtonEnabled(boolean)} instead.
     */
    @Deprecated
    public void showDoneButton(boolean showDone) {
        setProgressButtonEnabled(showDone);
    }

    /**
     * Override viewpager bar color
     *
     * @param color your color resource
     */
    public void setBarColor(@ColorInt final int color) {
        LinearLayout bottomBar = findViewById(com.github.paolorotolo.appintro.R.id.bottom);
        bottomBar.setBackgroundColor(color);
    }

    /**
     * Override Next button
     *
     * @param imageSkipButton your drawable resource
     */
    public void setImageSkipButton(final Drawable imageSkipButton) {
        final ImageButton nextButton = findViewById(R.id.skip);
        nextButton.setImageDrawable(imageSkipButton);

    }

    public void setBackgroundView(View view) {
        customBackgroundView = view;
        if (customBackgroundView != null) {
            backgroundFrame.addView(customBackgroundView);
        }
    }

    /**
     * For color transition, will be shown only if color values are properly set;
     * Size of the color array must be equal to the number of slides added
     *
     * @param colors Set color values
     */
    public void setAnimationColors(ArrayList<Integer> colors) {
        transitionColors = colors;
    }
}
