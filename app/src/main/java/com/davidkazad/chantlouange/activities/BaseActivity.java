package com.davidkazad.chantlouange.activities;

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
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Favoris;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.songs.CV;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    public static final String EXTRA_COMMUNITY = "community";
    private FragmentManager fm;
    private boolean isListVisible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (Prefs.getBoolean("Theme",false)){
            setTheme(R.style.MyAppTheme2);
        }else {

            setTheme(R.style.MyAppTheme);
        }*/

        fm = getSupportFragmentManager();
    }

    protected void navigationDrawer(Bundle savedInstanceState, Toolbar toolbar) {
        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        final IProfile userProfil = new ProfileDrawerItem()
                .withName("Recueil des cantiques")
                .withEmail("tclcantiques@gmail.com")
                .withIcon(R.drawable.holy_bible_96px).withIdentifier(1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        contact();
                        return false;
                    }
                });

        // Create the AccountHeader
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
                        //new PrimaryDrawerItem().withName("Programme de culte").withIcon(R.drawable.details_48px).withIdentifier(5),
                        new PrimaryDrawerItem().withName(R.string.home).withSelectable(false).withIcon(R.drawable.idea_24px).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.preach).withIcon(R.drawable.bible_48px).withIdentifier(1),
                        //new PrimaryDrawerItem().withName("Programme de culte").withIcon(R.drawable.details_48px).withIdentifier(5),
                        new PrimaryDrawerItem().withName(R.string.community).withIcon(R.drawable.coderwall_48px).withIdentifier(6),
                        new PrimaryDrawerItem().withName(R.string.favorities).withIcon(R.drawable.star0).withIdentifier(7),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.settings).withIcon(R.drawable.settings_48px).withIdentifier(9),
                        new SecondaryDrawerItem().withName(R.string.send_comment).withIcon(R.drawable.comments_24px).withIdentifier(10),
                        new SecondaryDrawerItem().withName("Contacts").withIcon(R.drawable.info_48px).withIdentifier(11)
                )

                .withSavedInstance(savedInstanceState)
                .build();

        result.addStickyFooterItem(new PrimaryDrawerItem().withName(R.string.support_dev).withIcon(R.drawable.pay)

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(getApplicationContext(), DonationActivity.class));
                        return false;

                    }
                }));

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);*/
        }

        if (Build.VERSION.SDK_INT >= 19) {
            //result.getDrawerLayout().setFitsSystemWindows(false);
        }

        if (savedInstanceState == null) {
            /*Fragment f = new AccueilFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commit();*/
        }

        result.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                //Toast.makeText(BaseActivity.this, "item: "+position, Toast.LENGTH_SHORT).show();
                switch (position) {

                    case 1:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        return false;

                    case 2:
                        openUrl("https://www.youtube.com/channel/UCr9ARDYEe-4pT4RHFtuWiRw");
                        return false;
                    case 3:
                        startActivity(new Intent(getApplicationContext(),WebviewActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(), FavorisActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;

                    case 7:
                        openPlayStore();
                        break;
                    case 8:
                        contact();
                        break;

                    default:
                        return false;
                }

                return false;
            }
        });
    }

    protected void joinGroup() {
        String link = "https://chat.whatsapp.com/GreL17Wbxz680C29RFSol8";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);
    }

    protected void contactWhatsapp(){
        String link = "https://wa.me/+243895026521?text=[Recueil%20des%20cantiques]\n";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);
    }

    protected void addToFavoris(Page mPage) {
        Favoris favoris = new Favoris(mPage);
        favoris.add();
    }

    protected void addToLike(int bookId, int songId) {
        Prefs.putBoolean(String.format(getString(R.string.like_preferences), bookId, songId), true);
        Toast.makeText(this, "successfully added!", Toast.LENGTH_SHORT).show();
    }


    protected void openHelp() {
        openBrowser("help");
    }


    protected void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    protected void openBrowser(String bookid) {
        String url = "https://14ka135.wixsite.com/website/?" + bookid;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    protected void openLicence() {
        String url = "https://alwaysdata.tclcantiques.net/?licence";
        //String url = "https://14ka135.wixsite.com/website/?" + bookid;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    protected void openPlayStore() {
        String url = "https://play.google.com/store/apps/details?id=com.davidkazad.tclcantiques";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    protected void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.davidkazad.tclcantiques");
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
        String url = "https://github.com/david14ka/tclcantiques";
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
        smsIntent.putExtra("address", new String("+243895026521"));
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

        phoneNo = "+243970015092";
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
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
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

    }

    protected void contact(){
        new MaterialDialog.Builder(this)
                .title("Contacts")
                .items(R.array.contact)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch (position){
                            case 0 : contactWhatsapp();break;
                            case 1 : sendEmail();break;
                            case 3 : joinGroup();break;
                            case 4 : openFacebook();break;
                            case 5 : openGithub();break;
                        }
                    }
                })
                .show();
    }
    protected void findItem() {

        new MaterialDialog.Builder(this)
                .title("Rechercher dans")
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

                                        if (bookId == 1) {
                                            Book book = new CV();
                                            final List<Page> pageList = book.find("" + page);
                                            final List<String> numberList = new ArrayList<>();

                                            if (pageList.size() > 1) {

                                                for (Page page1 :
                                                        pageList) {
                                                    numberList.add(page1.getNumber());
                                                }
                                                new MaterialDialog.Builder(BaseActivity.this)
                                                        .title(text)
                                                        .items(numberList)
                                                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                                            @Override
                                                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                                                Page currentPage = pageList.get(which);

                                                                if (currentPage != null) {

                                                                    initPageContent(currentBook, currentPage);

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

                                                    initPageContent(currentBook, currentPage);

                                                } else {

                                                    Toast.makeText(getApplicationContext(), R.string.number_ot_exists, Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        } else {
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

    private void initPageContent(Book currentBook, Page currentPage) {
        ItemActivity.currentBook = currentBook;
        ItemActivity.currentPage = currentPage;
        startActivity(new Intent(getApplicationContext(), ItemActivity.class));
    }
}
