package net.shadowkat.minecraft.opensolidstate.common.utils.classloader

import java.io.File

class OSSMClassLoader<T : Any>(private val path: String, private val blacklist: Array<String>?, private val init: (cls : OSSMClass<T>, path : String) -> Unit) {
    fun load() {
        var rpath = path.replace('.', '/')
        if (rpath[0] != '/')
            rpath = "/$rpath"
        val url = this::class.java.getResource(rpath)
        val dir = File(url.file)
        dir.walk()
            .filter {f -> f.isFile() && !f.name.contains('$') && f.name.endsWith(".class") }
            .forEach {
                val cpath = path + it.canonicalPath.removePrefix(dir.canonicalPath).dropLast(6).replace('/', '.')
                val c = Class.forName(cpath)
                init(c.kotlin as OSSMClass<T>, cpath)
            }
    }
}