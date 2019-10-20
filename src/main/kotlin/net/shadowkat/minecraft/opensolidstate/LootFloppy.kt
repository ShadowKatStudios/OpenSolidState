package net.shadowkat.minecraft.opensolidstate

import li.cil.oc.api.fs.FileSystem
import java.util.concurrent.Callable

class LootFloppy(n: String) : Callable<FileSystem> {
	val name : String = n
	override fun call(): FileSystem? {
		return li.cil.oc.api.FileSystem.asReadOnly(li.cil.oc.api.FileSystem.fromClass(OpenSolidState::class.java, "ossm", "loot/$name"))
	}
}