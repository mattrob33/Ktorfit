plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
}

version = "1.0-SNAPSHOT"

val ktorVersion = "2.0.0-beta-1"//"2.0.0-eap-354"
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    iosX64()
    iosArm64()
    js(IR) {
        this.nodejs()
        binaries.executable() // not applicable to BOTH, see details below
    }
    linuxX64() {
        binaries {
            executable()
        }
    }

    macosX64("macOS")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":ktorfit-lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

            }
        }
        val linuxX64Main by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core-native:1.3.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
                implementation("io.ktor:ktor-client-curl:2.0.0-eap-362")

            }
        }


        val jvmMain by getting {
            //   kotlin.srcDir("build/kspCaches/jvmMain/")

            dependencies {
                implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.3.9")
                implementation("io.reactivex.rxjava3:rxjava:3.1.3")
                implementation("io.ktor:ktor-client-gson:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")

            }
        }

        val jvmTest by getting {
            //   kotlin.srcDir("build/kspCaches/jvmMain/")


            dependencies {
                dependsOn(jvmMain)
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("junit:junit:4.13.2")


            }
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")

            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }

    }
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
    }
}
dependencies {
    add(
        "kspMetadata",
        project(":ktorfit-ksp")
    )
    add("kspJvm", project(":ktorfit-ksp"))
    add("kspIosX64", project(":ktorfit-ksp"))

    add("kspJvmTest", project(":ktorfit-ksp"))
    add("kspJs", project(":ktorfit-ksp"))
    add("kspLinuxX64", project(":ktorfit-ksp"))

    // The universal "ksp" configuration has performance issue and is deprecated on multiplatform since 1.0.1
    // ksp(project(":test-processor"))
}
