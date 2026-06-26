import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.hilt.android)
    alias(libs.plugins.plugin.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutlibraries.plugin)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)

}
val appName = "Habit Diary"

android {
    namespace = "com.charan.habitdiary"
    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }
    androidResources {
        generateLocaleConfig = true
    }

    defaultConfig {
        applicationId = "com.charan.habitdiary"
        minSdk = 26
        targetSdk = 37
        versionCode = 18
        versionName = "0.11.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()

    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(keystorePropertiesFile.inputStream())
    }
    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                storeFile = keystoreProperties.getProperty("storeFile")?.let { file(it) }
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            resValue("string", "app_name", appName)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            resValue("string", "app_name", "$appName-Debug")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    sourceSets {
        getByName("androidTest") {
            assets.directories.add("$projectDir/schemas")
        }
    }

}
androidComponents {
    onVariants { variant ->
        val variantName = variant.name
        val capitalizedVariantName = variantName.replaceFirstChar { it.uppercase() }
        variant.outputs.forEach { output ->
            if (output is com.android.build.api.variant.impl.VariantOutputImpl) {
                val versionName = android.defaultConfig.versionName ?: "1.0"
                output.outputFileName.set("$appName-$variantName-$versionName.apk")
            }
        }
        tasks.register("renameAab$capitalizedVariantName") {
            doLast {
                val versionName = android.defaultConfig.versionName ?: "1.0"
                val bundleDir = layout.buildDirectory.dir("outputs/bundle/$variantName").get().asFile

                bundleDir.listFiles()
                    ?.filter { it.extension == "aab" }
                    ?.forEach { aab ->
                        val newName = "$appName-$variantName-$versionName.aab"
                        aab.renameTo(File(bundleDir, newName))
                        println("Renamed AAB to: $newName")
                    }
            }
        }
        afterEvaluate {
            val bundleTaskName = "bundle$capitalizedVariantName"
            tasks.findByName(bundleTaskName)?.finalizedBy("renameAab$capitalizedVariantName")
        }
    }
}
hilt {
    enableAggregatingTask = false
}
room {
    schemaDirectory("$projectDir/schemas")
    generateKotlin = true
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.animation)
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.media3.exoplayer)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.accompanist.permissions)
    implementation(libs.kotlinx.datetime)
    implementation(libs.compose.multiplatform)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.graphics.shapes)
    implementation(libs.androidx.biometric.compose)
    androidTestImplementation(libs.androidx.room.testing)
    implementation(libs.zoomable.image.coil3)
    implementation(libs.coil.video)
    implementation(libs.androidx.media3.ui.compose.material3)
    implementation(libs.androidx.media3.ui)
    implementation(libs.compose.cloudy)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.compose.adaptive)
    implementation(libs.androidx.compose.adaptive.layout)
    implementation(libs.androidx.compose.adaptive.navigation)
    implementation(libs.androidx.compose.adaptive.navigation3)
    implementation(libs.richeditor.compose)
    debugImplementation(libs.leakcanary)


}
