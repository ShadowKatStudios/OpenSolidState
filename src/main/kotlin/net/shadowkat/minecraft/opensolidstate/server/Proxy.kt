package net.shadowkat.minecraft.opensolidstate.server

import net.minecraft.util.text.translation.I18n

class Proxy : net.shadowkat.minecraft.opensolidstate.common.Proxy() {
    override fun localize(key: String, vararg v: Any) {
        I18n.translateToLocalFormatted(key, v)
    }

}