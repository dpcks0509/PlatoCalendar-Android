import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "pnu.plato.calendar"
    compileSdk = 36

    defaultConfig {
        applicationId = "pusan.university.plato_calendar"
        minSdk = 26
        targetSdk = 36
        versionCode = 5
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "PLATO_BASE_URL", localProperties.getProperty("plato.base.url"))
        buildConfigField("String", "PNU_BASE_URL", localProperties.getProperty("pnu.base.url"))
        buildConfigField("String", "ADMOB_APP_ID", "\"${localProperties.getProperty("admob.app.id")}\"")

        manifestPlaceholders["ADMOB_APP_ID"] = localProperties.getProperty("admob.app.id")
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BANNER_AD_UNIT_ID",
                localProperties.getProperty("banner.ad.sample.id")
            )
        }

        release {
            buildConfigField(
                "String",
                "BANNER_AD_UNIT_ID",
                localProperties.getProperty("banner.ad.unit.id")
            )

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)

    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.compose.test)

    implementation(libs.bundles.lifecycle)
    implementation(libs.navigation.compose)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.serialization)
    implementation(libs.bundles.network)
    implementation(libs.bundles.coil)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.google.admob)

    kapt(libs.bundles.hilt.kapt)
}

kapt {
    correctErrorTypes = true
}
