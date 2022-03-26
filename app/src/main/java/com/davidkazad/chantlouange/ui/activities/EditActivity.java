package com.davidkazad.chantlouange.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.davidkazad.chantlouange.config.Common.SongsError;

public class EditActivity extends BaseActivity {

    public static Page mItem;
    public static int bookId;
    public static Book mBook;
    @BindView(R.id.text)
    TextView textView;
    private Toolbar toolbar;
    @BindView(R.id.edit) EditText editText;
    @BindView(R.id.annuler)
    Button buttonAnnuler;
    @BindView(R.id.envoyer) Button buttonEnvoyer;
    @BindView(R.id.layout_button)
    LinearLayout layoutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setDisplaySongs();

        setFabMenu();

        if (getIntent().getBooleanExtra("edit",false)){
            corrigerErreur();
        }

        navigationDrawer(savedInstanceState, null);


    }
    private void setDisplaySongs(){
        try {


            toolbar.setSubtitle(mItem.getNumber() + mItem.getTitle());
            textView.setText(mItem.getContent());
            editText.setText(mItem.getContent());
            textView.setTextSize(Prefs.getFloat("TextSize", 18));
            editText.setTextSize(Prefs.getFloat("TextSize", 18));
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    resizeTextDialog();
                    return false;
                }
            });

            toolbar.setTitle(mBook.getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } catch (Exception ax) {

            Toast.makeText(this, "Numero introvable", Toast.LENGTH_SHORT).show();
        }

    }

    private FloatingActionMenu fabMenu;
    private Handler mUiHandler = new Handler();
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;


    private void setFabMenu() {

        fabMenu = findViewById(R.id.menu_red);

        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fab4 = findViewById(R.id.fab4);

        fabMenu.showMenuButton(false);
        fabMenu.setClosedOnTouchOutside(true);

        fab1.setOnClickListener(fabClickListener);
        fab2.setOnClickListener(fabClickListener);
        fab3.setOnClickListener(fabClickListener);
        fab4.setOnClickListener(fabClickListener);

        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                fabMenu.showMenuButton(true);
            }
        }, 400);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            createCustomAnimation();
        }

        fabMenu.getMenuIconView().setImageResource(fabMenu.isOpened()
                ? R.drawable.ic_close : R.drawable.ic_menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void createCustomAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fabMenu.getMenuIconView().setImageResource(fabMenu.isOpened()
                        ? R.drawable.ic_menu : R.drawable.ic_close);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        fabMenu.setIconToggleAnimatorSet(set);
    }

    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab1:
                    fab1.setImageDrawable(getResources().getDrawable(R.drawable.fav_full));
                    addToLike(bookId, mItem.getId());
                    break;
                case R.id.fab2:

                    sendText(mItem.getNumber()+mItem.getTitle()+"\n"+mItem.getContent());

                    break;
                case R.id.fab3:
                    //fab1.setVisibility(View.GONE);
                    fab1.setImageDrawable(getResources().getDrawable(R.drawable.fav_full));
                    fab3.setImageDrawable(getResources().getDrawable(R.drawable.star_filed));

                    addToFavoris(mItem);

                    break;

                case R.id.fab4:
                    corrigerErreur();
                    fabMenu.hideMenu(true);
                    break;
            }
        }
    };

    private void corrigerErreur() {
        textView.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
        editText.setTextSize(Prefs.getFloat("TextSize",22));
        editText.setText(String.format("%s%s\n\n%s", mItem.getNumber(), mItem.getTitle(), mItem.getContent()));
        layoutButton.setVisibility(View.VISIBLE);
        fabMenu.hideMenu(true);
        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
                layoutButton.setVisibility(View.GONE);
                fabMenu.showMenu(true);
            }
        });

        buttonEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mItem.setContent(editText.getText().toString());
                SongsError.push().setValue(mItem);

                Toast.makeText(EditActivity.this, R.string.thanks, Toast.LENGTH_SHORT).show();

                finish();
                textView.setText(editText.getText().toString());
                textView.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
                layoutButton.setVisibility(View.GONE);
                fabMenu.showMenu(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.action_find:

                //findSongDialog();
                return true;

            case R.id.action_textSize:

                resizeTextDialog();
                return true;

            default:
                break;
        }

        return false;
    }

    private void resizeTextDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(EditActivity.this);
        alert.setMessage(R.string.text_size); //Message here

        final SeekBar seekBar = new SeekBar(EditActivity.this);
        seekBar.setProgress((int) Prefs.getFloat("TextSize", 18));


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                textView.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        alert.setView(seekBar);


        alert.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Prefs.putFloat("TextSize", seekBar.getProgress());
                dialog.cancel();
            }
        });

        alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();

        alertDialog.show();
    }
}
