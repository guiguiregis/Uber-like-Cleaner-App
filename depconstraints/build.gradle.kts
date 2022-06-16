plugins {
    id("java-platform")
}

val coroutinesVersion = "1.4.0"

val androidLifecycle = "2.2.0"
val androidxCore = "1.2.0"
val androidxFragment = "1.2.4"
val androidxAppcompat = "1.1.0"
val androidxAnnotation = "1.1.0"
val androidxLegacy = "1.0.0"
val androidxConstraint = "2.0.3"
val androidxRecyclerview = "1.1.0"
val androidxCardView = "1.0.0"
val androidxBrowser = "1.2.0"

val materialCalendar = "2.0.0"

val googleMaterial = "1.2.1"
val places = "2.4.0"
val googlePhoneLib = "8.2.0"

val koinVersion = "2.0.1"

val retrofit = "2.9.0"
val converters = "2.8.1"
val loggingInterceptor = "4.9.0"
val gson = "2.8.6"

val rxPermissions = "0.10.2"
val rxAndroid = "2.1.1"

val spinKit = "1.4.0"
val pageIndicator = "1.0.4"
val rvPageIndicator = "1.2.1"

val localDate = "1.2.0"

val hawk = "2.0.1"

val imagePicker = "2.2.1"
val coil = "1.1.0"
val onfido = "7.2.0"

val lottie = "3.4.4"
val paris = "1.7.2"

val iban = "1.8.0"

dependencies {
    constraints {
        api("${Libs.KOTLIN_STD_LIB}:${Versions.KOTLIN}")
        api("${Libs.KTX_COROUTINES_CORE}:$coroutinesVersion")
        api("${Libs.KTX_COROUTINES_ANDROID}:$coroutinesVersion")

        api("${Libs.ANDROIDX_LIFECYCLE_EXTENSIONS}:$androidLifecycle")
        api("${Libs.ANDROIDX_LIFECYCLE_LIVEDATA}:$androidLifecycle")
        api("${Libs.ANDROIDX_LIFECYCLE_VIEWMODEL_KTX}:$androidLifecycle")
        api("${Libs.ANDROIDX_NAVIGATION_FRAGMENT_KTX}:${Versions.ANDROID_NAVIGATION}")
        api("${Libs.ANDROIDX_NAVIGATION_UI_KTX}:${Versions.ANDROID_NAVIGATION}")
        api("${Libs.ANDROIDX_CORE_KTX}:$androidxCore")
        api("${Libs.ANDROIDX_FRAGMENT_KTX}:$androidxFragment")
        api("${Libs.ANDROIDX_APPCOMPAT}:$androidxAppcompat")
        api("${Libs.ANDROIDX_ANNOTATION}:$androidxAnnotation")
        api("${Libs.ANDROIDX_LEGACY}:$androidxLegacy")
        api("${Libs.ANDROIDX_CONSTRAINTLAYOUT}:$androidxConstraint")
        api("${Libs.ANDROIDX_RECYCLERVIEW}:$androidxRecyclerview")
        api("${Libs.ANDROIDX_CARDVIEW}:$androidxCardView")
        api("${Libs.ANDROIDX_BROWSER}:${androidxBrowser}")

        api("${Libs.MATERIAL_CALENDAR}:$materialCalendar")

        api("${Libs.GOOGLE_MATERIAL}:$googleMaterial")
        api("${Libs.GOOGLE_PLACES}:$places")
        api("${Libs.GOOGLE_FIREBASE_BOM}:${Versions.FIREBASE_BOM}")
        api(Libs.GOOGLE_FIREBASE_ANALYTICS)
        api(Libs.GOOGLE_FIREBASE_CRASHLYTICS)
        api(Libs.GOOGLE_FIREBASE_MESSAGING)

        api("${Libs.KOIN_CORE}:$koinVersion")
        api("${Libs.KOIN_ANDROID}:$koinVersion")
        api("${Libs.KOIN_ANDROIDX_SCOPE}:$koinVersion")
        api("${Libs.KOIN_ANDROIDX_VIEWMODEL}:$koinVersion")

        api("${Libs.RETROFIT}:$retrofit")
        api("${Libs.RETROFIT_GSON_CONVERTER}:$converters")
        api("${Libs.RETROFIT_LOGGING_INTERCEPTOR}:$loggingInterceptor")
        api("${Libs.GOOGLE_GSON}:$gson")

        api("${Libs.RX_PERMISSIONS}:$rxPermissions")
        api("${Libs.RX_JAVA2_ANDROID}:$rxAndroid")

        api("${Libs.LOCAL_DATE}:$localDate")

        api("${Libs.SPINKIT}:$spinKit")
        api("${Libs.PAGE_INDICATOR}:$pageIndicator")
        api("${Libs.RV_PAGE_INDICATOR}:${rvPageIndicator}")
        api("${Libs.HAWK}:$hawk")
        api("${Libs.IMAGE_PICKER}:$imagePicker")
        api("${Libs.COIL}:$coil")
        api("${Libs.LOTTIE}:${lottie}")
        api("${Libs.PARIS}:${paris}")
        api("${Libs.GOOGLE_PHONE_LIB}:${googlePhoneLib}")
        api("${Libs.IBAN}:${iban}")

        api("${Libs.CORE_LIBRARY_DESUGARING}:${Versions.CORE_LIBRARY_DESUGARING}")
    }
}
