package com.davidkazad.chantlouange.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.datas.PsalmVerses;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.ui.activities.ListActivity;

public class BookFragment extends Fragment {

    private RecyclerView rvBooks;

    private TextView tvVerseText;
    private TextView tvVerseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvBooks = view.findViewById(R.id.rv_books);

        // ── Verset biblique aléatoire des Psaumes ──────────────────────
        tvVerseText = view.findViewById(R.id.tv_verse_text);
        tvVerseReference = view.findViewById(R.id.tv_verse_reference);
        PsalmVerses.init(requireContext());
        displayRandomVerse();

        // ── Configuration du RecyclerView avec 2 colonnes ───────────────
        rvBooks.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvBooks.setAdapter(new BookAdapter());
    }

    /**
     * Adapter moderne pour la liste des recueils
     */
    private class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

        @NonNull
        @Override
        public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
            return new BookViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
            Book currentBook = Book.bookList.get(position);
            
            holder.bookName.setText(currentBook.getName());
            
            int bookImageRes = currentBook.getImage();
            if (bookImageRes != 0) {
                holder.bookImage.setImageResource(bookImageRes);
            }

            int songCount = currentBook.getPages() != null ? currentBook.getPages().size() : 0;
            if (songCount > 0) {
                holder.bookCount.setVisibility(View.VISIBLE);
                holder.bookCount.setText(String.format(getString(R.string.song_count_label), songCount));
            } else {
                holder.bookCount.setVisibility(View.GONE);
            }

            // Animation d'entrée "WOW" (Fondue + Montée + Échelle avec Rebond)
            holder.itemView.setAlpha(0f);
            holder.itemView.setTranslationY(40f);
            holder.itemView.setScaleX(0.95f);
            holder.itemView.setScaleY(0.95f);
            
            holder.itemView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                    .setStartDelay(position * 60L)
                    .start();

            holder.itemView.setOnClickListener(v -> {
                ListActivity.bookItem = currentBook;
                if (currentBook.isBookComingSoon()) {
                    Toast.makeText(getContext(), R.string.book_coming_soon, Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getContext(), ListActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return Book.bookList.size();
        }
    }

    private static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookName;
        TextView bookCount;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.book_image);
            bookName = itemView.findViewById(R.id.book_name);
            bookCount = itemView.findViewById(R.id.book_count);
        }
    }

    /**
     * Affiche un verset aléatoire des Psaumes avec une animation de fondu.
     */
    private void displayRandomVerse() {
        if (tvVerseText == null || tvVerseReference == null) return;

        PsalmVerses.Verse verse = PsalmVerses.getRandomVerse();

        tvVerseText.setAlpha(0f);
        tvVerseReference.setAlpha(0f);

        tvVerseText.setText(verse.text);
        tvVerseReference.setText("\u2014 " + verse.reference);

        ObjectAnimator fadeText = ObjectAnimator.ofFloat(tvVerseText, "alpha", 0f, 1f);
        fadeText.setDuration(800);
        fadeText.setStartDelay(200);
        fadeText.start();

        ObjectAnimator fadeRef = ObjectAnimator.ofFloat(tvVerseReference, "alpha", 0f, 0.75f);
        fadeRef.setDuration(800);
        fadeRef.setStartDelay(500);
        fadeRef.start();
    }
}
