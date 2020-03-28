package com.davidkazad.chantlouange.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.davidkazad.chantlouange.R;


public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about);
    }
}
