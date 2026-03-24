package com.davidkazad.chantlouange;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.davidkazad.chantlouange.ui.activities.HomeActivity;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.pixplicity.easyprefs.library.Prefs;

public class IntroSlider extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        SliderPage sliderPage1 = new SliderPage();
//        sliderPage1.setTitle("Collection de cantiques Nouvelle Edition");
//        sliderPage1.setDescription("Cantiques inspirés des frères et soeurs du Tabernacle Chretien de Lubumbashi et d'ailleurs, jugés être en harmonie avec la parole de l'Heure que nous croyons.");
//        sliderPage1.setImageDrawable(R.drawable.cc);
//        sliderPage1.setBgColor(Color.TRANSPARENT);

        //addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Only Believe");
        sliderPage2.setDescription("Song of worship Sung by William Marrion Branham.");
        sliderPage2.setImageDrawable(R.drawable.ob_piano_2);
        sliderPage2.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage2.setTitle("Crois Seulement");
        sliderPage2.setDescription("Version française de Only Believe");
        sliderPage2.setImageDrawable(R.drawable.book_cs);
        sliderPage2.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage2.setTitle("Bemba Hymns");
        sliderPage2.setDescription("Inyimbo Sha Kulumbanya Lesa.");
        sliderPage2.setImageDrawable(R.drawable.book_bb);
        sliderPage2.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Prefs.putBoolean("firstRun",true);
        startActivity(new Intent(IntroSlider.this, HomeActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Prefs.putBoolean("firstRun",true);
        startActivity(new Intent(IntroSlider.this, HomeActivity.class));
        finish();
    }
}
