package com.davidkazad.chantlouange.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.activities.SongDetailsActivity;
import com.davidkazad.chantlouange.songs.SongsBook;
import com.davidkazad.chantlouange.songs.SongsItem;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;

import static com.davidkazad.chantlouange.common.Common.SongsError;
import static com.davidkazad.chantlouange.fragment.SongListFragment.TAG;
import static com.davidkazad.chantlouange.songs.SongsBook.cvException;

public class SongsItemFragment extends Fragment {


    private TextView textView;
    @BindView(R.id.edit) EditText editText;
    @BindView(R.id.annuler)
    Button buttonAnnuler;
    @BindView(R.id.envoyer) Button buttonEnvoyer;
    @BindView(R.id.layout_button)
    LinearLayout layoutButton;
    private SongsItem mSong;
    private Bundle args;

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

        try {

            args = getArguments();

            if (args != null) {

                mSong = SongsBook.getSong(args.getInt("bookId"), args.getInt("songId"));

                //toolbar.setTitle(mSong.getNumber() +". "+mSong.getTitle());
                textView.setMovementMethod(new ScrollingMovementMethod());
                textView.setText(mSong.getContent());
                textView.setTextSize(Prefs.getFloat("TextSize", 18));
                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        resizeTextDialog();
                        return false;
                    }
                });
            }
        } catch (Exception ax) {

            Toast.makeText(getContext(), "Numero introvable", Toast.LENGTH_SHORT).show();
        }

    }

    public static SongsItemFragment getInstance(int bookId, int songId) {
        SongsItemFragment fragment = new SongsItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("bookId", bookId);
        bundle.putInt("songId", songId);
        bundle.putString("songTitle", "SongItem : " + songId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_find:

                find();
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
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Taille du Text"); //Message here

        final SeekBar seekBar = new SeekBar(getContext());
        seekBar.setProgress((int) textView.getTextSize());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setTextSize(progress/2);
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

                Prefs.putFloat("TextSize", textView.getTextSize());
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
    private void find(){

        new MaterialDialog.Builder(getContext())
                .title("Rechercher dans")
                .items(R.array.book_list)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, final int which, CharSequence text) {

                        new MaterialDialog.Builder(getContext())
                                .title(text)
                                .input(R.string.number, R.string.action_search, false, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                        SongsItem item ;

                                        if (which == 1) {

                                            cvException(input);
                                            item = SongsBook.getSong(which + 1, cvException(input));
                                        }else
                                            item = SongsBook.getSong(which + 1, Integer.valueOf(String.valueOf(input)) - 1);

                                        if (item != null) {
                                            SongDetailsActivity.mItem = item;
                                            SongDetailsActivity.bookId = which+1;
                                            startActivity(new Intent(getContext(),SongDetailsActivity.class));
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getContext(), "Ce numero n'exist pas !", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                .inputType(InputType.TYPE_CLASS_NUMBER)
                                .negativeText(R.string.cancel)
                                .positiveText(R.string.research)
                                .show();
                        return true; // allow selection
                    }
                })
                .show();

    }
}
