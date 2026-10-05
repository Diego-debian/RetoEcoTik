# RetoEcoTik Proguard rules
# Preserve Room schema and entities
-keep class com.retoecotik.app.data.local.entity.** { *; }
-dontwarn androidx.room.paging.**
