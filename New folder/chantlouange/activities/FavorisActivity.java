package com.davidkazad.chantlouange.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.events.ClickItemTouchListener;
import com.davidkazad.chantlouange.models.Favoris;
import com.davidkazad.chantlouange.songs.SongsBook;
import com.davidkazad.chantlouange.songs.SongsItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavorisActivity extends BaseActivity {

    private static final String TAG = "FAV";
    @BindView(R.id.recycler_fav)
    RecyclerView recyclerView;
    private List<Favoris> favorises;
    private FavAdapter adapter;

    @BindView(R.id.annuler)
    Button buttonAnnuler;
    @BindView(R.id.envoyer) Button buttonEnvoyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        recyclerView = findViewById(R.id.recycler_fav);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        //favorises = Favoris.getList();
        adapter = new FavAdapter();
        recyclerView.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                }
            });
        }

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: "+newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: dx "+dx+"   dy: "+ dy);
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurePreferences();
            }
        });

        buttonEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });


        navigationDrawer(savedInstanceState,null);
    }

    private class FavAdapter extends RecyclerView.Adapter<FavHolder> {

        private List<Favoris> favorisList;

        public FavAdapter() {
            this.favorisList = Favoris.getList();
            Log.d(TAG, "FavAdapter: " + favorisList.size());
        }

        @NonNull
        @Override
        public FavHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(FavorisActivity.this);
            View view = inflater.inflate(R.layout.item_fav, parent, false);
            Log.d("TAG", "onCreateViewHolder: ");
            return new FavHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FavHolder holder, final int position) {
            final Favoris favoris = favorisList.get(position);
            final SongsItem item = SongsBook.getSong(favoris.getBookId(), favoris.getSongId());
            String bookName = SongsBook.getSongBook(favoris.getBookId() - 1).getSongsBookName();

            Log.d("TAG", "onBindViewHolder: " + bookName);
            //if (item != null) {
            holder.textViewSongsNumber.setText(item.getNumber());
            holder.textViewSongsTitle.setText(item.getTitle());
            holder.textViewSongsBookName.setText(String.format("%s %s", item.getNumber(), bookName));
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //handle menu1 click
                    SongDetailsActivity.mItem = item;
                    SongDetailsActivity.bookId = favoris.getBookId();

                    startActivity(new Intent(FavorisActivity.this, SongDetailsActivity.class));

                }
            });
            holder.iMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(FavorisActivity.this, v);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.fav_options_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.menu1:
                                    //handle menu1 click
                                    SongDetailsActivity.mItem = item;
                                    SongDetailsActivity.bookId = favoris.getBookId();

                                    startActivity(new Intent(FavorisActivity.this, SongDetailsActivity.class));

                                    break;
                                case R.id.menu2:
                                    favoris.removeFromFavoris();
                                    adapter=new FavAdapter();
                                    adapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(adapter);
                                    break;
                                case R.id.menu3:
                                    SongDetailsActivity.mItem = item;
                                    SongDetailsActivity.bookId = favoris.getBookId();
                                    startActivity(new Intent(getApplicationContext(),SongDetailsActivity.class).putExtra("edit",true));
                                    break;
                            }
                            return false;
                        }
                    });

                    popup.show();
                }
            });
            //}
        }


        @Override
        public int getItemCount() {
            return favorisList.size();
        }
    }

    private PopupMenu popUpMenu(View view) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(FavorisActivity.this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.fav_options_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu1:
                        //handle menu1 click
                        break;
                    case R.id.menu2:
                        //handle menu2 click
                        break;
                    case R.id.menu3:
                        //handle menu3 click
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        return popup;
    }

    private class FavHolder extends RecyclerView.ViewHolder {

        public TextView textViewSongsNumber;
        public TextView textViewSongsTitle;
        public TextView textViewSongsBookName;
        public ImageView iMenu;
        public LinearLayout layout;

        public FavHolder(View itemView) {
            super(itemView);

            textViewSongsBookName = itemView.findViewById(R.id.song_book);
            textViewSongsNumber = itemView.findViewById(R.id.song_id);
            textViewSongsTitle = itemView.findViewById(R.id.song_title);
            iMenu = itemView.findViewById(R.id.item_menu);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_help) {

            openHelp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
