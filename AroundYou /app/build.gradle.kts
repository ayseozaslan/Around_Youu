
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.navigation.safe.args)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.google.gms.google.services)    // KSP plugin

}

android {
    namespace = "com.ayse.aroundyou"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ayse.aroundyou"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        val mapsKey = properties.getProperty("MAPS_API_KEY") ?: ""

        // ✅ BuildConfig alanı
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsKey\"")
        // ✅ XML string olarak
        resValue("string", "MAPS_API_KEY", mapsKey)

        val translateKey = properties.getProperty("TRANSLATE_API_KEY") ?: ""
        buildConfigField("String", "TRANSLATE_API_KEY", "\"$translateKey\"")
        resValue("string","TRANSLATE_API_KEY", translateKey)

        val geminiKey = properties.getProperty("GEMINI_API_KEY") ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
        resValue("string","GEMINI_API_KEY", geminiKey)

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.support.annotations)
    implementation(libs.androidx.runtime)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.preference.ktx)
    // implementation(libs.firebase.firestore.ktx)
    //implementation(libs.androidx.navigation.compose.jvmstubs)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.navigation.compose)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    //api den veri almak içn gerekli olanlar
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    //hilt lifecycle
    implementation(libs.viewmodel)
    implementation(libs.livedata)
    implementation(libs.lifecycle.runtime)

    //hilt ksp
    implementation(libs.hilt)
    ksp(libs.hilt.kapt)
    implementation(libs.hilt.navigation.compose)
    // implementation(libs.navigation.compose)

    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation(libs.google.auth)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
/*
    implementation(libs.google.maps)
    implementation(libs.google.location)

 */

    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.osmdroid)

    implementation("androidx.compose.ui:ui-viewbinding:1.5.0")

    implementation(libs.coil.core)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.gif)

    implementation ("androidx.compose.material3:material3:1.2.0") // Material3 Compose
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.30.1") // System UI controller
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
    implementation ("androidx.compose.material3:material3:1.2.0")

    implementation ("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation ("com.google.android.gms:play-services-ads:23.1.0")


}