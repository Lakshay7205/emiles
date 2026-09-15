    import org.jetbrains.compose.desktop.application.dsl.TargetFormat
    import org.jetbrains.kotlin.gradle.dsl.JvmTarget

    plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidApplication)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.kotlinSerialization) // Add this
        alias(libs.plugins.composeCompiler)
        kotlin("native.cocoapods")

    }

    kotlin {
        cocoapods {
            summary = "GreenMiles iOS"
            homepage = "https://example.com"
            ios.deploymentTarget = "16.0"
            version = "1.0.0"

            pod("GoogleMaps", version = "10.7.0")
            pod("GooglePlaces", version = "10.6.0")

            framework {
                baseName = "composeApp"
                isStatic = true
            }
        }




        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "composeApp"
                isStatic = true
            }
        }

        sourceSets {
            androidMain.dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.ktor.client.android)
                implementation("com.google.maps.android:maps-compose:2.11.4")
                implementation("com.google.android.gms:play-services-maps:18.2.0")
                implementation(libs.koin.android)
                implementation("com.google.android.libraries.places:places:3.3.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
                implementation("io.insert-koin:koin-android:3.5.3")
                implementation("io.insert-koin:koin-androidx-compose:3.5.3")
                implementation("com.russhwolf:multiplatform-settings:1.1.1")
            }
            commonMain.dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(compose.materialIconsExtended)

                implementation("io.github.aakira:napier:2.6.1")
                implementation("co.touchlab:kermit:2.0.3")
                implementation("io.insert-koin:koin-core:3.5.3")


                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
                implementation(libs.ktor.client.logging)

                implementation(libs.composeIcons.material)


                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor3)

                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.navigation.compose)

                implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")
            }
            commonTest.dependencies {
                implementation(libs.kotlin.test)
            }
            iosMain.dependencies {

                implementation(libs.ktor.client.darwin)
            }
        }
    }

    android {
        namespace = "com.state.greenmiles.com_state_greenmiles"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        defaultConfig {
            applicationId = "com.state.greenmiles.com_state_greenmiles"
            minSdk = libs.versions.android.minSdk.get().toInt()
            targetSdk = libs.versions.android.targetSdk.get().toInt()
            versionCode = 1
            versionName = "1.0"
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    dependencies {
        debugImplementation(compose.uiTooling)
    }

