buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Downgraded from 8.13.0 → 8.12.0 for compatibility
        classpath("com.android.tools.build:gradle:8.12.0")
        classpath("com.google.gms:google-services:4.3.15")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}