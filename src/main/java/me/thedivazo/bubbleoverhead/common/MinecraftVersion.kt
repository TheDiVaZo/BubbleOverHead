package me.thedivazo.bubbleoverhead.common

import java.io.Serializable
import java.util.regex.Matcher
import java.util.regex.Pattern

class MinecraftVersion private constructor(private val major: Int, private val minor: Int, private val patch: Int) :
    Serializable,
    Comparable<MinecraftVersion> {
    override operator fun compareTo(other: MinecraftVersion): Int {
        if (major != other.major) return major - other.major
        if (minor != other.minor) return minor - other.minor
        if (patch == -1 || other.patch == -1) return 0
        if (patch != other.patch) return patch - other.patch
        return 0
    }

    companion object {
        @JvmStatic
        private val VERSION_PATTERN: Pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(?:\\D+([0-9]+))?")

        @Throws(IllegalArgumentException::class)
        @JvmStatic
        fun of(version: String): MinecraftVersion {
            val matcher = VERSION_PATTERN.matcher(version)

            require(matcher.find()) { "Invalid version: $version" }
            val major = matcher.group(1).toInt()
            val minor = matcher.group(2).toInt()
            val patch = matcher.group(3)?.toInt() ?: -1

            return MinecraftVersion(major, minor, patch)
        }
    }
}
