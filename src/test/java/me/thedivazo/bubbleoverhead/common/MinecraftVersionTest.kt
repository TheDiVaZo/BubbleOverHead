package me.thedivazo.bubbleoverhead.common

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.platform.commons.annotation.Testable

class MinecraftVersionTest {
    private fun mv(major: Int, minor: Int, patch: Int): MinecraftVersion {
        val ctor = MinecraftVersion::class.java.getDeclaredConstructor(
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )
        ctor.isAccessible = true
        return ctor.newInstance(major, minor, patch)
    }

    private fun fields(v: MinecraftVersion): Triple<Int, Int, Int> {
        val c = v.javaClass
        val fMajor = c.getDeclaredField("major").apply { isAccessible = true }
        val fMinor = c.getDeclaredField("minor").apply { isAccessible = true }
        val fPatch = c.getDeclaredField("patch").apply { isAccessible = true }
        return Triple(
            fMajor.getInt(v),
            fMinor.getInt(v),
            fPatch.getInt(v)
        )
    }

    private fun assertFields(v: MinecraftVersion, major: Int, minor: Int, patch: Int) {
        val (a, b, c) = fields(v)
        assertEquals(major, a, "major mismatch")
        assertEquals(minor, b, "minor mismatch")
        assertEquals(patch, c, "patch mismatch")
    }

    // -------- compareTo --------

    @Test
    fun `compareTo - major diff decides`() {
        assertTrue(mv(2, 0, 0) > mv(1, 99, 99))
        assertTrue(mv(1, 0, 0) < mv(2, 0, 0))
    }

    @Test
    fun `compareTo - minor diff decides when major equal`() {
        assertTrue(mv(1, 21, 0) > mv(1, 20, 99))
        assertTrue(mv(1, 19, 9) < mv(1, 20, 0))
    }

    @Test
    fun `compareTo - patch decides when major and minor equal and both have patch`() {
        assertTrue(MinecraftVersion.of("1.20.2") > MinecraftVersion.of("1.20.1"))
        assertTrue(MinecraftVersion.of("1.20.0") < MinecraftVersion.of("1.20.1"))
        assertEquals(0, MinecraftVersion.of("1.20.4").compareTo(MinecraftVersion.of("1.20.4")))
    }

    @Test
    fun `compareTo - patch -1 makes versions equal by patch`() {
        val wildcard = mv(1, 20, -1)
        val v1 = MinecraftVersion.of("1.20.1")
        val v999 = MinecraftVersion.of("1.20.999")

        assertEquals(0, wildcard.compareTo(v1))
        assertEquals(0, v1.compareTo(wildcard))

        assertEquals(0, wildcard.compareTo(v999))
        assertEquals(0, v999.compareTo(wildcard))
    }

    @Test
    fun `compareTo - patch -1 does not override major or minor comparison`() {
        val wildcard120 = mv(1, 20, -1)
        val v1210 = MinecraftVersion.of("1.21.0")
        val v0200 = mv(0, 20, -1)

        assertTrue(wildcard120 < v1210) // minor решает раньше patch-логики
        assertTrue(v0200 < wildcard120) // major решает раньше patch-логики
    }

    // -------- of --------

    @Test
    fun `of - parses plain patch`() {
        val v = MinecraftVersion.of("1.20.4")
        assertFields(v, 1, 20, 4)
    }

    @Test
    fun `of - parses Paper-like version string`() {
        val v = MinecraftVersion.of("1.21.1-R0.1-SNAPSHOT")
        assertFields(v, 1, 21, 1)
    }

    @Test
    fun `of - parses version without patch as patch=-1`() {
        val v = MinecraftVersion.of("1.20")
        assertFields(v, 1, 20, -1)
    }

    @Test
    fun `of - rejects invalid strings`() {
        assertThrows<IllegalArgumentException> { MinecraftVersion.of("") }
        assertThrows<IllegalArgumentException> { MinecraftVersion.of("abc") }
        assertThrows<IllegalArgumentException> { MinecraftVersion.of("1") }
        assertThrows<IllegalArgumentException> { MinecraftVersion.of("1.") }
    }

}