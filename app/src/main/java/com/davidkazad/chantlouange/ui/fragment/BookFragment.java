package com.davidkazad.chantlouange.ui.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.datas.OB;
import com.davidkazad.chantlouange.ui.activities.ListActivity;
import com.davidkazad.chantlouange.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class BookFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.grid_book)
    GridView grid_book;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        ButterKnife.bind(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        grid_book = view.findViewById(R.id.grid_book);

        grid_book.setOnItemClickListener(this);
        grid_book.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return Book.bookList.size();
            }

            @Override
            public Object getItem(int position) {
                return Book.bookList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(getContext());

                BookHolder holder = new BookHolder();

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_book, parent, false);
                    holder.bookName = convertView.findViewById(R.id.book_name);
                    holder.bookImage = convertView.findViewById(R.id.book_image);

                    convertView.setTag(holder);

                } else {

                    holder = (BookHolder) convertView.getTag();
                }


                String bookName = Book.bookList.get(position).getName();
                int bookImage = Book.bookList.get(position).getImage();

                Resources res = getResources();
                Log.d("TAG BookImage : ", bookName + " : "+ bookImage);
                Drawable drawableBookImage = ResourcesCompat.getDrawable(res, bookImage, null);

                holder.bookName.setText(bookName);
                holder.bookImage.setImageDrawable(drawableBookImage);



                return convertView;
            }
        });

    }

    private void promptCollection() {
        new MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.book_name)
                .setPrimaryText("Collection des cantiques")
                .setSecondaryText("Nouvelle version du livre avec plus de 600 chansons")
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            // User has pressed the prompt target
                        }
                    }
                })
                .show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Book clickedBook = Book.bookList.get(position);
        ListActivity.bookItem = clickedBook;
        if (clickedBook.isBookComingSoon()){
            Toast.makeText(getContext(), "This book is Coming soon!", Toast.LENGTH_SHORT).show();

        }else {
            startActivity(new Intent(getContext(), ListActivity.class));
        }
    }


    private class BookHolder {
        public ImageView bookImage;
        public TextView bookName;
    }

}
