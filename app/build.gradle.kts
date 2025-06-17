plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.davidkazad.chantlouange"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.davidkazad.chantlouange"
        minSdk = 23
        targetSdk = 35
        versionCode = 14
        versionName = "25.6.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("myreleasekey.keystore")
            storePassword = "com.davidkz.chant.louange153"
            keyAlias = "davidkazad"
            keyPassword = "com.davidkz.chant.louange153"
            enableV2Signing = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.vectordrawable)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(files("libs/eventbus-2.4.0.jar"))
    implementation(files("libs/easyprefs-1.9.0.aar"))
    implementation(libs.appintro)
    implementation(libs.butterknife)
    
    annotationProcessor(libs.butterknife.compiler)
    implementation(libs.cardview)
    implementation(libs.recyclerview)
    implementation (libs.materialdrawer)

    implementation(libs.core)
    implementation(libs.universal.image.loader)
    
    implementation (libs.circleimageview)
    implementation (libs.fab)
    implementation(libs.gson)

    implementation(libs.dexter)
    //implementation("com.pixplicity.easyprefs:EasyPrefs:1.9.0")

    implementation(libs.material.tap.target.prompt)
    implementation(libs.work.runtime)

    implementation(libs.androidslidinguppanel)

    dependencies {
        implementation(libs.glide)
        annotationProcessor(libs.compiler)
    }


    /*
    annotationProcessor("com.jakewharton:butterknife-compiler:10.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.mikepenz:materialdrawer:6.1.2")

    implementation("com.afollestad.material-dialogs:core:0.9.6.0")
    implementation("com.pixplicity.easyprefs:library:1.9.0")
    implementation("com.nostra13.universalimageloader:universal-image-loader:1.9.5")

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.clans:fab:1.6.4")
    implementation("com.google.code.gson:gson:2.8.6")

    implementation("com.karumi:dexter:6.1.2")
    implementation("uk.co.samuelwall:material-tap-target-prompt:3.0.0")
    implementation("androidx.work:work-runtime:2.7.1")

    implementation("com.github.kabouzeid:androidslidinguppanel:6")
// ViewModel and LiveData
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    //if using java 8,ignore above line and add the following line
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.4.1")

    dependencies {
        implementation("com.github.bumptech.glide:glide:4.12.0")
        annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    }
     */
}