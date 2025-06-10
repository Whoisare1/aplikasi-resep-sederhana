plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.resepapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.resepapp"
        minSdk = 21
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        // Anda mungkin juga ingin menambahkan ini untuk konsistensi
        languageVersion = "1.9" // Sesuaikan dengan versi Kotlin di root build.gradle.kts
        apiVersion = "1.9" // Sesuaikan dengan versi Kotlin di root build.gradle.kts
    }
    buildFeatures {
        viewBinding = true // Aktifkan View Binding
    }
}

dependencies {
    // Core KTX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Room components
    val room_version = "2.6.1" // Sesuaikan dengan versi Room terbaru
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // Lifecycle components (ViewModel and LiveData)
    val lifecycle_version = "2.8.0" // Sesuaikan dengan versi Lifecycle terbaru
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    // optional - SavedState for ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
    // Annotation processor for lifecycle (if needed, though KTX usually handles it)
    // kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coil (untuk memuat gambar, jika nanti diperlukan dari URL/path)
    // Jika hanya dari drawable/local file, mungkin tidak perlu Coil
    // implementation("io.coil-kt:coil:2.6.0") // Sesuaikan versi terbaru

    implementation("com.google.code.gson:gson:2.10.1")

    // Untuk pengujian
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("io.coil-kt:coil:2.6.0")
}