package net.shadowkat.minecraft.opensolidstate.common.utils

import net.minecraft.item.EnumRarity

object Utils {
    private val bytes = arrayOf("bytes", "KiB", "MiB", "GiB", "TiB")
    private val bytesSI = arrayOf("bytes", "kB", "MB", "GB", "TB")

    fun toHuman(num : Double, divisor: Double, postfix : Array<String>, decPlaces : Int = 2) : String {
        var res = num
        val last = postfix.last()
        for (post in postfix) {
            if (res < divisor || post == last)
                return String.format("%.${decPlaces}f %s", res, post)
            res /= divisor
        }
        throw InternalError("how did we get here?")
    }

    fun toBytes(num : Double, decPlaces : Int = 2) : String {
        return toHuman(num, 1024.0, bytes, decPlaces)
    }

    fun toBytesSI(num : Double, decPlaces : Int = 2) : String {
        return toHuman(num, 1000.0, bytesSI, decPlaces)
    }

    private val tiers = arrayOf(EnumRarity.COMMON, EnumRarity.UNCOMMON, EnumRarity.RARE, EnumRarity.EPIC)

    fun getRarity(tier : Int) : EnumRarity {
        return tiers[Math.min(tier, 3)]
    }
}