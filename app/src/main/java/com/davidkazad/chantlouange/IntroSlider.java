package com.davidkazad.chantlouange;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.pixplicity.easyprefs.library.Prefs;

public class IntroSlider extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("Collection des cantiques");
        sliderPage1.setDescription("Cantiques inspirés des frères et soeurs du Tabernacle Chretien de Lubumbashi et d'ailleurs, jugés être en harmonie avec la parole de l'Heure que nous croyons.");
        sliderPage1.setImageDrawable(R.drawable.bground);
        sliderPage1.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Chant de victoire");
        sliderPage2.setDescription("RECUEIL DE CANTIQUES\nPour reunion d'évangelisation et d'édification");
        //, mission de réveil
        // Nous sommes plus ques vainqueurs
        //Par Christ qui nous a aimé.	Rom. 8. 37
        sliderPage2.setImageDrawable(R.drawable.guitar_3283649_640);
        sliderPage2.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("Nyimbo za mungu");
        sliderPage3.setDescription("");
        sliderPage3.setImageDrawable(R.drawable.book_3757523_640);
        sliderPage3.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Nyimbo za wokovu");
        sliderPage4.setDescription("Communauté des Eglises de Pentecôte \nen Afrique Centrale");
        sliderPage4.setImageDrawable(R.drawable.book_3755514_640);
        sliderPage4.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage4));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Prefs.putBoolean("firstRun",true);
        startActivity(new Intent(IntroSlider.this, MainActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Prefs.putBoolean("firstRun",true);
        startActivity(new Intent(IntroSlider.this, MainActivity.class));
        finish();
    }
}
