import java.util.Properties

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists())
        f.inputStream().use { load(it) }
}
val sportsDbKey = localProperties.getProperty("THE_SPORTS_DB_API_KEY") ?: "3"

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.kev.data"
    compileSdk = 36
    defaultConfig {
        minSdk = 28
        buildConfigField(
            "String",
            "TSDB_API_KEY",
            "\"$sportsDbKey\""
        )
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)

    // OkHttp logging interceptor
    implementation(libs.logging.interceptor)

    // Moshi + Retrofit converter
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.androidx.junit.ktx)
    ksp(libs.moshi.kotlin.codegen)

    //Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.core.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.paging)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.converter.moshi)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockwebserver)

    //Robolectric
    testImplementation(libs.robolectric)

    implementation(project(":domain"))
}