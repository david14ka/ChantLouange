package com.davidkazad.chantlouange.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.config.Common;
import com.davidkazad.chantlouange.datas.CC;
import com.davidkazad.chantlouange.models.AppNotification;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.datas.CV;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    public static final String EXTRA_COMMUNITY = "community";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNavigation(int selectedItemId) {
        com.google.android.material.bottomnavigation.BottomNavigationView navigation = findViewById(R.id.navigation);
        if (navigation == null) return;

        if (selectedItemId != 0) {
            navigation.setSelectedItemId(selectedItemId);
        }

        navigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == selectedItemId) return false;

            if (this instanceof HomeActivity) {
                return false; // Géré nativement par HomeActivity
            }

            Intent intent;
            if (item.getItemId() == R.id.navigation_settings) {
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            } else {
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("targetFragment", item.getItemId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
        });
    }

    protected void navigationDrawer(Bundle savedInstanceState, Toolbar toolbar) {
        final IProfile userProfil = new ProfileDrawerItem()
                .withName(R.string.app_name)
                .withEmail("14ka135@gmail.com")
                .withIcon(R.drawable.holy_bible_96px).withIdentifier(1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        contact();
                        return false;
                    }
                });

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withTextColor(Color.WHITE)
                .withActivity(this)
                .withHeaderBackground(R.drawable.guitar_3283649_640)
                .withTranslucentStatusBar(true)
                .addProfiles(
                        userProfil,
                        new ProfileSettingDrawerItem()
                                .withName(R.string.problems)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        contactWhatsapp();
                                        return false;
                                    }
                                })
                )
                .withSavedInstance(savedInstanceState)
                .build();

        Drawer result = new DrawerBuilder()
                .withSelectedItemByPosition(1)
                .withSavedInstance(savedInstanceState)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(false)
                .withShowDrawerOnFirstLaunch(true)
                .withActivity(this)
                .withHasStableIds(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.home).withSelectable(false).withIcon(R.drawable.bible_48px).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.community).withIcon(R.drawable.coderwall_48px).withIdentifier(2)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                if (!(BaseActivity.this instanceof HomeActivity)) {
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                }
                                break;
                            case 2:
                                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                                break;
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }

    protected void getNotification(View view) {

        Common.NOTIFICATION.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot s :
                            dataSnapshot.getChildren()) {
                        AppNotification notification = s.getValue(AppNotification.class);

                        if (notification != null) {

                            if (!Prefs.getBoolean(s.getKey(), false)) {

                                Snackbar snackbar = Snackbar.make(view, notification.getText(), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("GO TO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Prefs.putBoolean(s.getKey(), true);
                                        WebviewActivity.dataUrl = notification.getUrl();
                                        WebviewActivity.dataTitle = notification.getTitle();
                                        startActivity(new Intent(getApplicationContext(), WebviewActivity.class));
                                    }
                                });

                                snackbar.show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void joinGroup() {
        String link = "https://chat.whatsapp.com/JlNVKKLq1sa9U3Lx3dnHLH";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);
    }

    protected void contactWhatsapp() {
        String link = "https://wa.me/+243821741481?text=[Recueil%20des%20cantiques]\n";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);
    }

    protected void openHelp() {
        String url = "https://davidkazad.com/?chantlouange&action=help";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    protected void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    protected void openBrowser(String bookid) {
        String url = "https://davidkazad.com/?chantlouange&bookid=" + bookid;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    protected void openLicence() {
        String url = "https://davidkazad.com/?chantlouange&action=licence";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    protected void openPlayStore() {
        String url = "https://play.google.com/store/apps/details?id=com.davidkazad.chantlouange";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    protected void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.davidkazad.chantlouange");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    protected void sendText(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)));
    }

    protected void openFacebook() {
        String url = "https://web.facebook.com/tclcantiques";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    protected void openGithub() {
        String url = "https://github.com/david14ka/chantlouange";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    protected void openYoutub() {
        String url = "https://www.youtube.com/watch?v=4zPa0t4pz7o&list=PLVIN7hVeWee3v5gLMSb_FcWScjG_uYqQs";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    protected void openGoogle() {
        String url = "https://plus.google.com/u/0/communities/103970766906777906788";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    String message;
    String phoneNo;

    protected void sendEmail() {
        Log.i("Send email", "");

        Intent emailIntent = new Intent(Intent.ACTION_VIEW);
        String mailto = "mailto:14ka135@gmail.com" +
                "?cc=" + "tclcantiques@gmail.com" +
                "&subject=" + Uri.encode("#Livre des cantiques") +
                "&body=" + Uri.encode("Que Dieu vous benisse,");

        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);

            Log.i(TAG, "Finished sending email...");

        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(BaseActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendSMS() {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", new String("+243821741481"));
        smsIntent.putExtra("sms_body", "Chant&Louange:  ");

        try {
            startActivity(smsIntent);
            //finish();
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BaseActivity.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendSMSMessage() {

        phoneNo = "+243821741481";
        message = "Chant&Louange: ";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                return;
            }
        }

    }

    protected void contact() {
        new MaterialDialog.Builder(this)
                .title("Contacts")
                .items(R.array.contact)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch (position) {
                            case 0:
                                contactWhatsapp();
                                break;
                            case 1:
                                sendEmail();
                                break;
                            case 3:
                                joinGroup();
                                break;
                            case 4:
                                openFacebook();
                                break;
                            case 5:
                                openGithub();
                                break;
                        }
                    }
                })
                .show();
    }

    protected void findItem() {

        new MaterialDialog.Builder(this)
                .title(R.string.rechercher_dans)
                .items(R.array.book_list2)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, final int bookId, final CharSequence text) {

                        new MaterialDialog.Builder(BaseActivity.this)
                                .title(text)
                                .input(R.string.number, R.string.action_search, false, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence page) {

                                        final Book currentBook = Book.bookList.get(bookId);
                                        Book bookWithPageMarkedAB = (bookId==1)? new CV(): (bookId == 0) ? new CC() : null;

                                        if (bookWithPageMarkedAB != null){
                                            findSongWithABMark(bookWithPageMarkedAB, page);
                                        }
                                        else {
                                            Page currentPage = currentBook.getPage(Integer.valueOf(String.valueOf(page)) - 1);

                                            if (currentPage != null) {

                                                initPageContent(currentBook, currentPage);

                                            } else {
                                                Toast.makeText(getApplicationContext(), R.string.number_ot_exists, Toast.LENGTH_SHORT).show();
                                            }

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

    private void findSongWithABMark(Book book, CharSequence page) {

            final List<Page> pageList = book.find("" + page);
            final List<String> numberList = new ArrayList<>();

            if (pageList.size() > 1) {

                for (Page page1 :
                        pageList) {
                    numberList.add(page1.getNumber());
                }
                new MaterialDialog.Builder(BaseActivity.this)
                        .title(book.getName())
                        .items(numberList)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                Page currentPage = pageList.get(which);

                                if (currentPage != null) {

                                    initPageContent(book, currentPage);

                                } else {

                                    Toast.makeText(getApplicationContext(), R.string.number_ot_exists, Toast.LENGTH_SHORT).show();
                                }

                                return false;
                            }
                        })
                        .show();

            } else if (pageList.size() == 1) {

                Page currentPage = pageList.get(0);
                if (currentPage != null) {

                    initPageContent(book, currentPage);

                } else {

                    Toast.makeText(getApplicationContext(), R.string.number_ot_exists, Toast.LENGTH_SHORT).show();
                }
            }
    }

    private void initPageContent(Book currentBook, Page currentPage) {
        ItemActivity.currentBook = currentBook;
        ItemActivity.currentPage = currentPage;
        startActivity(new Intent(getApplicationContext(), ItemActivity.class));
    }


}
