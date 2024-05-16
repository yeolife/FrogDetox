plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id ("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services");
}

android {
    namespace = "com.ssafy.frogdetox"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ssafy.frogdetox"
        minSdk = 26
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding{
        enable = true
    }
    dataBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // material
    implementation("com.google.android.material:material:1.0.0")
    implementation("com.tbuonomo:dotsindicator:5.0")

    // 이미지 파싱
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
    
    // 웹 이미지 URL 보여줌
    implementation("io.coil-kt:coil:2.6.0")

    // viewPager2
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // firebase
    implementation("com.google.firebase:firebase-database-ktx:20.0.5")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // firebase auth 에서 필요한 의존성 추가
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    // fcm
    implementation (platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")

    //framework ktx dependency 추가
    implementation ("androidx.fragment:fragment-ktx:1.6.2")

    // calenarView
    implementation("com.kizitonwose.calendar:view:2.5.0")

    // swipe delete
    implementation("it.xabaras.android:recyclerview-swipedecorator:1.4")
}