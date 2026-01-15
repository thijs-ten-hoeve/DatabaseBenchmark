plugins {
    id("java")
}

group = "nhlstenden.thijs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.6.2"))
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("com.mysql:mysql-connector-j:9.5.0")
    implementation("org.postgresql:postgresql:42.7.8")
    implementation("ch.qos.logback:logback-classic:1.5.24")
}

tasks.test {
    useJUnitPlatform()
}