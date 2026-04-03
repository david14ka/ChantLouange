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
import android.os.Handler;
import android.util.Log;
import android.media.MediaPlayer;
import android.widget.ProgressBar;
import android.widget.ImageButton;
import com.davidkazad.chantlouange.utils.AudioMapper;
import java.util.List;
import java.io.IOException;

public class ItemFragment extends BaseFragment {

    private static final String TAG = "ItemFragment";
    private static final String EXTRA_PAGE_ID = "pageId";
    public static Book currentBook;

    private LinearLayout lyricsContainer;
    private TextView txtTitle, txtWatermark, txtCategory, txtAuthor, txtKey;
    private float currentTextSize = 18f;

    private Page currentPage;

    // Audio Player Fields
    private View audioPlayerCard;
    private TextView btnAudioVersion;
    private ProgressBar audioLoading;
    private ImageButton btnAudioPlay;
    private TextView txtAudioCurrent;
    private TextView txtAudioDuration;
    private SeekBar audioSeekbar;
    private MediaPlayer mediaPlayer;
    private List<String> audioUrls;
    private int currentAudioVersionIndex = 0;
    private Handler audioHandler = new Handler();
    private boolean isTrackingUserSeek = false;
    private Runnable updateSeekbarTask = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying() && !isTrackingUserSeek) {
                int currentPos = mediaPlayer.getCurrentPosition();
                audioSeekbar.setProgress(currentPos);
                txtAudioCurrent.setText(formatTime(currentPos));
            }
            audioHandler.postDelayed(this, 1000);
        }
    };

    private String formatTime(int ms) {
        int sec = ms / 1000;
        int m = sec / 60;
        int s = sec % 60;
        return String.format("%d:%02d", m, s);
    }

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
        View btnPlus = view.findViewById(R.id.btn_text_plus);
        if (btnPlus != null) btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomText(2f);
            }
        });
        
        View btnMinus = view.findViewById(R.id.btn_text_minus);
        if (btnMinus != null) btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomText(-2f);
            }
        });
        
        // Share Pill Button
        final View btnShare = view.findViewById(R.id.btn_share);
        if (btnShare != null) {
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPage != null && currentBook != null) {
                        String shareBody = currentBook.getName() + "\n" + currentPage.getNumber() + " " + currentPage.getTitle() + "\n\n" + currentPage.getContent();
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, currentPage.getTitle());
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                }
            });
        }
        // Theme Pill Button
        final View btnTheme = view.findViewById(R.id.btn_theme);
        if (btnTheme != null) {
            btnTheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTheme();
                    initTheme();
                    parseAndRenderLyrics();
                }
            });
        }

        // --- Audio Player Initialization ---
        audioPlayerCard = view.findViewById(R.id.audio_player_card);
        btnAudioVersion = view.findViewById(R.id.btn_audio_version);
        audioLoading = view.findViewById(R.id.audio_loading);
        btnAudioPlay = view.findViewById(R.id.btn_audio_play);
        txtAudioCurrent = view.findViewById(R.id.txt_audio_current);
        txtAudioDuration = view.findViewById(R.id.txt_audio_duration);
        audioSeekbar = view.findViewById(R.id.audio_seekbar);
        
        setupAudioPlayerUi();

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

        initAudio();

        parseAndRenderLyrics();
    }

    private void initAudio() {
        if (currentBook == null || currentPage == null) return;
        
        audioUrls = AudioMapper.getAudioUrls(currentBook.getId(), currentPage.getNumber());
        if (audioUrls == null || audioUrls.isEmpty()) {
            if (audioPlayerCard != null) audioPlayerCard.setVisibility(View.GONE);
            return;
        }
        
        if (audioPlayerCard != null) audioPlayerCard.setVisibility(View.VISIBLE);
        currentAudioVersionIndex = 0;
        updateVersionButtonText();
        
        resetPlayer();
    }

    private void setupAudioPlayerUi() {
        if (audioPlayerCard == null) return;
        
        btnAudioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });
        
        btnAudioVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioUrls != null && audioUrls.size() > 1) {
                    currentAudioVersionIndex = (currentAudioVersionIndex + 1) % audioUrls.size();
                    updateVersionButtonText();
                    boolean wasPlaying = mediaPlayer != null && mediaPlayer.isPlaying();
                    resetPlayer();
                    if (wasPlaying) {
                        togglePlay();
                    }
                } else {
                    Toast.makeText(getContext(), "Seule cette version est disponible.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        audioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && txtAudioCurrent != null) {
                    txtAudioCurrent.setText(formatTime(progress));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingUserSeek = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTrackingUserSeek = false;
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    private void updateVersionButtonText() {
        if (audioUrls != null && !audioUrls.isEmpty() && btnAudioVersion != null) {
            String url = audioUrls.get(currentAudioVersionIndex).toLowerCase();
            if (url.contains("piano")) {
                btnAudioVersion.setText("🎹 Piano");
            } else {
                btnAudioVersion.setText("🎻 Orchestre");
            }
        }
    }

    private void resetPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
        if (btnAudioPlay != null) {
            btnAudioPlay.setImageResource(android.R.drawable.ic_media_play);
            btnAudioPlay.setVisibility(View.VISIBLE);
        }
        if (audioLoading != null) audioLoading.setVisibility(View.GONE);
        if (audioSeekbar != null) {
            audioSeekbar.setProgress(0);
            audioSeekbar.setMax(0);
        }
        if (txtAudioCurrent != null) txtAudioCurrent.setText("0:00");
        if (txtAudioDuration != null) txtAudioDuration.setText("0:00");
        audioHandler.removeCallbacks(updateSeekbarTask);
    }
    
    private void togglePlay() {
        if (audioUrls == null || audioUrls.isEmpty() || getContext() == null) return;
        
        if (mediaPlayer == null) {
            // Start loading and playing
            btnAudioPlay.setVisibility(View.GONE);
            audioLoading.setVisibility(View.VISIBLE);
            
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrls.get(currentAudioVersionIndex));
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (getActivity() == null) return;
                        btnAudioPlay.setVisibility(View.VISIBLE);
                        audioLoading.setVisibility(View.GONE);
                        
                        audioSeekbar.setMax(mp.getDuration());
                        txtAudioDuration.setText(formatTime(mp.getDuration()));
                        
                        mp.start();
                        btnAudioPlay.setImageResource(android.R.drawable.ic_media_pause);
                        audioHandler.post(updateSeekbarTask);
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnAudioPlay.setImageResource(android.R.drawable.ic_media_play);
                        audioSeekbar.setProgress(0);
                        txtAudioCurrent.setText("0:00");
                        audioHandler.removeCallbacks(updateSeekbarTask);
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        if (getActivity() == null) return true;
                        btnAudioPlay.setVisibility(View.VISIBLE);
                        audioLoading.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Erreur lors du chargement de l'audio.", Toast.LENGTH_SHORT).show();
                        resetPlayer();
                        return true;
                    }
                });
            } catch (IOException e) {
                LogUtil.e(e);
                resetPlayer();
                btnAudioPlay.setVisibility(View.VISIBLE);
                audioLoading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erreur de lecture : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnAudioPlay.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                btnAudioPlay.setImageResource(android.R.drawable.ic_media_pause);
            }
        }
    }

    
    private void parseAndRenderLyrics() {
        try {
            if (lyricsContainer != null) lyricsContainer.removeAllViews();
            if (currentPage == null) return;
            
            String content = currentPage.getContent();
            if (content == null) return;
            
            boolean isNight = Prefs.getBoolean("night_mode", true);
            int colorPrimaryText = isNight ? Color.WHITE : Color.parseColor("#111111");
            int colorRedAccent = Color.parseColor("#C62828");
            int colorGoldText = Color.parseColor("#F5E0B7");
            int colorLightSubtext = Color.parseColor("#9E9E9E");
            
            int verseNumberColor = isNight ? colorLightSubtext : colorRedAccent;
            int refrainBgColor = isNight ? Color.parseColor("#1A1A1A") : Color.parseColor("#F5F5F5");
            int refrainTextColor = isNight ? colorGoldText : colorRedAccent;

            String[] stanzas = content.split("\n\\s*\n");
            LayoutInflater inflater = LayoutInflater.from(getContext());

            String musicalTermsRegex = "(?i)(\\b(?:bis|ter|quater|sol|solo|tous)\\b|\\(\\s*(?:bis|ter|quater|sol|solo|tous)\\s*\\))";
            String hexHighlightColor = String.format("#%06X", (0xFFFFFF & (isNight ? colorLightSubtext : colorRedAccent)));

        for (String stanza : stanzas) {
            stanza = stanza.trim();
            if (stanza.isEmpty()) continue;

            String upperStanza = stanza.toUpperCase().replaceAll("Œ", "OE");
            boolean isRefrain = upperStanza.startsWith("REFRAIN") || upperStanza.startsWith("CHOEUR") || upperStanza.startsWith("CHOUR") || upperStanza.startsWith("REF");
            
            if (isRefrain) {
                String text = stanza.replaceAll("(?i)^(REFRAIN|CHOEUR|CHOUR|CHŒUR|REF\\.?)\\s*:?\\s*", "").trim();
                
                String htmlText = text.replaceAll(musicalTermsRegex, "<i><font color=\"" + hexHighlightColor + "\">$1</font></i>").replace("\n", "<br>");
                
                View refrainView = inflater.inflate(R.layout.item_lyric_refrain, lyricsContainer, false);
                ((androidx.cardview.widget.CardView) refrainView).setCardBackgroundColor(refrainBgColor);
                
                TextView txtRefrain = refrainView.findViewById(R.id.txt_refrain_content);
                txtRefrain.setText(android.text.Html.fromHtml(htmlText));
                txtRefrain.setTextSize(currentTextSize);
                txtRefrain.setTextColor(refrainTextColor);
                
                TextView txtLabel = refrainView.findViewById(R.id.txt_refrain_label);
                if (txtLabel != null) {
                    txtLabel.setTextColor(isNight ? colorLightSubtext : colorRedAccent);
                }

                lyricsContainer.addView(refrainView);
            } else {
                View verseView = inflater.inflate(R.layout.item_lyric_verse, lyricsContainer, false);
                TextView txtContent = verseView.findViewById(R.id.txt_verse_content);
                TextView txtNumber = verseView.findViewById(R.id.txt_verse_number);

                Matcher m = Pattern.compile("^(\\d+\\.)\\s*(.*)", Pattern.DOTALL).matcher(stanza);
                String textToRender;
                if (m.matches()) {
                    txtNumber.setText(m.group(1));
                    txtNumber.setVisibility(View.VISIBLE);
                    txtNumber.setTextColor(verseNumberColor);
                    textToRender = m.group(2).trim();
                } else {
                    txtNumber.setVisibility(View.GONE);
                    textToRender = stanza;
                }
                
                String htmlText = textToRender.replaceAll(musicalTermsRegex, "<i><font color=\"" + hexHighlightColor + "\">$1</font></i>").replace("\n", "<br>");
                txtContent.setText(android.text.Html.fromHtml(htmlText));
                
                txtContent.setTextSize(currentTextSize);
                txtContent.setTextColor(colorPrimaryText);
                lyricsContainer.addView(verseView);
            }
        }
        } catch (Exception ex) {
            LogUtil.e(ex);
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
    public void onDestroyView() {
        super.onDestroyView();
        resetPlayer();
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
        try {
            boolean current = Prefs.getBoolean("night_mode", true);
            Prefs.putBoolean("night_mode", !current);
        } catch (Exception e) {}
    }

    private void initTheme() {
        try {
            boolean isNight = Prefs.getBoolean("night_mode", true);
            
            // Respect the user's new background overlay layout
            View rootOverlay = getView() != null ? getView().findViewById(R.id.lyrics_root_layout) : null;
            if (rootOverlay != null) {
                // If it's night, use deep dark semi-transparent overlay to obscure the image. If day, use white semi-transparent.
                rootOverlay.setBackgroundColor(isNight ? Color.parseColor("#CD000000") : Color.parseColor("#DDFDFDFD"));
            }
            
            if (txtTitle != null) {
                txtTitle.setTextColor(isNight ? Color.WHITE : Color.parseColor("#111111"));
            }
            if (txtWatermark != null) {
                txtWatermark.setTextColor(isNight ? Color.parseColor("#33C62828") : Color.parseColor("#11C62828"));
            }
            if (txtCategory != null) {
                txtCategory.setTextColor(isNight ? Color.parseColor("#9E9E9E") : Color.parseColor("#757575"));
            }
            if (txtAuthor != null) {
                txtAuthor.setTextColor(isNight ? Color.parseColor("#9E9E9E") : Color.parseColor("#757575"));
            }
            
            View pillGroup = getView() != null ? getView().findViewById(R.id.control_pill) : null;
            if (pillGroup != null) {
                pillGroup.setBackgroundResource(R.drawable.pill_background);
            }
        } catch (Exception e) {}
    }

    private void resizeTextDialog() {
       // Replaced by inline control pill buttons
    }

}
