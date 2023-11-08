plugins {
    id("com.android.application")
    id ("kotlin-android")
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.uni_cob"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.uni_cob"
        minSdk = 28
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("com.facebook.android:facebook-applinks:latest.release")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.2.0")
   /* implementation("com.google.firebase:firebase-firestore:24.8.1")*/
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.firebase:firebase-firestore-ktx:24.9.1")
    implementation ("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation("androidx.annotation:annotation:1.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.android.car.ui:car-ui-lib:2.5.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    testImplementation("junit:junit:4.13.2")
    implementation ("com.facebook.android:facebook-android-sdk:[8,9)")
    implementation ("com.facebook.android:facebook-android-sdk:latest.release")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("me.relex:circleindicator:2.1.6")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("de.hdodenhof:circleimageview:2.2.0")




}