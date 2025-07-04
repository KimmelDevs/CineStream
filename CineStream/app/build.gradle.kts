plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.cinestream"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cinestream"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.firebase.firestore)
    implementation("androidx.viewpager2:viewpager2:1.0.0")

        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")// ViewModel scope
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")  // For LiveData
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")   // For coroutine support

    // TabLayout (for TabLayoutMediator)
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson Converter
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation(libs.volley)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx) // Logging Interceptor for debugging
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}