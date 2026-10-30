/*
 * LibertyBans-fabric
 * Copyright © 2026 Anand Beh
 *
 * LibertyBans-fabric is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibertyBans-fabric is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with LibertyBans-fabric. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources", "target/maven/generated-resources")
        }
    }
}

dependencies {
    implementation("org.libertybans:bans-bootstrap:${project.version}")

    // Bundle adventure as nested jars, so that adventure-platform-mod is not required
    include("net.kyori:adventure-api:${project.findProperty("adventure_version")}") {
        exclude(group = "org.jetbrains")
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.auto.service")
    }
    include("net.kyori:adventure-key:${project.findProperty("adventure_version")}") {
        exclude(group = "org.jetbrains")
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.auto.service")
    }
    include("net.kyori:option:1.1.0") {
        exclude(group = "org.jetbrains")
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.auto.service")
    }
    include("net.kyori:adventure-text-serializer-json:${project.findProperty("adventure_version")}") {
        exclude(group = "org.jetbrains")
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.auto.service")
    }
    include("net.kyori:adventure-text-serializer-gson:${project.findProperty("adventure_version")}") {
        exclude(group = "org.jetbrains")
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.auto.service")
    }
    include("net.kyori:adventure-text-serializer-commons:${project.findProperty("adventure_version")}") {
        exclude(group = "org.jetbrains")
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.auto.service")
    }
    include("net.kyori:adventure-text-serializer-legacy:${project.findProperty("adventure_version")}") {
        exclude(group = "org.jetbrains")
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.auto.service")
    }
    include("net.kyori:adventure-text-serializer-plain:${project.findProperty("adventure_version")}") {
        exclude(group = "org.jetbrains")
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.auto.service")
    }
}
