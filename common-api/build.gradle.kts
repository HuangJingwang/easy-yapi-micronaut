group = "com.itangcent"
version = properties["plugin_version"]!!

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/central")
    mavenCentral()
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlin_version"]}")

    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlin_version"]}")

    implementation("com.itangcent:commons:${properties["itangcent_intellij_version"]}") {
        exclude("com.google.inject")
        exclude("com.google.code.gson")
    }

    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpmime
    compileOnly("org.apache.httpcomponents:httpmime:4.5.10")

    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
    compileOnly("org.apache.httpcomponents:httpclient:4.5.10")

    compileOnly("com.google.code.gson:gson:2.8.6")

    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpmime
    testImplementation("org.apache.httpcomponents:httpmime:4.5.10")

    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
    testImplementation("org.apache.httpcomponents:httpclient:4.5.10")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    testImplementation("com.google.code.gson:gson:2.8.6")

    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
    testImplementation("org.apache.httpcomponents:httpclient:4.5.10")

    // https://search.maven.org/artifact/org.mockito.kotlin/mockito-kotlin/3.2.0/jar
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")

    // https://mvnrepository.com/artifact/org.mockito/mockito-inline
    testImplementation("org.mockito:mockito-inline:5.2.0")

    testImplementation("org.junit.jupiter:junit-jupiter-params:${properties["junit_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${properties["junit_version"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${properties["junit_version"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${properties["kotlin_version"]}")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(maxOf(17, JavaVersion.current().majorVersion.toInt())))
    }
}
