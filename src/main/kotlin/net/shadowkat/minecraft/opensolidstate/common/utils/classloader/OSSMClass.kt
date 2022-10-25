package net.shadowkat.minecraft.opensolidstate.common.utils.classloader

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

/*abstract class OSSMClass<T : Any> : KClass<T> {


}*/

fun <T : Any> KClass<T>.getFunction2(name : String) : KFunction<*> {
    for (m in this.functions) {
        if (m.name == name) {
            return m
        }
    }
    throw NoSuchMethodException("no such method $name")
}

fun <T : Any, V : Any> KClass<T>.getValue2(name : String) : V? {
    val memb = this.memberProperties.firstOrNull {it.name == name}
    return memb?.getter?.invoke(this.objectInstance!!) as V?
}

fun <T : Any> KClass<T>.hasFunction(name : String) : Boolean {
    for (m in this.functions) {
        if (m.name == name) {
            return true
        }
    }
    return false
}
