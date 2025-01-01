
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.englishquiz"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.englishquiz"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.englishquiz.CustomTestRunner"
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        packaging { resources { excludes += "/META-INF/*" } }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }

    sourceSets {
        getByName("test") {
            resources.srcDirs("src/test/resources")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.gson)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.process)

    // hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Hilt For instrumented tests.
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")

    // Hilt For Robolectric tests.
    testImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptTest("com.google.dagger:hilt-android-compiler:2.51.1")

    // room database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.rules)
    kapt("androidx.room:room-compiler:2.6.1") // For annotation processing
    implementation("androidx.room:room-ktx:2.6.1") // For Coroutines and Flow support

    // For konfetti animation
    implementation("nl.dionsegijn:konfetti-xml:2.0.4")

    // JUnit for unit testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // For coroutine testing
    testImplementation("io.mockk:mockk:1.13.8") // For mocking dependencies
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.3")
    testImplementation("org.robolectric:robolectric:4.14")
    testImplementation("com.google.truth:truth:1.1.5")
    // for testing flow
    testImplementation("app.cash.turbine:turbine:1.2.0")

    // for instrument test
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("app.cash.turbine:turbine:1.2.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.8.0")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
