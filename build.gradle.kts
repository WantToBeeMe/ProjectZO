plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "me.wanttobee"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

val lwjglVersion = "3.3.2"
val jomlVersion = "1.10.5"
val lwjglNativesWindows = "natives-windows"
//val lwjglNativesWindowsX = "natives-windows-x86"
//val lwjglNativesLinux = "natives-linux"

val imguiNativesWindows = "natives-windows"
//val imguiNativesWindowsX = "natives-windows-x86"
//val imguiNativesLinux = "natives-linux"
//val imguiNativesLinuxX = "natives-linux-x86"

dependencies {
    //imGUI
    implementation("io.imgui.java:binding:1.75-0.7.2")
    implementation("io.imgui.java:lwjgl3:1.75-0.7.2")
    runtimeOnly("io.imgui.java:$imguiNativesWindows:1.75-0.7.2")
    //runtimeOnly("io.imgui.java:$imguiNativesWindowsX:1.75-0.7.2")
    //runtimeOnly("io.imgui.java:$imguiNativesLinux:1.75-0.7.2")
    //runtimeOnly("io.imgui.java:$imguiNativesLinuxX:1.75-0.7.2")



    //LWJGL
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    //implementation("org.lwjgl", "lwjgl-assimp")
    //implementation("org.lwjgl", "lwjgl-nfd")
    //implementation("org.lwjgl", "lwjgl-openal")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNativesWindows)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNativesWindows)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNativesWindows)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNativesWindows)
    //runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNativesWindows)
    //runtimeOnly("org.lwjgl", "lwjgl-nfd", classifier = lwjglNativesWindows)
    //runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNativesWindows)
    implementation("org.joml", "joml", jomlVersion)
}

