import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}


val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())
val kakaoMapKey = localProperties.getProperty("KAKAO_MAP_KEY")?:""
val kakaoManifestMapKey = localProperties.getProperty("KAKAO_MANIFEST_MAP_KEY")?:""



android {
    namespace = "com.example.toiletfinder"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.toiletfinder"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_MAP_KEY", kakaoMapKey)
        manifestPlaceholders["KAKAO_MAP_KEY"] = kakaoManifestMapKey

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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.kakao.maps.open:android:2.11.8")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
}
