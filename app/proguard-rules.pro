# Add project-specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified in
# proguard-android-optimize.txt

# Keep Compose metadata
-keep class androidx.compose.** { *; }

# Keep data models
-keep class com.filerhythm.app.data.model.** { *; }
