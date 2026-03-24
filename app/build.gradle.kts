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
        versionCode = 28
        versionName = "6.3.2"

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
    implementation(libs.eventbus)
    implementation(files("libs/easyprefs-1.9.0.aar"))
    implementation(libs.appintro)
    implementation(libs.butterknife)
    annotationProcessor(libs.butterknife.compiler)
    implementation(libs.cardview)
    implementation(libs.recyclerview)
    implementation (libs.materialdrawer)
    implementation(libs.core)
    implementation (libs.circleimageview)
    implementation (libs.fab)
    implementation(libs.gson)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.material.tap.target.prompt)
    implementation(libs.work.runtime)
    implementation(libs.androidslidinguppanel)
}