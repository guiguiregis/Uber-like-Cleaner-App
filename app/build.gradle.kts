plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.appdistribution")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
}

android {
    compileSdkVersion(Versions.COMPILE_SDK)
    defaultConfig {
        applicationId = "com.wolfpackdigital.kliner.partner"
        minSdkVersion(Versions.MIN_SDK)
        targetSdkVersion(Versions.TARGET_SDK)
        versionCode = getBuildVersion()
        versionName = Versions.VERSION_NAME
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        getByName("debug") {}
        create("release") {
            storeFile = file("../kliner_partner_keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    val dimensionName = "env"
    flavorDimensions(dimensionName)
    productFlavors {
        create("beta") {
            dimension = dimensionName
            resValue("string", "app_name", "(B) Kliner Partner")
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta"
            firebaseAppDistribution {
                testers = "wolfpackdigitaltesting@gmail.com"
            }
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://kliner-api-beta.herokuapp.com/api/v1/\""
            )
        }

        create("staging") {
            dimension = dimensionName
            resValue("string", "app_name", "(S) Kliner Partner")
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            firebaseAppDistribution {
                testers = "wolfpackdigitaltesting@gmail.com,bngouo@kliner.fr"
            }
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://kliner-api-staging.herokuapp.com/api/v1/\""
            )
        }

        create("production") {
            dimension = dimensionName
            versionCode = 5
            versionName = Versions.VERSION_NAME
            resValue("string", "app_name", "Kliner Partner")
            firebaseAppDistribution {
                testers = "wolfpackdigitaltesting@gmail.com,bngouo@kliner.fr"
            }
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://kliner-api.herokuapp.com/api/v1/\""
            )
        }
    }

    buildFeatures.dataBinding = true

    lintOptions {
        isAbortOnError = true
        isWarningsAsErrors = true
        lintConfig = file("lint.xml")
        disable("GradleDependency")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions.jvmTarget = "1.8"
}

fun getBuildVersion(): Int {
    var version = Versions.VERSION_CODE
    val buildNumber = System.getenv("CIRCLE_BUILD_NUM")
    buildNumber?.toIntOrNull()?.let {
        version += it
    }
    println("VersionCode: $version")
    return version
}

dependencies {
    api(platform(project(":depconstraints")))
    kapt(platform(project(":depconstraints")))

    // Kotlin & Coroutines
    implementation(Libs.KOTLIN_STD_LIB)
    implementation(Libs.KTX_COROUTINES_CORE)
    implementation(Libs.KTX_COROUTINES_ANDROID)

    // Lifecycle, LiveData, ViewModel
    implementation(Libs.ANDROIDX_LIFECYCLE_EXTENSIONS)
    implementation(Libs.ANDROIDX_LIFECYCLE_LIVEDATA)
    implementation(Libs.ANDROIDX_LIFECYCLE_VIEWMODEL_KTX)

    // Navigation
    implementation(Libs.ANDROIDX_NAVIGATION_FRAGMENT_KTX)
    implementation(Libs.ANDROIDX_NAVIGATION_UI_KTX)

    // Android defaults
    implementation(Libs.ANDROIDX_CORE_KTX)
    implementation(Libs.ANDROIDX_FRAGMENT_KTX)
    implementation(Libs.ANDROIDX_APPCOMPAT)
    implementation(Libs.ANDROIDX_ANNOTATION)
    implementation(Libs.ANDROIDX_LEGACY)
    implementation(Libs.ANDROIDX_CONSTRAINTLAYOUT)
    implementation(Libs.ANDROIDX_RECYCLERVIEW)
    implementation(Libs.ANDROIDX_CARDVIEW)
    implementation(Libs.ANDROIDX_BROWSER)
    implementation(Libs.GOOGLE_MATERIAL)
    implementation(Libs.GOOGLE_PLACES)

    // Material calendar
    implementation(Libs.MATERIAL_CALENDAR)

    // Firebase
    implementation(platform("${Libs.GOOGLE_FIREBASE_BOM}:${Versions.FIREBASE_BOM}"))
    implementation(Libs.GOOGLE_FIREBASE_ANALYTICS)
    implementation(Libs.GOOGLE_FIREBASE_CRASHLYTICS)
    implementation(Libs.GOOGLE_FIREBASE_MESSAGING)

    // Koin
    implementation(Libs.KOIN_CORE)
    implementation(Libs.KOIN_ANDROID)
    implementation(Libs.KOIN_ANDROIDX_SCOPE)
    implementation(Libs.KOIN_ANDROIDX_VIEWMODEL)

    // Networking
    implementation(Libs.RETROFIT)
    implementation(Libs.RETROFIT_GSON_CONVERTER)
    implementation(Libs.RETROFIT_LOGGING_INTERCEPTOR)
    implementation(Libs.GOOGLE_GSON)

    // Permissions
    implementation(Libs.RX_PERMISSIONS)
    implementation(Libs.RX_JAVA2_ANDROID)

    // Misc
    implementation(Libs.SPINKIT)
    implementation(Libs.PAGE_INDICATOR)
    implementation(Libs.RV_PAGE_INDICATOR)
    implementation(Libs.IMAGE_PICKER)
    implementation(Libs.COIL)
    implementation(Libs.LOTTIE)
    implementation(Libs.PARIS)
    implementation(Libs.GOOGLE_PHONE_LIB)
    implementation(Libs.IBAN)

    // Hawk
    implementation(Libs.HAWK)

    //
    implementation(Libs.LOCAL_DATE)

    //Library desugaring
    coreLibraryDesugaring("${Libs.CORE_LIBRARY_DESUGARING}:${Versions.CORE_LIBRARY_DESUGARING}")
}