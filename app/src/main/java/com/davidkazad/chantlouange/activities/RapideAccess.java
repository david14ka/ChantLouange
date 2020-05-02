package com.davidkazad.chantlouange.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.davidkazad.chantlouange.R;

public class RapideAccess extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapide_access);

        findItem();

    }

    public void dash(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}
