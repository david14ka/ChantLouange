package com.davidkazad.chantlouange.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.pixplicity.easyprefs.library.Prefs;

public class SettingsActivity extends BaseActivity {

    private Switch switchDarkMode;
    private View btnFontSize, btnReset, btnPrivacy, btnTerms;
    private TextView txtFontSizeDesc;
    private TextView tabFr, tabEn, tabSw;

    private final int[] fontSizes = {14, 18, 22, 26};
    private final String[] fontSizeLabels = {"Small (14px)", "Standard (18px)", "Large (22px)", "Extra Large (26px)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        // Remove navigation drawer link here since Settings usually exists on top

        switchDarkMode = findViewById(R.id.switch_dark_mode);
        btnFontSize = findViewById(R.id.btn_font_size);
        txtFontSizeDesc = findViewById(R.id.txt_font_size_desc);
        
        tabFr = findViewById(R.id.tab_lang_fr);
        tabEn = findViewById(R.id.tab_lang_en);
        tabSw = findViewById(R.id.tab_lang_sw);
        
        btnReset = findViewById(R.id.btn_reset);
        btnPrivacy = findViewById(R.id.btn_privacy);
        btnTerms = findViewById(R.id.btn_terms);

        // Load & bind font size
        float currentSize = Prefs.getFloat("TextSize", 18);
        int initialProgress = 1;
        for (int i = 0; i < fontSizes.length; i++) {
            if (fontSizes[i] == (int) currentSize) {
                initialProgress = i;
                break;
            }
        }
        updateFontSizeDisplay(initialProgress);

        btnFontSize.setOnClickListener(v -> {
            float cSize = Prefs.getFloat("TextSize", 18);
            int selectedIndex = 1;
            for (int i = 0; i < fontSizes.length; i++) {
                if (fontSizes[i] == (int) cSize) {
                    selectedIndex = i;
                    break;
                }
            }
            
            new MaterialDialog.Builder(this)
                    .title("Default Font Size")
                    .items(fontSizeLabels)
                    .itemsCallbackSingleChoice(selectedIndex, (dialog, view, which, text) -> {
                        Prefs.putFloat("TextSize", fontSizes[which]);
                        updateFontSizeDisplay(which);
                        return true; 
                    })
                    .show();
        });

        // Load & bind dark mode
        switchDarkMode.setChecked(Prefs.getBoolean("night_mode", false));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Prefs.putBoolean("night_mode", isChecked);
            // In a real scenario we'd trigger recreate(), but the legacy app just triggers it on re-launch
        });

        // Load & bind language
        String lang = Prefs.getString("app_language", "fr");
        updateLanguageTabs(lang);
        
        tabFr.setOnClickListener(v -> setLanguage("fr"));
        tabEn.setOnClickListener(v -> setLanguage("en"));
        tabSw.setOnClickListener(v -> setLanguage("sw"));

        // Links
        btnPrivacy.setOnClickListener(v -> Toast.makeText(this, "Privacy Policy linked", Toast.LENGTH_SHORT).show());
        btnTerms.setOnClickListener(v -> Toast.makeText(this, "Terms of Service linked", Toast.LENGTH_SHORT).show());
        
        // Reset
        btnReset.setOnClickListener(v -> {
            Prefs.putBoolean("night_mode", false);
            Prefs.putFloat("TextSize", 18);
            Prefs.putString("app_language", "fr");
            
            Toast.makeText(this, "Settings Reset", Toast.LENGTH_SHORT).show();
            
            // Reload UI
            switchDarkMode.setChecked(false);
            updateFontSizeDisplay(1);
            updateLanguageTabs("fr");
        });
    }

    private void updateFontSizeDisplay(int index) {
        txtFontSizeDesc.setText(fontSizeLabels[index]);
    }
    
    private void setLanguage(String code) {
        Prefs.putString("app_language", code);
        updateLanguageTabs(code);
    }
    
    private void updateLanguageTabs(String activeLang) {
        tabFr.setBackgroundResource(activeLang.equals("fr") ? R.drawable.pill_active_bg : R.drawable.pill_inactive_bg);
        tabEn.setBackgroundResource(activeLang.equals("en") ? R.drawable.pill_active_bg : R.drawable.pill_inactive_bg);
        tabSw.setBackgroundResource(activeLang.equals("sw") ? R.drawable.pill_active_bg : R.drawable.pill_inactive_bg);
    }
}
