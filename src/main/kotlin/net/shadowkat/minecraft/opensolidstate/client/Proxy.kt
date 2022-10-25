package net.shadowkat.minecraft.opensolidstate.client

import net.minecraft.client.resources.I18n

class Proxy : net.shadowkat.minecraft.opensolidstate.common.Proxy() {
    override fun localize(key: String, vararg v: Any) {
        I18n.format(key, v)
    }
}