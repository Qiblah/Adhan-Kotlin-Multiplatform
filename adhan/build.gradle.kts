plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    jvm {
        withJava()
        jvmToolchain(17)
    }
    js {
        browser()
        binaries.executable()
    }
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

//        val androidMain by getting {
//            dependencies {
//                implementation(libs.androidx.appcompat)
//                implementation(libs.androidx.activityCompose)
//                implementation(libs.compose.uitooling)
//            }
//        }

//        val androidTest by getting {
//            dependencies {
//                implementation(kotlin("test"))
//            }
//        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

//        val iosMain by getting {
//            dependencies {
//                implementation(libs.kotlinx.datetime)
//            }
//        }

//        val iosTest by getting {
//            dependencies {
//                implementation(kotlin("test"))
//            }
//        }

    }


}