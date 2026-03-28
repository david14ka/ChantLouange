package com.davidkazad.chantlouange.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.ui.activities.ItemActivity;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.config.utils.LogUtil;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.davidkazad.chantlouange.config.Common.PREFS_TABLE_MATIERES_ALPHABETIQUE;

public class ListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = ListFragment.class.getName();
    public static final String EXTRA_BOOK_ID = "bookId";

    /** Kept static for backward-compat: ListActivity writes here before EventBus fires. */
    public static String query;

    @BindView(R.id.grid_song)
    GridView grid_song;

    private TextView resultCountView;
    private LinearLayout emptyView;

    private List<Page> pageList;
    private Book bookItem;
    private GridAdapter adapter;

    /** Per-instance copy so tabs don't share a stale query between each other. */
    private String currentQuery = "";

    public ListFragment() {}

    public static ListFragment getInstance(int bookId) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(view);

        LogUtil.d();

        grid_song     = view.findViewById(R.id.grid_song);
        resultCountView = view.findViewById(R.id.result_count);
        emptyView     = view.findViewById(R.id.empty_view);

        grid_song.setVisibility(View.VISIBLE);
        grid_song.setOnItemClickListener(this);

        if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {
            grid_song.setNumColumns(1);
        }

        if (getArguments() != null) {
            int bookId = getArguments().getInt(EXTRA_BOOK_ID);
            bookItem = Book.bookList.get(bookId);

            // Load the default page list
            pageList = Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)
                    ? bookItem.sort()
                    : bookItem.getPages();

            adapter = new GridAdapter();

            // Apply any pre-existing query
            if (query != null && !query.isEmpty()) {
                applySearch(query);
            }

            grid_song.setAdapter(adapter);
        }
    }

    // -------------------------------------------------------------------------
    // Search handling
    // -------------------------------------------------------------------------

    private void applySearch(String q) {
        currentQuery = (q == null) ? "" : q;
        if (bookItem == null) return;

        if (currentQuery.isEmpty()) {
            pageList = Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)
                    ? bookItem.sort()
                    : bookItem.getPages();
        } else {
            pageList = bookItem.searchPage(currentQuery);
            if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {
                pageList = bookItem.sort(pageList);
            }
        }

        boolean hasQuery   = !currentQuery.isEmpty();
        boolean hasResults = !pageList.isEmpty();

        // Result count bar
        if (resultCountView != null) {
            if (hasQuery) {
                resultCountView.setVisibility(View.VISIBLE);
                resultCountView.setText(
                        getString(R.string.result_count, pageList.size()));
            } else {
                resultCountView.setVisibility(View.GONE);
            }
        }

        // Empty state / grid visibility
        if (emptyView != null) {
            emptyView.setVisibility(hasQuery && !hasResults ? View.VISIBLE : View.GONE);
        }
        if (grid_song != null) {
            grid_song.setVisibility(!hasQuery || hasResults ? View.VISIBLE : View.GONE);
        }

        if (adapter != null) adapter.notifyDataSetChanged();
    }

    // -------------------------------------------------------------------------
    // EventBus
    // -------------------------------------------------------------------------

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String q) {
        applySearch(q);
    }

    // -------------------------------------------------------------------------
    // Item click
    // -------------------------------------------------------------------------

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Page item = pageList.get(position);
        if (item.getId() < 0) return;

        ItemActivity.currentPage = item;
        ItemActivity.currentBook = bookItem;
        startActivity(new Intent(getContext(), ItemActivity.class));
    }

    // -------------------------------------------------------------------------
    // Adapter
    // -------------------------------------------------------------------------

    private class HolderItem {
        public TextView  number;
        public TextView  title;
        public ImageView fav;
        public ImageView note;
        public TextView  letter;
    }

    private class GridAdapter extends BaseAdapter {

        @Override public int getCount()              { return pageList.size(); }
        @Override public Object getItem(int pos)     { return pageList.get(pos); }
        @Override public long   getItemId(int pos)   { return 0; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            HolderItem holder;

            Page mPage = pageList.get(position);

            if (convertView == null) {
                if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {
                    convertView = inflater.inflate(R.layout.item_alphabetique, parent, false);
                } else {
                    convertView = inflater.inflate(R.layout.item_numerique, parent, false);
                }

                holder        = new HolderItem();
                holder.number = convertView.findViewById(R.id.txt_number);
                holder.title  = convertView.findViewById(R.id.txt_tittle);
                holder.fav    = convertView.findViewById(R.id.fav);
                holder.note   = convertView.findViewById(R.id.note);
                try {
                    holder.letter = convertView.findViewById(R.id.txt_letter);
                } catch (Exception ignored) { /* alphabetic mode only */ }

                convertView.setTag(holder);
            } else {
                holder = (HolderItem) convertView.getTag();
            }

            if (mPage.getId() < 0) {
                // Alphabetic section header
                holder.letter.setText(mPage.getTitle());
                holder.letter.setVisibility(View.VISIBLE);
                holder.number.setVisibility(View.INVISIBLE);
                holder.note.setVisibility(View.GONE);
                holder.title.setVisibility(View.GONE);
            } else {
                holder.letter.setVisibility(View.GONE);
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setGravity(Gravity.LEFT);
                holder.number.setVisibility(View.VISIBLE);
                holder.note.setVisibility(View.VISIBLE);

                // Highlight matched portion of the title
                highlightText(holder.title, mPage.getTitle(), currentQuery);
            }

            holder.fav.setVisibility(mPage.isFavorite() ? View.VISIBLE : View.GONE);

            if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {
                holder.number.setText(String.format(
                        getString(R.string.txt_number),
                        mPage.getNumber().replace(". ", "")));
            } else {
                holder.note.setVisibility(View.GONE);
                holder.number.setText(mPage.getNumber().replace(". ", ""));
            }

            return convertView;
        }

        /** Applies a gold background span around the portion of {@code text} that
         *  matches {@code query}. Falls back to plain text if no match or empty query. */
        private void highlightText(TextView tv, String text, String query) {
            if (query == null || query.isEmpty()) {
                tv.setText(text);
                return;
            }
            int start = text.toLowerCase().indexOf(query.toLowerCase());
            if (start >= 0) {
                SpannableString spannable = new SpannableString(text);
                spannable.setSpan(
                        new BackgroundColorSpan(Color.parseColor("#55FFD700")),
                        start,
                        start + query.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(spannable);
            } else {
                tv.setText(text);
            }
        }
    }
}
