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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemFragment extends BaseFragment {

    private static final String TAG = "ItemFragment";
    private static final String EXTRA_PAGE_ID = "pageId";
    public static Book currentBook;

    private LinearLayout lyricsContainer;
    private TextView txtTitle, txtWatermark, txtCategory, txtAuthor, txtKey;
    private float currentTextSize = 18f;

    private Page currentPage;

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

        lyricsContainer = view.findViewById(R.id.lyrics_container);
        txtTitle = view.findViewById(R.id.txt_title);
        txtWatermark = view.findViewById(R.id.txt_watermark_number);
        txtCategory = view.findViewById(R.id.txt_category);
        txtAuthor = view.findViewById(R.id.txt_author);
        txtKey = view.findViewById(R.id.txt_key);
        
        currentTextSize = Prefs.getFloat("TextSize", 18f);

        // Control Pill Buttons
        view.findViewById(R.id.btn_text_plus).setOnClickListener(v -> zoomText(2f));
        view.findViewById(R.id.btn_text_minus).setOnClickListener(v -> zoomText(-2f));

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
        if (currentPage == null) return;
        
        txtTitle.setText(currentPage.getTitle());
        txtWatermark.setText(currentPage.getNumber());
        txtCategory.setText(currentBook.getName() != null ? currentBook.getName().toUpperCase() : "");

        String author = currentPage.getReference();
        if (author != null && !author.trim().isEmpty()) {
            txtAuthor.setText(author);
            txtAuthor.setVisibility(View.VISIBLE);
            if(getView() != null) getView().findViewById(R.id.ic_author).setVisibility(View.VISIBLE);
        } else {
            txtAuthor.setVisibility(View.GONE);
            if(getView() != null) getView().findViewById(R.id.ic_author).setVisibility(View.GONE);
        }
        
        txtKey.setVisibility(View.GONE);
        if(getView() != null) getView().findViewById(R.id.ic_key).setVisibility(View.GONE);

        parseAndRenderLyrics();
    }
    
    private void parseAndRenderLyrics() {
        lyricsContainer.removeAllViews();
        String content = currentPage.getContent();
        if (content == null) return;

        String[] stanzas = content.split("\n\\s*\n");
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String stanza : stanzas) {
            stanza = stanza.trim();
            if (stanza.isEmpty()) continue;

            if (stanza.toUpperCase().startsWith("REFRAIN") || stanza.toUpperCase().startsWith("CHOEUR")) {
                String text = stanza.replaceAll("(?i)^(REFRAIN|CHOEUR)\\s*:?\\s*", "").trim();
                
                View refrainView = inflater.inflate(R.layout.item_lyric_refrain, lyricsContainer, false);
                TextView txtRefrain = refrainView.findViewById(R.id.txt_refrain_content);
                txtRefrain.setText(text);
                txtRefrain.setTextSize(currentTextSize);
                lyricsContainer.addView(refrainView);
            } else {
                View verseView = inflater.inflate(R.layout.item_lyric_verse, lyricsContainer, false);
                TextView txtContent = verseView.findViewById(R.id.txt_verse_content);
                TextView txtNumber = verseView.findViewById(R.id.txt_verse_number);

                Matcher m = Pattern.compile("^(\\d+\\.)\\s*(.*)", Pattern.DOTALL).matcher(stanza);
                if (m.matches()) {
                    txtNumber.setText(m.group(1));
                    txtNumber.setVisibility(View.VISIBLE);
                    txtContent.setText(m.group(2).trim());
                } else {
                    txtNumber.setVisibility(View.GONE);
                    txtContent.setText(stanza);
                }
                
                txtContent.setTextSize(currentTextSize);
                lyricsContainer.addView(verseView);
            }
        }
    }
    
    private void zoomText(float diff) {
        currentTextSize += diff;
        if (currentTextSize < 12f) currentTextSize = 12f;
        if (currentTextSize > 40f) currentTextSize = 40f;
        Prefs.putFloat("TextSize", currentTextSize);
        parseAndRenderLyrics();
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
        // Obsolete UI bindings removed to prevent crash. Let default dark theme dominate.
    }

    private void resizeTextDialog() {
       // Replaced by inline control pill buttons
    }

}
