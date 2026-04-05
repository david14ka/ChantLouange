package com.davidkazad.chantlouange.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.ui.activities.HomeActivity;
import com.pixplicity.easyprefs.library.Prefs;

public class SettingsFragment extends Fragment {

    private Switch switchDarkMode;
    private View btnFontSize, btnReset, btnPrivacy, btnTerms;
    private TextView txtFontSizeDesc;
    private TextView tabFr, tabEn;

    private final int[] fontSizes = {14, 18, 22, 26};

    private String[] getFontSizeLabels() {
        return new String[]{
                getString(R.string.settings_font_size_petit),
                getString(R.string.settings_font_size_standard),
                getString(R.string.settings_font_size_grand),
                getString(R.string.settings_font_size_extra)
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        btnFontSize = view.findViewById(R.id.btn_font_size);
        txtFontSizeDesc = view.findViewById(R.id.txt_font_size_desc);
        
        tabFr = view.findViewById(R.id.tab_lang_fr);
        tabEn = view.findViewById(R.id.tab_lang_en);
        
        btnReset = view.findViewById(R.id.btn_reset);
        btnPrivacy = view.findViewById(R.id.btn_privacy);
        btnTerms = view.findViewById(R.id.btn_terms);

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
            
            new MaterialDialog.Builder(getContext())
                    .title(R.string.settings_font_size_label)
                    .items(getFontSizeLabels())
                    .itemsCallbackSingleChoice(selectedIndex, (dialog, view1, which, text) -> {
                        Prefs.putFloat("TextSize", fontSizes[which]);
                        updateFontSizeDisplay(which);
                        return true; 
                    })
                    .show();
        });

        // Load & bind dark mode
        switchDarkMode.setChecked(Prefs.getBoolean("night_mode", true));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Prefs.putBoolean("night_mode", isChecked);
            AppCompatDelegate.setDefaultNightMode(isChecked ? 
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Load & bind language
        String lang = Prefs.getString("app_language", "fr");
        updateLanguageTabs(lang);
        
        tabFr.setOnClickListener(v -> setLanguage("fr"));
        tabEn.setOnClickListener(v -> setLanguage("en"));

        // Links
        btnPrivacy.setOnClickListener(v -> Toast.makeText(getContext(), R.string.settings_privacy_label, Toast.LENGTH_SHORT).show());
        btnTerms.setOnClickListener(v -> Toast.makeText(getContext(), R.string.settings_terms_label, Toast.LENGTH_SHORT).show());
        
        // Reset
        btnReset.setOnClickListener(v -> {
            Prefs.putBoolean("night_mode", false);
            Prefs.putFloat("TextSize", 18);
            Prefs.putString("app_language", "fr");
            
            Toast.makeText(getContext(), R.string.settings_reset_confirm, Toast.LENGTH_SHORT).show();
            
            if (getActivity() != null) getActivity().recreate();
        });

        return view;
    }

    private void updateFontSizeDisplay(int index) {
        txtFontSizeDesc.setText(getFontSizeLabels()[index]);
    }
    
    private void setLanguage(String code) {
        String currentLang = Prefs.getString("app_language", "fr");
        if (currentLang.equals(code)) return;
        
        Prefs.putString("app_language", code);
        updateLanguageTabs(code);
        
        // Recreate to apply changes
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
    
    private void updateLanguageTabs(String activeLang) {
        tabFr.setBackgroundResource(activeLang.equals("fr") ? R.drawable.pill_active_bg : R.drawable.pill_inactive_bg);
        tabEn.setBackgroundResource(activeLang.equals("en") ? R.drawable.pill_active_bg : R.drawable.pill_inactive_bg);
    }
}
