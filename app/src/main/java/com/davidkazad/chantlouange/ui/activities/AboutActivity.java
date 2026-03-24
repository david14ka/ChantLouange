package com.davidkazad.chantlouange.ui.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


import com.afollestad.materialdialogs.MaterialDialog;
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

        TextView vcode = findViewById(R.id.about_version_text);

        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo("com.davidkazad.chantlouange", 0);
            String versionName = info.versionName;
            int versionCode = info.versionCode; // deprecated in API 28, use info.getLongVersionCode()
            vcode.setText(String.format("%s%s", getString(R.string.version_2), versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        navigationDrawer(savedInstanceState,null);
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

    public void donation(View view) {

        shareApp();
        //startActivity(new Intent(getApplicationContext(), DonationActivity.class));
    }

    public void equipe(View view) {
        new MaterialDialog.Builder(AboutActivity.this)
                .title(R.string.remerciement)
                .items(R.array.contributor)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                    }
                })
                .show();

    }

    public void alive(View view) {
        openUrl("alivecorp.com");
    }

    public void me(View view) {
    }
}
