package com.davidkazad.chantlouange.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.common.Common;
import com.davidkazad.chantlouange.models.Favoris;
import com.davidkazad.chantlouange.models.User;
import com.davidkazad.chantlouange.models.UserPref;
import com.davidkazad.chantlouange.songs.SongsBook;
import com.davidkazad.chantlouange.songs.SongsItem;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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

import java.lang.reflect.Type;
import java.util.List;

import static com.davidkazad.chantlouange.common.Common.LogPrefs;
import static com.davidkazad.chantlouange.common.Common.LogUsers;
import static com.davidkazad.chantlouange.songs.SongsBook.cvException;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private FragmentManager fm;
    private boolean isListVisible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* if (Prefs.getBoolean("Theme",false)){
            setTheme(R.style.MyAppTheme_light);
        }else {
            setTheme(R.style.MyAppTheme);
        }*/
        fm = getSupportFragmentManager();
    }

    protected void navigationDrawer(Bundle savedInstanceState, Toolbar toolbar) {
        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        final IProfile userProfil = new ProfileDrawerItem()
                .withName("Developer")
                .withEmail("14ka135@gmail.com")
                .withIcon(R.drawable.holy_bible_96px).withIdentifier(1);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withTextColor(Color.WHITE)
                .withActivity(this)
                .withHeaderBackground(R.drawable.guitar_3283649_640)
                .withTranslucentStatusBar(true)
                .addProfiles(
                        userProfil,
                        new ProfileSettingDrawerItem().withName("Signaler un problème").withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                }, 350);
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

                        new PrimaryDrawerItem().withName("Accueil").withIcon(R.drawable.bible_48px).withIdentifier(1),
                        new PrimaryDrawerItem().withName("Découvrir").withSelectable(false).withIcon(R.drawable.idea_24px).withIdentifier(2),
                        //new PrimaryDrawerItem().withName("Predication en ligne").withIcon(R.drawable.bible_48px).withIdentifier(3),
                        //new PrimaryDrawerItem().withName("Programme de culte").withIcon(R.drawable.details_48px).withIdentifier(5),
                        new PrimaryDrawerItem().withName("Communautés").withIcon(R.drawable.coderwall_48px).withIdentifier(6),
                        new PrimaryDrawerItem().withName("Favoris").withIcon(R.drawable.star0).withIdentifier(7),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Paramètres").withIcon(R.drawable.settings_48px).withIdentifier(9),
                        new SecondaryDrawerItem().withName("Envoyer des commentaires").withIcon(R.drawable.comments_24px).withIdentifier(10),
                        new SecondaryDrawerItem().withName("Apropos").withIcon(R.drawable.info_48px).withIdentifier(11)
                )

                .withSavedInstance(savedInstanceState)
                .build();

        result.addStickyFooterItem(new PrimaryDrawerItem().withName("Support Development").withIcon(R.drawable.pay)

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

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

                        return false;
                    case 2:
                        displayCommunity();
                        return false;
                    case 3: displayCommunity(); break;
                    case 4: startActivity(new Intent(getApplicationContext(), FavorisActivity.class)); break;
                    case 6: startActivity(new Intent(getApplicationContext(), SettingsActivity.class)); break;

                    case 7: dialogContact(); break;
                    case 8: startActivity(new Intent(getApplicationContext(), AboutActivity.class));break;
                    case 9: sendEmail();

                    default:
                        return false;
                }

                return false;
            }
        });
    }

    protected void displayCommunity(){
        Common.LogClick.push().setValue(Common.currentUser.getDefaultId());
        Toast.makeText(BaseActivity.this, "Coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void dialogReference() {

        new MaterialDialog.Builder(BaseActivity.this)
                .title("** REFERENCES **")
                .items(R.array.reference)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                break;
                            case 1:
                                openBrowser("cc");
                                break;
                            case 2:
                                openBrowser("cv");
                                break;
                            case 3:
                                openBrowser("nm");
                                break;
                            case 4:
                                openBrowser("nw");
                                break;
                            case 5:
                                break;
                            case 6:
                                openPlayStore();
                                break;
                        }
                    }
                })
                .show();
    }

    protected void dialogContact() {

        new MaterialDialog.Builder(BaseActivity.this)
                .title(R.string.contacts)
                .items(R.array.contact)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                break;
                            case 1:
                                sendSMS();
                                break;
                            case 2:
                                sendEmail();
                                break;
                            case 3:
                                break;
                            case 4:
                                openGoogle();
                                break;
                            case 5:
                                openYoutub();
                                break;
                            case 6:
                                openFacebook();
                                break;
                            case 7:
                                openGithub();
                                break;
                        }
                    }
                })
                .show();

    }

    protected void addToFavoris(int bookId, int songId) {
        Favoris favoris = new Favoris(songId, bookId);
        favoris.addToFavoris();
    }

    protected void addToLike(int bookId, int songId) {
        Prefs.putBoolean(String.format(getString(R.string.like_preferences), bookId, songId),true);
        Toast.makeText(this, "successfully added!", Toast.LENGTH_SHORT).show();
    }


    protected void findSongDialog(){

        new MaterialDialog.Builder(BaseActivity.this)
                .title("Rechercher dans")
                .items(R.array.book_list)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, final int which, CharSequence text) {

                        new MaterialDialog.Builder(BaseActivity.this)
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
                                            startActivity(new Intent(BaseActivity.this,SongDetailsActivity.class));
                                        } else {
                                            Toast.makeText(BaseActivity.this, "Ce numero n'exist pas !", Toast.LENGTH_SHORT).show();
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
    protected void openHelp() {
        openBrowser("help");
    }
    protected void openBrowser(String bookid) {
        String url1 = "https://alwaysdata.tclcantiques.net/?"+bookid;
        String url = "https://14ka135.wixsite.com/website/?"+bookid;
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
        startActivity(Intent.createChooser(sendIntent,getString(R.string.share_with)));
    }

    protected void openFacebook() {
        String url = "https://github.com/david14ka/tclcantiques";
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
                "&subject=" + Uri.encode("#Chant & Louange") +
                "&body=" + Uri.encode("Email message goes here");

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
        smsIntent.putExtra("address", new String("+243970015092"));
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



    private FloatingActionMenu fabMenu;

    protected void onCreateFabMenu(){

    };

    protected void signUp(){
        new MaterialDialog.Builder(BaseActivity.this)
                .title("Creer une sauvegarde")
                .input(R.string.email_number, R.string.action_search, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, final CharSequence email) {
                        new MaterialDialog.Builder(BaseActivity.this)
                                .title("Sauvergarder")
                                .input(R.string.firstname, R.string.action_search, false, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence firstname) {
                                        User user = new User(String.valueOf(email), String.valueOf(firstname));
                                        UserPref pref= new UserPref();

                                        String eemail = firebaseEncapsulateFields(user.getEmail());

                                        LogUsers.child(eemail).setValue(user);
                                        LogPrefs.child(eemail).setValue(pref);
                                    }
                                })
                                .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                                .negativeText(R.string.cancel)
                                .positiveText(R.string.finiish)
                                .show();
                    }
                })
                .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                .negativeText(R.string.cancel)
                .positiveText(R.string.next)
                .show();

    }

    protected String firebaseEncapsulateFields(String email){
        email = email.replace(".","");
        email = email.replace("#","");
        email = email.replace("$","");
        email = email.replace("[","");
        email = email.replace("]","");

        return email;
    }

    protected void login(){
        new MaterialDialog.Builder(BaseActivity.this)
                .title("Se connecter")
                .input(R.string.email_number, R.string.action_search, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, final CharSequence email) {

                        LogUsers.child(firebaseEncapsulateFields(String.valueOf(email))).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    User user = dataSnapshot.getValue(User.class);

                                    Toast.makeText(BaseActivity.this, "Connexion success!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .negativeText(R.string.cancel)
                .positiveText(R.string.next)
                .show();

    }
    protected void restaurePreferences(){
        new MaterialDialog.Builder(BaseActivity.this)
                .title("Se connecter")
                .input(R.string.email_number, R.string.action_search, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, final CharSequence email) {

                        LogUsers.child(firebaseEncapsulateFields(String.valueOf(email))).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    User user = dataSnapshot.getValue(User.class);
                                    Toast.makeText(BaseActivity.this, "connexion success!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        LogPrefs.child(firebaseEncapsulateFields(String.valueOf(email))).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    UserPref pref = dataSnapshot.getValue(UserPref.class);
                                    Log.d(TAG, "onDataChange: "+dataSnapshot);

                                    Type listType = new TypeToken<List<Favoris>>() {}.getType();
                                    if (pref != null) {
                                        List<Favoris> favorisList = new GsonBuilder().create().fromJson(pref.getFavGson(), listType);
                                        for (Favoris f :
                                                favorisList) {
                                            f.save();
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .negativeText(R.string.cancel)
                .positiveText(R.string.next)
                .show();

    }
}
