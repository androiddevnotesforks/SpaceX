plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

dependencies {

    // Compose
    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.tooling)
    implementation(Dependencies.Compose.toolingPreview)
    implementation(Dependencies.Compose.runtime)
    implementation(Dependencies.Compose.util)
}