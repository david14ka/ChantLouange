package com.davidkazad.chantlouange.ui.activities;

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

    private Switch switchKeepScreenOn;

    private final int[] fontSizes = {14, 18, 22, 26};
    private final String[] fontSizeLabels = {"Small", "Medium", "Large", "Extra Large"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationDrawer(savedInstanceState,null);

        switchKeepScreenOn = findViewById(R.id.switch_keep_screen_on);

        float currentSize = Prefs.getFloat("TextSize", 18);
        int initialProgress = 1; // Default to Medium (18)
        for (int i = 0; i < fontSizes.length; i++) {
            if (fontSizes[i] == (int) currentSize) {
                initialProgress = i;
                break;
            }
        }

        seekBar.setProgress(initialProgress);
        updateFontSizeDisplay(initialProgress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Prefs.putFloat("TextSize", fontSizes[progress]);
                updateFontSizeDisplay(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        aSwitch.setChecked(Prefs.getBoolean("night_mode", false));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.putBoolean("night_mode", isChecked);
            }
        });

        if (switchKeepScreenOn != null) {
            switchKeepScreenOn.setChecked(Prefs.getBoolean("keep_screen_on", false));
            switchKeepScreenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Prefs.putBoolean("keep_screen_on", isChecked);
                }
            });
        }
    }

    private void updateFontSizeDisplay(int progress) {
        txt_progress.setText(fontSizeLabels[progress]);
        sampleText.setTextSize(fontSizes[progress]);
    }
}
