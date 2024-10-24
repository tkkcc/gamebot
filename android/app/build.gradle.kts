@file:OptIn(ExperimentalPathApi::class)

import com.android.tools.r8.internal.rf
import java.util.Locale
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteRecursively

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
//    alias(libs.plugins.android.rust)
    alias(libs.plugins.serialization)

    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)

    id("dev.rikka.tools.refine")
}

android {
    namespace = "gamebot.host"
    compileSdk = 34
//    ndkVersion = "26.3.11579264"

    defaultConfig {
        applicationId = "gamebot.host"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures {
        aidl = true
        buildConfig = true
    }
    compileOptions {
//        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,DEPENDENCIES,INDEX.LIST}"
            excludes += "/META-INF/io.netty.versions.properties"
//            merges += "plugin.properties"
        }
    }
}
composeCompiler {
    enableStrongSkippingMode = true
}

class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File
) : CommandLineArgumentProvider {

    init {
        schemaDir.mkdirs()
    }

    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}

// For KSP, configure using KSP extension:
ksp {
    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
}


androidComponents.onVariants { variant ->
    val src = Path(projectDir.absolutePath, "..", "..","host")
    val dst = Path(projectDir.absolutePath, "src", "main", "jniLibs")

    // skip build and bundle library on release
    if (variant.buildType == "release") {
        dst.deleteRecursively()
        return@onVariants
    }

    val target = if (variant.buildType == "release") {
        listOf("x86", "x86_64", "arm64-v8a")
//        listOf( "x86","x86_64",)
    } else {
        listOf("x86", "x86_64", "arm64-v8a")
//        listOf("arm64-v8a")
//        listOf("x86","x86_64",)
    }


    val cmd = mutableListOf("cargo", "ndk").apply {
        add("-o")
        add(dst.absolutePathString())
        add("-p")
        add(android.defaultConfig.minSdkVersion!!.apiLevel.toString())
        target.forEach {
            add("-t")
            add(it)
        }


        add("build")
        if (variant.buildType == "release") {
            add("--release")
        }

    }


    val variantName =
        variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val cargoTask = task<Exec>("cargo${variantName}") {
        workingDir(src)


            commandLine(cmd)

    }

    project.afterEvaluate {
        val mergeTask = project.tasks.getByName("merge${variantName}JniLibFolders")
        mergeTask.dependsOn(cargoTask)
    }
}



dependencies {
//    coreLibraryDesugaring(libs.desugar)
    implementation(libs.jgit)
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


//    implementation(libs.ktor.server.core)
//    implementation(libs.ktor.server.netty)
//    implementation(libs.ktor.server.negotiation)
//    implementation(libs.ktor.server.json)

    implementation(libs.lyricist)
//    ksp(libs.lyricist.processor)
    implementation(libs.shizuku)
    implementation(libs.shizuku.provider)
    implementation(libs.refine)
    compileOnly(project(":hidden"))
    implementation(libs.rootCore)
    implementation(libs.rootService)
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")
    implementation(libs.serialization.json)
    implementation(libs.serialization.cbor)
    implementation(libs.compose.navigation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.room.runtime)

    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.appcompat)
    implementation(libs.compose.webview)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.github.only52607:compose-floating-window:1.0")
    implementation("io.github.torrydo:floating-bubble-view:0.6.5")
    implementation("androidx.webkit:webkit:1.12.1")

//    implementation("com.google.mlkit:text-recognition-chinese:16.0.1")
//    implementation("com.microsoft.onnxruntime:onnxruntime-android:latest.release")
//    implementation("org.eclipse.jgit:org.eclipse.jgit.lfs:4.3.0.201604071810-r")


//    implementation("io.ktor:ktor-client-core:3.2.2")
//    implementation("io.ktor:ktor-client-cio:3.2.2")

//    implementation(libs.ktor.client.core)
//    implementation(libs.ktor.client.android)
//    implementation("org.slf4j:slf4j-android:1.7.36")
//    implementation("io.ktor:ktor-client-logging:3.0.0")
//    implementation("io.ktor:ktor-client-encoding:3.0.0")

    implementation(libs.ktor.client.okhttp)
//    implementation("com.github.khushpanchal:Ketch:2.0.2") // Use latest available version
//    implementation("com.github.amitshekhariitbhu:PRDownloader:1.0.1")
//    implementation("com.akuleshov7:ktoml-core:0.5.1")

    implementation("io.github.jonanorman.android.webviewup:core:0.1.0")

}