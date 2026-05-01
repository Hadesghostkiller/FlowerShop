plugins {
    alias(libs.plugins.android.application) apply false

    // 👉 THÊM DÒNG NÀY
    id("com.google.gms.google-services") version "4.4.2" apply false
}