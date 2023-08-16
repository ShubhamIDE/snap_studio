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
-keep class com.photo.editor.snapstudio.** { *; }

# AndroidX
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }
-keep class androidx.constraintlayout.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.activity.** { *; }
-keep class androidx.hilt.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Google Play Services
-keep class com.google.android.gms.** { *; }

# Shimmer Effect
-keep class com.facebook.shimmer.** { *; }

# ViewModel
-keep class androidx.lifecycle.** { *; }

# Hilt
-keep class dagger.hilt.android.** { *; }
-keepclassmembers class * {
    @dagger.hilt.InstallIn *;
}

# Dots Indicator
-keep class com.tbuonomo.andrui.** { *; }

# Rounded BottomSheet
-keep class com.github.Deishelon.** { *; }

# Glide
-keep class com.bumptech.glide.** { *; }

# Gson
-keep class com.google.gson.** { *; }

# Retrofit
-keep class com.squareup.retrofit2.** { *; }
-keep class com.squareup.okhttp3.** { *; }

# Dexter
-keep class com.karumi.dexter.** { *; }

# Jsoup
-keep class org.jsoup.** { *; }

# Lottie
-keep class com.airbnb.android.lottie.** { *; }

# WySaid
-keep class org.wysaid.** { *; }

# Keyboard Height
-keep class com.github.ConsultantPlus.Mobile.** { *; }

# RoundedImageView
-keep class com.github.siyamed.** { *; }

# Warkiz
-keep class com.github.warkiz.widget.** { *; }

# flipZeus
-keep class com.github.flipzeus.** { *; }

# SimpleCropView
-keep class com.isseiaoki.** { *; }

# RoundedFrameLayout
-keep class com.github.QuarkWorks.** { *; }

# CropIwa
-keep class com.steelkiwi.** { *; }

# UCrop
-keep class com.yalantis.** { *; }

# Palette
-keep class androidx.palette.** { *; }

# Picasso
-keep class com.squareup.picasso.** { *; }

# GPUImage
-keep class jp.co.cyberagent.android.gpuimage.** { *; }

# TensorFlow
-keep class org.tensorflow.** { *; }
#
## Keep all public and protected classes, methods, and fields
#-keepclassmembers public, protected class * {
#    public protected <fields>;
#    public protected <methods>;
#}
