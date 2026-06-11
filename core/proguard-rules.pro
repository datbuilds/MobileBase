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

# Hide original source filenames in release artifacts.
-renamesourcefileattribute SourceFile

# Core relies on Retrofit and annotation metadata during runtime.
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,*Annotation*

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

################################# Gson & Data Classes ##################################
# Keep all data classes for Gson serialization
-keep class com.mobile.base.data.entities.** { *; }
-keep class com.mobile.base.core.core.domain.usecases.** { *; }

# Keep field names with @SerializedName annotation
-keepclassmembers class ** {
    @com.google.gson.annotations.SerializedName <fields>;
}
