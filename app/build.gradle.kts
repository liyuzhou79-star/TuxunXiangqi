plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.tuxunxiangqi.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tuxunxiangqi.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 222
        versionName = "1.211.0"
    }

    signingConfigs {
        // Pinned TEST signing: without this, every CI runner generates
        // a fresh debug keystore and test builds can never be installed
        // over each other. This key is a throwaway committed on purpose
        // (standard debug alias/password); it is NOT a release key.
        create("testPin") {
            storeFile = rootProject.file("tools/test-signing.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        // Release signing: only created when the CI workflow restores the
        // release keystore from repository secrets and passes its path and
        // credentials through the environment. The key itself and its
        // passwords never live in this repository.
        if (System.getenv("TUXUNXIANGQI_KEYSTORE_PATH") != null) {
            create("release") {
                storeFile = file(System.getenv("TUXUNXIANGQI_KEYSTORE_PATH"))
                storePassword = System.getenv("TUXUNXIANGQI_STORE_PASSWORD")
                keyAlias = System.getenv("TUXUNXIANGQI_KEY_ALIAS")
                keyPassword = System.getenv("TUXUNXIANGQI_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("testPin")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (System.getenv("TUXUNXIANGQI_KEYSTORE_PATH") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    androidResources {
        // Keep the engine NNUE network stored raw in the APK so the app
        // can copy out the exact bytes Pikafish expects.
        noCompress += "nnue"
    }
    packaging {
        jniLibs {
            // The Pikafish executable ships as libpikafish.so and must be
            // extracted to nativeLibraryDir so the app can exec it.
            useLegacyPackaging = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-text")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    testImplementation("junit:junit:4.13.2")
}
