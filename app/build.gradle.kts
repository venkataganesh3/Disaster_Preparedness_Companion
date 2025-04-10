plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mad"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mad"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation( "com.android.volley:volley:1.2.1")
    implementation("com.google.android.libraries.places:places:4.2.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation(libs.play.services.location)
    implementation(libs.places)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}