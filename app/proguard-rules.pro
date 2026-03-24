# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Keep default constructors for all model and data classes
-keepclassmembers class * {
    public <init>();
}

# Keep all model classes (example)
-keep class com.davidkazad.chantlouange.models.** { *; }

# Keep greenrobot EventBus subscriber info
-keep class org.greenrobot.eventbus.** { *; }
-keepclassmembers class ** {
    public void onEvent*(**);
}