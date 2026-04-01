package com.davidkazad.chantlouange.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.ui.activities.ItemActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllSongsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllSongsAdapter mAdapter;
    private TextView txtSongCount;
    
    private TextView btnAz, btnNumero, btnRecueil, btnFavoris;
    private EditText edtSearch;
    private CheckBox checkSearchLyrics;

    private List<ListItem> allSongsRaw = new ArrayList<>();
    private String currentSearchQuery = "";
    private boolean searchLyricsEnabled = false;
    
    private enum FilterType { AZ, NUMERO, RECUEIL, FAVORIS }
    private FilterType currentFilter = FilterType.AZ;

    public static class ListItem {
        public int type;
        public Book book;
        public Page page;
        public String bookAbbr;
        public String headerText;

        public ListItem(int type, String headerText) {
            this.type = type;
            this.headerText = headerText;
        }

        public ListItem(int type, Book book, Page page, String bookAbbr) {
            this.type = type;
            this.book = book;
            this.page = page;
            this.bookAbbr = bookAbbr;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);
        recyclerView = view.findViewById(R.id.rv_all_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        txtSongCount = view.findViewById(R.id.txt_song_count);
        
        btnAz = view.findViewById(R.id.btn_filter_az);
        btnNumero = view.findViewById(R.id.btn_filter_numero);
        btnRecueil = view.findViewById(R.id.btn_filter_recueil);
        btnFavoris = view.findViewById(R.id.btn_filter_favoris);
        edtSearch = view.findViewById(R.id.edt_search_songs);
        checkSearchLyrics = view.findViewById(R.id.check_search_lyrics);
        
        checkSearchLyrics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchLyricsEnabled = isChecked;
                applyFiltersAndSort();
            }
        });

        mAdapter = new AllSongsAdapter(new ArrayList<>());
        mAdapter.setOnItemClickListener(new AllSongsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListItem item) {
                if (item.type == AllSongsAdapter.TYPE_SONG) {
                    ItemActivity.currentBook = item.book;
                    ItemActivity.currentPage = item.page;
                    startActivity(new Intent(getActivity(), ItemActivity.class));
                }
            }
        });
        recyclerView.setAdapter(mAdapter);

        loadSongsCache();
        setupFilters();
        setupSearch();
        
        applyFiltersAndSort();

        return view;
    }

    private void loadSongsCache() {
        List<Book> books = Book.getAll();
        Map<Integer, String> bookNames = new HashMap<>();

        for (Book b : books) {
            bookNames.put(b.getId(), b.getAbbreviation() != null ? b.getAbbreviation() : b.getName());
            if(b.getPages() != null) {
                for (Page p : b.getPages()) {
                    if (p.getTitle() == null || p.getTitle().length() <= 1 || p.getId() < 0) continue;
                    allSongsRaw.add(new ListItem(AllSongsAdapter.TYPE_SONG, b, p, bookNames.get(b.getId())));
                }
            }
        }
    }
    
    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim().toLowerCase();
                applyFiltersAndSort();
            }
        });
    }

    private void setupFilters() {
        View.OnClickListener filterListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAz.setBackgroundResource(R.drawable.pill_inactive_bg);
                btnNumero.setBackgroundResource(R.drawable.pill_inactive_bg);
                btnRecueil.setBackgroundResource(R.drawable.pill_inactive_bg);
                btnFavoris.setBackgroundResource(R.drawable.pill_inactive_bg);
                
                v.setBackgroundResource(R.drawable.pill_active_bg);

                int id = v.getId();
                if (id == R.id.btn_filter_az) {
                    currentFilter = FilterType.AZ;
                } else if (id == R.id.btn_filter_numero) {
                    currentFilter = FilterType.NUMERO;
                } else if (id == R.id.btn_filter_recueil) {
                    currentFilter = FilterType.RECUEIL;
                } else if (id == R.id.btn_filter_favoris) {
                    currentFilter = FilterType.FAVORIS;
                }
                applyFiltersAndSort();
            }
        };

        btnAz.setOnClickListener(filterListener);
        btnNumero.setOnClickListener(filterListener);
        btnRecueil.setOnClickListener(filterListener);
        btnFavoris.setOnClickListener(filterListener);
    }

    private void applyFiltersAndSort() {
        List<ListItem> filtered = new ArrayList<>();
        
        // 1. Filter
        for (ListItem item : allSongsRaw) {
            boolean matchesSearch = true;
            if (!currentSearchQuery.isEmpty()) {
                String searchStr = (item.page.getNumber() + " " + item.page.getTitle() + " " + item.bookAbbr).toLowerCase();
                if (searchLyricsEnabled && item.page.getContent() != null) {
                    searchStr += " " + item.page.getContent().toLowerCase();
                }
                if (!searchStr.contains(currentSearchQuery)) {
                    matchesSearch = false;
                }
            }
            
            boolean matchesFavorite = true;
            if (currentFilter == FilterType.FAVORIS) {
                if (!item.page.isFavorite()) {
                    matchesFavorite = false;
                }
            }

            if (matchesSearch && matchesFavorite) {
                filtered.add(item);
            }
        }
        
        txtSongCount.setText(String.format("%,d HYMNES", filtered.size()));

        // 2. Sort
        if (currentFilter == FilterType.AZ || currentFilter == FilterType.FAVORIS) {
            Collections.sort(filtered, new Comparator<ListItem>() {
                @Override
                public int compare(ListItem o1, ListItem o2) {
                    String t1 = o1.page.getTitle() != null ? o1.page.getTitle().trim() : "";
                    String t2 = o2.page.getTitle() != null ? o2.page.getTitle().trim() : "";
                    return t1.compareToIgnoreCase(t2);
                }
            });
        } else if (currentFilter == FilterType.NUMERO) {
            Collections.sort(filtered, new Comparator<ListItem>() {
                @Override
                public int compare(ListItem o1, ListItem o2) {
                    int n1 = extractNumber(o1.page.getNumber());
                    int n2 = extractNumber(o2.page.getNumber());
                    if (n1 == n2) {
                        return (o1.page.getTitle() != null ? o1.page.getTitle() : "").compareToIgnoreCase(o2.page.getTitle() != null ? o2.page.getTitle() : "");
                    }
                    return Integer.compare(n1, n2);
                }
            });
        } else if (currentFilter == FilterType.RECUEIL) {
            Collections.sort(filtered, new Comparator<ListItem>() {
                @Override
                public int compare(ListItem o1, ListItem o2) {
                    // Sort by book ID or Name first
                    int bookCompare = Integer.compare(o1.book.getId(), o2.book.getId());
                    if (bookCompare == 0) {
                        // Then by number
                        int n1 = extractNumber(o1.page.getNumber());
                        int n2 = extractNumber(o2.page.getNumber());
                        return Integer.compare(n1, n2);
                    }
                    return bookCompare;
                }
            });
        }

        // 3. Build Final Items (with headers)
        List<ListItem> finalItems = new ArrayList<>();
        String currentSection = "";
        
        for (int i = 0; i < filtered.size(); i++) {
            if (currentFilter == FilterType.AZ) {
                if (finalItems.size() == 7 && currentSearchQuery.isEmpty()) {
                    finalItems.add(new ListItem(AllSongsAdapter.TYPE_FEATURED, ""));
                }
                ListItem item = filtered.get(i);
                String title = item.page.getTitle() != null ? item.page.getTitle().trim() : "";
                if (title.length() > 0) {
                    String firstChar = String.valueOf(title.charAt(0)).toUpperCase();
                    if (Character.isLetter(firstChar.charAt(0)) && !firstChar.equals(currentSection)) {
                        currentSection = firstChar;
                        finalItems.add(new ListItem(AllSongsAdapter.TYPE_HEADER, "LETTRE " + currentSection));
                    }
                }
                finalItems.add(item);
            } else if (currentFilter == FilterType.FAVORIS) {
                ListItem item = filtered.get(i);
                String title = item.page.getTitle() != null ? item.page.getTitle().trim() : "";
                if (title.length() > 0) {
                    String firstChar = String.valueOf(title.charAt(0)).toUpperCase();
                    if (Character.isLetter(firstChar.charAt(0)) && !firstChar.equals(currentSection)) {
                        currentSection = firstChar;
                        finalItems.add(new ListItem(AllSongsAdapter.TYPE_HEADER, "LETTRE " + currentSection));
                    }
                }
                finalItems.add(item);            
            } else if (currentFilter == FilterType.RECUEIL) {
                ListItem item = filtered.get(i);
                String bookName = item.book.getName() != null ? item.book.getName() : "INCONNU";
                if (!bookName.equals(currentSection)) {
                    currentSection = bookName;
                    finalItems.add(new ListItem(AllSongsAdapter.TYPE_HEADER, "RECUEIL: " + currentSection.toUpperCase()));
                }
                finalItems.add(item);
            } else { // NUMERO
                finalItems.add(filtered.get(i));
            }
        }

        mAdapter.updateData(finalItems);
    }
    
    private int extractNumber(String numberString) {
        if (numberString == null) return 0;
        String clean = numberString.replaceAll("[^0-9]", "");
        try {
            return clean.isEmpty() ? 0 : Integer.parseInt(clean);
        } catch (Exception e) {
            return 0;
        }
    }
}
