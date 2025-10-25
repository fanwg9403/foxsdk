import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "com.sohuglobal.foxsdk"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 21
    }

    testOptions {
        targetSdk = 36
    }

    lint {
        targetSdk = 36
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            consumerProguardFile("consumer-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            javaParameters.set(true)
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    api(libs.androidx.core.ktx)
    api(libs.kotlin.coroutines.core)
    api(libs.kotlin.coroutines.android)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.activity.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.constraintlayout)
    api(libs.recyclerview)

    // 第三方库
    api(libs.qiyukf)
    api(libs.bundles.common.utils)
    api(libs.bundles.common.list)

    // 网络框架
    api(libs.bundles.network.utils)
    api(libs.gson)

    // 支付框架
    api(libs.bundles.pay.utils)
}

// JitPack 发布配置
group = "com.github.fanwg9403"

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.fanwg9403"
            artifactId = "foxsdk"
            version = "1.0.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
