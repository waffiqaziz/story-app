plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  id("kotlin-parcelize")
  alias(libs.plugins.mapsplatform.secrets)
  alias(libs.plugins.ksp)
}

android {
  compileSdk = 36
  namespace = "com.dicoding.storyapp"

  defaultConfig {
    applicationId = "com.dicoding.storyapp"
    minSdk = 23
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    // base url
    buildConfigField("String", "API_URL", "\"https://story-api.dicoding.dev/v1/\"")

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  secrets {
    propertiesFileName = "secrets.properties"

    defaultPropertiesFileName = "local.defaults.properties"

    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    isCoreLibraryDesugaringEnabled = true
  }
  kotlinOptions.jvmTarget = "17"
  packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
  buildFeatures {
    viewBinding = true
    buildConfig = true
  }

  @Suppress("UnstableApiUsage")
  testOptions {
    animationsDisabled = true
    unitTests.apply {
      isReturnDefaultValues = true
      isIncludeAndroidResources = true
    }
    unitTests.all {
      it.testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      }
    }
  }
}

dependencies {
  coreLibraryDesugaring(libs.desugar.jdk.libs)
  implementation(libs.androidx.legacy.support.v4)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.activity.ktx)

  implementation(libs.google.material)
  implementation(libs.androidx.annotation)

  //splashscreen API
  implementation(libs.androidx.core.splashscreen)

  implementation(libs.androidx.lifecycle.livedata.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.datastore.preferences)

  implementation(libs.glide)
  implementation(libs.kotlinx.coroutines.android)

  //retrofit
  implementation(libs.retrofit)
  implementation(libs.converter.gson)
  implementation(libs.logging.interceptor)

  //cameraX
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)

  //room & paging
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.room.paging)
  implementation(libs.androidx.paging.runtime.ktx)

  //google maps
  implementation(libs.play.services.maps)
  implementation(libs.play.services.location)

  //testing
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.rules)
  androidTestImplementation(libs.androidx.test.core.ktx)

  implementation(libs.androidx.espresso.idling.resource)
  androidTestImplementation(libs.espresso.core)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.espresso.contrib)
  androidTestImplementation(libs.espresso.contrib)
  androidTestImplementation(libs.androidx.espresso.intents)

  //mock web server
  androidTestImplementation(libs.mockwebserver)
  androidTestImplementation(libs.okhttp.tls)

  //mockito
  testImplementation(libs.mockito)
  testImplementation(libs.mockito.inline)

  testImplementation(libs.junit)
  testImplementation(libs.mockito.inline)

  //special testing
  testImplementation(libs.androidx.core.testing)
  testImplementation(libs.kotlinx.coroutines.test)
}
