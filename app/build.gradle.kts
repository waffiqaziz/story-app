import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import kotlin.apply

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.mapsplatform.secrets)
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.parcelize)
}

android {
  compileSdk = 36
  namespace = "com.dicoding.storyapp"

  defaultConfig {
    applicationId = "com.dicoding.storyapp"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    // base url
    buildConfigField("String", "API_URL", "\"https://story-api.dicoding.dev/v1/\"")

    // test token
    buildConfigField("String", "TEST_TOKEN", "\"${getToken("TEST_TOKEN")}\"")

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
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    isCoreLibraryDesugaringEnabled = true
  }
  kotlin {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_21
    }
  }
  packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
  buildFeatures {
    viewBinding = true
    buildConfig = true
  }

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

ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
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

  implementation(libs.androidx.espresso.idling.resource)

  //testing
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.rules)
  androidTestImplementation(libs.androidx.test.core.ktx)

  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.espresso.contrib)
  androidTestImplementation(libs.androidx.espresso.intents)
  androidTestImplementation(libs.androidx.uiautomator)

  //mock web server
  androidTestImplementation(libs.mockwebserver)
  androidTestImplementation(libs.okhttp.tls)

  //mockito
  testImplementation(libs.androidx.core.testing)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.mockito)
  testImplementation(libs.mockito.inline)
}

fun Project.getToken(key: String): String {
  val props = Properties().apply {
    File(project.rootDir, "local.properties").inputStream().use { load(it) }
  }
  return props.getProperty(key)
}
