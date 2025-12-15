package me.thedivazo.bubbleoverhead.common

import java.io.Serializable
import java.util.regex.Pattern

class MinecraftVersion private constructor(private val major: Int, private val minor: Int, private val patch: Int) :
    Serializable,
    Comparable<MinecraftVersion> {
    override operator fun compareTo(o: MinecraftVersion): Int {
        if (major != o.major) return major - o.major
        if (minor != o.minor) return minor - o.minor
        if (patch == -1 || o.patch == -1) return 0
        if (patch != o.patch) return patch - o.patch
        return 0
    }

    companion object {
        @JvmStatic
        private val VERSION_PATTERN: Pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(?:\\D+([0-9]+))?")

        @Throws(IllegalArgumentException::class)
        @JvmStatic
        fun of(version: String): MinecraftVersion {
            val matcher = VERSION_PATTERN.matcher(version)

            require(matcher.matches()) { "Invalid version: $version" }
            val major = matcher.group(1).toInt()
            val minor = matcher.group(2).toInt()
            var patch = -1
            if (matcher.groupCount() > 2) patch = matcher.group(3).toInt()

            return MinecraftVersion(major, minor, patch)
        }
    }
}
