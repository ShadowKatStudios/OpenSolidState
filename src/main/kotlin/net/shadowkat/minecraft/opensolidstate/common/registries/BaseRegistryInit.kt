package net.shadowkat.minecraft.opensolidstate.common.registries

import java.io.File
import java.util.*
import kotlin.reflect.KClass


abstract class BaseRegistryInit(pkgpath : String) {
    val pkgpath : String = pkgpath

    @Throws(ClassNotFoundException::class)
    private fun findClasses(directory: File, packageName: String): ArrayList<KClass<*>> {
        val classes : ArrayList<KClass<*>> = ArrayList<KClass<*>>()
        if (!directory.exists()) {
            return classes
        }
        val files = directory.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                assert(!file.name.contains("."))
                classes.addAll(findClasses(file, packageName + "." + file.name))
            } else if (file.name.endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.name.substring(0, file.name.length - 6))::kotlin.get())
            }
        }
        return classes
    }

    fun init() {
        preInit()
        // Classloader here.
        val cl = Thread.currentThread().contextClassLoader
        val res = cl.getResources(pkgpath.replace('.', '/'))
        val dirs = ArrayList<File>()

        while (res.hasMoreElements()) {
            val url = res.nextElement()
            dirs.add(File(url.file))
        }
        for (dir in dirs) {
            for (cls in findClasses(dir, pkgpath))
                load(cls)
        }
    }
    abstract fun load(cls : KClass<*>)
    abstract fun preInit()
}