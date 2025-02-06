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

# Keep Room database classes
-keep class com.example.englishquiz.data.** { *; }

# If using Kotlin serialization
-keep class kotlinx.serialization.** { *; }

# Keep Moshi adapter classes
-keep class com.squareup.moshi.** { *; }

# Keep classes annotated with @JsonClass(generateAdapter = true)
-keepclasseswithmembers class * {
    @com.squareup.moshi.JsonClass <fields>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep @com.squareup.moshi.JsonClass interface * { *; }
-keep @com.squareup.moshi.JsonAdapter class * { *; }
-keepattributes RuntimeVisibleAnnotations