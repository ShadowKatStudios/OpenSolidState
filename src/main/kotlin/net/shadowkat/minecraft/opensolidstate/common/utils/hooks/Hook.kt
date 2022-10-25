package net.shadowkat.minecraft.opensolidstate.common.utils.hooks

import java.util.concurrent.Callable
import kotlin.reflect.KFunction

class Hook<T>() {
    private val hooks : MutableList<KFunction<T?>> = mutableListOf()

    fun add(hook : KFunction<T?>) {
        hooks.add(hook)
    }

    fun call(vararg : Any) : T? {
        for (hook in hooks) {
            val rv = hook.call(vararg)
            if (rv != null) {
                return rv
            }
        }
        return null
    }

    fun run(vararg : Any) {
        for (hook in hooks) {
            hook.call(vararg)
        }
    }
}