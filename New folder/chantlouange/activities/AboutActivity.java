package com.davidkazad.chantlouange.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.davidkazad.chantlouange.R;

public class AboutActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }

    public void rate_me(View view) {
        openPlayStore();
    }

    public void share_me(View view) {
        shareApp();
    }

    public void github(View view) {
        openGithub();
    }

    public void licence(View view) {
        openLicence();
    }

    public void equipe(View view) {
        new MaterialDialog.Builder(AboutActivity.this)
                .title("** REMERCIEMENTS **")
                .items(R.array.contibutor)
                .show();

    }
}
