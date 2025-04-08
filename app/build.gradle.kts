plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "dev.whysoezzy.bduiproj"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.whysoezzy.bduiproj"
        minSdk = 30
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
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        // Добавляем специальный тип сборки для бенчмарков
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // не используем минификацию для бенчмарков
            isMinifyEnabled = false
            isDebuggable = false
            // Добавляем суффикс для избежания конфликта с релизной версией
            applicationIdSuffix = ".benchmark"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Server API communication
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation ("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("io.coil-kt:coil-compose:2.5.0")

}

// Создаем файл правил Proguard для бенчмарков, если его ещё нет
tasks.register("createBenchmarkRules") {
    doLast {
        val benchmarkRules = file("benchmark-rules.pro")
        if (!benchmarkRules.exists()) {
            benchmarkRules.writeText("""
                # Правила для бенчмарков
                -keepattributes *Annotation*
                -keepclassmembers class * {
                    @org.junit.Test *;
                }
                -keep class androidx.test.** { *; }
                -keep class androidx.benchmark.** { *; }
            """.trimIndent())
        }
    }
}

tasks.getByPath("preBuild").dependsOn("createBenchmarkRules")