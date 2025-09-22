Project: AINotes (Android, Kotlin, Jetpack Compose)

This document captures project‑specific notes to accelerate development, builds, testing, and debugging. It assumes an experienced Android/Kotlin developer.

1) Build and configuration requirements

- JDK runtime used by Gradle/AGP
  - Android Gradle Plugin (AGP) 8.13.0 requires running Gradle with a Java 11+ runtime. JDK 17 is recommended and well‑supported with AGP 8.x.
  - Symptom if wrong JDK is used: Build fails during configuration with a message like “Dependency requires at least JVM runtime version 11. This build uses a Java 8 JVM.” Fix by ensuring Gradle runs on JDK 11+ (ideally 17).

- How to point Gradle to a specific JDK
  - Preferred: Use the JBR/JDK bundled with Android Studio (File > Settings > Build, Execution, Deployment > Gradle > Gradle JDK). The bundled JBR path typically looks like:
    - Windows: C:\Program Files\Android\Android Studio\jbr
  - CLI (temporary for a single shell):
    - PowerShell: $env:JAVA_HOME = "C:\\path\\to\\jdk-17"; $env:PATH = "$env:JAVA_HOME\\bin;$env:PATH"
  - Project override (optional): Set org.gradle.java.home in gradle.properties to a JDK 17 path if you must force the wrapper to use it:
    - org.gradle.java.home=C:\\path\\to\\jdk-17
  - Verify with: .\gradlew.bat -v (check “JVM:” line)

- Android SDK / compileSdk
  - compileSdk = 36 and targetSdk = 36. Install the corresponding Android SDK (Android 15 / API 36 preview if applicable) via Android Studio SDK Manager.
  - Ensure build tools and platform tools are updated.

- Kotlin and Gradle
  - Kotlin: 2.0.21; Gradle wrapper: 8.13; AGP: 8.13.0 (see gradle/libs.versions.toml).
  - Module Java compatibility is set to Java 11 (compileOptions + kotlinOptions). This is independent of the JDK that Gradle itself runs under. The Gradle runtime should still be JDK 11+ (prefer 17).

- First full build (CLI)
  - .\gradlew.bat help           # quick sanity check of wrapper/JDK
  - .\gradlew.bat assembleDebug  # build app

2) Testing: configuring and running

- Unit tests (local JVM tests)
  - Location: app/src/test/java/... (JUnit4 by default via junit:4.13.2)
  - Run all: .\gradlew.bat test
  - Run only app module unit tests: .\gradlew.bat :app:test
  - Run a single class: .\gradlew.bat :app:test --tests "com.example.ainotes.YourTestClass"
  - Notes:
    - These run on the local JVM and do not require an emulator/device.
    - Keep unit tests free of Android framework types. Use Robolectric only if you add it explicitly (not present by default).

- Instrumented tests (on device/emulator)
  - Location: app/src/androidTest/java/...
  - Runner: androidx.test.runner.AndroidJUnitRunner (already configured)
  - Start an emulator or connect a device with USB debugging, then:
    - .\gradlew.bat :app:connectedAndroidTest
  - Compose UI testing is available via androidx.compose.ui:ui-test-junit4 and ui-test-manifest deps already declared.

- Typical troubleshooting
  - JDK mismatch: ensure Gradle runs with JDK 11+ (prefer 17). See section 1.
  - Missing SDK/Build tools: use SDK Manager to install API 36 platform + latest build tools.
  - Emulator not found for instrumented tests: start an AVD via Android Studio or sdkmanager/avdmanager.

3) How to add a new unit test (example)

Below is a minimal JUnit4 unit test you can add under app/src/test/java/com/example/ainotes/SampleSanityTest.kt. It exercises pure Kotlin logic and does not depend on Android.

package com.example.ainotes

import org.junit.Assert.assertEquals
import org.junit.Test

class SampleSanityTest {
    @Test
    fun `string template produces expected greeting`() {
        val name = "Android"
        val text = "Hello $name!"
        assertEquals("Hello Android!", text)
    }
}

- Steps to run it
  1) Ensure Gradle uses JDK 11+ (prefer 17). For Windows PowerShell, e.g.:
     $env:JAVA_HOME = "C:\\Program Files\\Android\\Android Studio\\jbr"; $env:PATH = "$env:JAVA_HOME\\bin;$env:PATH"
     Verify: .\gradlew.bat -v
  2) Place the file at app/src/test/java/com/example/ainotes/SampleSanityTest.kt (create directories as needed).
  3) Run: .\gradlew.bat :app:test --tests "com.example.ainotes.SampleSanityTest"
  4) You should see BUILD SUCCESSFUL and 1 test executed.

- Clean up
  - The file is just for demonstration. You may remove it after verifying your toolchain.

4) Adding instrumented Compose UI tests (tips)

- The project already includes Compose BOM and ui-test-junit4. A skeleton test might look like:

package com.example.ainotes

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class MainActivityUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun launches() {
        // Example usage; setContent and perform assertions as needed
        // compose.setContent { AINotesTheme { /* UI */ } }
        // compose.onNodeWithText("Hello Android!").assertExists()
    }
}

- Run on an emulator/device: .\gradlew.bat :app:connectedAndroidTest

5) Code style and project conventions

- Kotlin style: Follow Kotlin official code style (Android Studio default). Use .kt files with top‑level functions where appropriate. Prefer explicit visibility modifiers when relevant.
- Compose best‑practices:
  - Keep Composables stateless where feasible; hoist state.
  - Provide previews with stable parameters only; avoid side effects in @Preview.
  - For text/resources, prefer stringResource(R.string.id) over hardcoded strings when moving beyond samples.
- Gradle Kotlin DSL (build.gradle.kts):
  - Shared versions are centralized in gradle/libs.versions.toml. Prefer adding dependencies via libs.* aliases.
  - Keep compileSdk/targetSdk aligned; upgrade SDK and plugin versions in lockstep with JDK updates.

6) Known environment gotcha (as observed during automation)

- If you see a failure similar to:
  “Could not resolve com.android.tools.build:gradle:8.13.0 … Dependency requires at least JVM runtime version 11. This build uses a Java 8 JVM.”
  - Resolution: Run Gradle with JDK 11 or 17 (see Section 1). In Android Studio, simply select the bundled JBR for the Gradle JDK.

7) Quick command reference (Windows/PowerShell)

- Set Gradle JDK for the current shell only:
  $env:JAVA_HOME = "C:\\Program Files\\Android\\Android Studio\\jbr"; $env:PATH = "$env:JAVA_HOME\\bin;$env:PATH"
- Build debug APK: .\gradlew.bat :app:assembleDebug
- Run unit tests: .\gradlew.bat :app:test
- Run one test: .\gradlew.bat :app:test --tests "com.example.ainotes.SampleSanityTest"
- Run instrumented tests: .\gradlew.bat :app:connectedAndroidTest

That’s all that’s project‑specific right now. Update this document when bumping AGP/Kotlin/SDK versions or adding tooling (e.g., detekt, ktlint, Robolectric).