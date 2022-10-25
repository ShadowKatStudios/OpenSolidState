package net.shadowkat.minecraft.opensolidstate.common.utils.classloader

import java.io.File
import java.net.URI
import java.util.zip.ZipFile
import kotlin.reflect.KClass

class OSSMClassLoader<T : Any>(private val cls : KClass<*>, private val path: String, private val blacklist: Array<String>?, private val init: (cls : KClass<T>, path : String) -> Unit) {
    fun load() {
        var rpath = path.replace('.', '/')
        // fuck it, we ball
        val loc = cls.java.protectionDomain.codeSource.location.path
        val jloc = loc.dropLast(loc.length-loc.indexOfFirst { it == '!' })
        print(jloc)
        val zip = ZipFile(File(URI(jloc)))
        zip.entries().asSequence()
            .filter {
                it.name.startsWith(rpath) && !it.isDirectory && !it.name.contains('$') && it.name.endsWith(".class")
            }
            .filter {
                //println(it.name.dropLast(6).replace('/', '.').removePrefix("$path."))
                blacklist?.contains(it.name.dropLast(6).replace('/', '.').removePrefix("$path.")) != true
            }
            .forEach {
                println(it.name)
                val cpath = it.name.dropLast(6).replace('/', '.')
                val c = Class.forName(cpath)
                init(c.kotlin as KClass<T>, cpath)
            }
        zip.close()
    }
}