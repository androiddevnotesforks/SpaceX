object Dependencies {

    const val ktlint = "com.pinterest:ktlint:0.45.2"
    const val coil = "io.coil-kt:coil-compose:2.1.0"
    const val gson = "com.google.code.gson:gson:2.9.0"
    const val timber = "com.jakewharton.timber:timber:5.0.1"

    object Kotlin {
        const val version = "1.7.0"
    }

    object Koin {
        const val version = "3.2.0"
        const val koin = "io.insert-koin:koin-android:$version"
        const val compose = "io.insert-koin:koin-androidx-compose:$version"
    }

    object OkHttp {
        const val okHttp = "com.squareup.okhttp3:okhttp:4.10.0"
        const val okLog = "com.github.simonpercic:oklog3:2.3.0"
    }

    object Gradle {
        const val androidBuildPlugin = "com.android.tools.build:gradle:7.2.1"
        const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"

        object VersionsPlugin {
            const val id = "com.github.ben-manes.versions"
            const val version = "0.42.0"
        }
    }

    object AndroidX {
        object Ktx {
            const val core = "androidx.core:core-ktx:1.8.0"
        }
    }

    object Compose {
        const val version = "1.2.0-rc01"
        const val compiler = "1.2.0"
        const val ui = "androidx.compose.ui:ui:$version"
        const val material = "androidx.compose.material:material:$version"
        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val toolingPreview = "androidx.compose.ui:ui-tooling-preview:$version"
        const val paging = "androidx.paging:paging-compose:1.0.0-alpha15"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.0-rc02"
        const val activity = "androidx.activity:activity-compose:1.5.0"

        object Accompanist {
            private const val version = "0.24.13-rc"

            const val pager = "com.google.accompanist:accompanist-pager-indicators:$version"
            const val pagerIndicator = "com.google.accompanist:accompanist-pager:$version"
            const val systemUiController =
                "com.google.accompanist:accompanist-systemuicontroller:$version"
        }
    }

    object Retrofit {
        const val version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:$version"
    }

    object Pluto {
        const val version = "2.0.2-beta2"

        object Debug {
            const val pluto = "com.plutolib:pluto:$version"
            const val network = "com.plutolib.plugins:network:$version"
            const val exceptions = "com.plutolib.plugins:exceptions:$version"

        }

        object Release {
            const val pluto = "com.plutolib:pluto-no-op:$version"
            const val network = "com.plutolib.plugins:network-no-op:$version"
            const val exceptions = "com.plutolib.plugins:exceptions-no-op:$version"
        }
    }
}