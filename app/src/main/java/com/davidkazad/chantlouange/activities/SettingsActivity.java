package com.davidkazad.chantlouange.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.davidkazad.chantlouange.R;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingsActivity extends BaseActivity {

    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.txtSize)
    TextView txt_progress;
    @BindView(R.id.sample) TextView sampleText;

    @BindView(R.id.switch1)
    Switch aSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationDrawer(savedInstanceState,null);


        seekBar.setProgress((int) Prefs.getFloat("TextSize", 18));
        txt_progress.setText(String.format(getString(R.string.seek_progress), (int) Prefs.getFloat("TextSize", 18)));
        sampleText.setTextSize((int) Prefs.getFloat("TextSize", 18));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Prefs.putFloat("TextSize", seekBar.getProgress());
                txt_progress.setText(String.format(getString(R.string.seek_progress), progress));
                sampleText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

        }
        });

        aSwitch.setChecked(Prefs.getBoolean("night_mode",false));

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.putBoolean("night_mode",isChecked);
            }
        });
    }
}
