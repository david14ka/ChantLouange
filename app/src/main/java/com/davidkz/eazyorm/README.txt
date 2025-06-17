<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="alive.smag">

    <application
        android:name="com.davidkz.eazyorm.config.MyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <meta-data
            android:name="AA_DB_NAME"
            android:value="smag.db" />
        <meta-data
            android:name="AA_DB_VERSION"
            android:value="4" />
        <meta-data
            android:name="AA_MODELS"
            android:value="alive.smag.persistance.HoraireDao, alive.smag.persistance.UtilisateurDao,
             alive.smag.persistance.CommuniquerDao, alive.smag.persistance.ResultatDao,
              alive.smag.model.Notification, alive.smag.model.BannerImage, com.davidkz.eazyorm.util.LogCat" />

        <activity
            android:name=".SplashActivity"
            android:label="@string/app_name"
            android:screenOrientation="portrait"
            android:theme="@style/AppTheme.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
            </application>

</manifest>