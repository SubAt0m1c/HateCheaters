package com.github.subat0m1c.hatecheaters

import com.github.subat0m1c.hatecheaters.modules.AutoKick
import kotlinx.coroutines.*
import com.github.subat0m1c.hatecheaters.modules.HELLO
import com.github.subat0m1c.hatecheaters.utils.OdinCheck.checkIfOdinIsLoaded
import me.odinmain.features.ModuleManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import kotlin.coroutines.EmptyCoroutineContext

@Mod(modid = "hatecheaters", useMetadata = true)
class HateCheaters {

    val scope = CoroutineScope(EmptyCoroutineContext)

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        checkIfOdinIsLoaded()

        ModuleManager.addModules(
            HELLO, AutoKick
        )
    }
}