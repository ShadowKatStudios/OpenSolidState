package net.shadowkat.minecraft.opensolidstate.common.utils.classloader

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

abstract class OSSMClass<T : Any> : KClass<T> {
    fun getFunction(name : String) : KFunction<*> {
        for (m in this.functions) {
            if (m.name == name) {
                return m
            }
        }
        throw NoSuchMethodException("no such method $name")
    }

    fun <T> getValue(name : String) : T? {
        val memb = this.memberProperties.firstOrNull {it.name == name}
        return memb?.getter?.invoke(this.objectInstance!!) as T?
    }
}