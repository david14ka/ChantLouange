package com.davidkazad.chantlouange.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.ui.activities.SettingsActivity;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.config.utils.LogUtil;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;

public class ItemFragment extends BaseFragment {

    private static final String TAG = "ItemFragment";
    private static final String EXTRA_PAGE_ID = "pageId";
    public static Book currentBook;

    private TextView textView;
    @BindView(R.id.edit)
    EditText editText;
    @BindView(R.id.annuler)
    Button buttonAnnuler;
    @BindView(R.id.envoyer)
    Button buttonEnvoyer;
    @BindView(R.id.layout_button)
    LinearLayout layoutButton;
    private Page currentPage;
    private RelativeLayout layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.text);
        layout = view.findViewById(R.id.layout);
        try {

            Bundle args = getArguments();
            if (args != null) {
                currentPage = currentBook.getPage(args.getInt(EXTRA_PAGE_ID));
                initPageContent();
            }

        } catch (Exception ax) {

            Toast.makeText(getContext(), R.string.numero_introuvable, Toast.LENGTH_SHORT).show();
        }

        initTheme();

        LogUtil.d();
    }

    private void initPageContent() {

        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(currentPage.getContent());
        textView.setTextSize(Prefs.getFloat("TextSize", 18));
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                resizeTextDialog();
                return false;
            }
        });

    }

    public static ItemFragment getInstance(int songId) {
        ItemFragment fragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_PAGE_ID, songId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item2, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_textSize:
                resizeTextDialog();
                return true;

            case R.id.action_settings:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            case R.id.action_help:
                openHelp();
                return true;

            default:
                break;
        }

        return false;
    }

    private void changeTheme() {
        if (Prefs.getBoolean("night_mode", false)) {
            Prefs.putBoolean("night_mode",false);
        } else {
            Prefs.putBoolean("night_mode", true);
        }
    }

    private void initTheme() {
        if (Prefs.getBoolean("night_mode",false)){
            //layout.setBackgroundColor(getResources().getColor(R.color.bg_day));
            layout.setBackgroundResource(R.drawable.day_heaven);
            textView.setBackgroundColor(getResources().getColor(R.color.bg_day_trans));
            textView.setTextColor(Color.BLACK);
            //textView.setTypeface(Typeface.MONOSPACE);
        }else {
            layout.setBackgroundColor(getResources().getColor(R.color.transparent));
            textView.setTextColor(getResources().getColor(R.color.white));

        }
    }

    private void resizeTextDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(R.string.text_size); //Message here

        final SeekBar seekBar = new SeekBar(getContext());
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


        alert.setPositiveButton(R.string.enregistrer, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Prefs.putFloat("TextSize", seekBar.getProgress());
                dialog.cancel();
            }
        });

        alert.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();

        alertDialog.show();
    }

}
