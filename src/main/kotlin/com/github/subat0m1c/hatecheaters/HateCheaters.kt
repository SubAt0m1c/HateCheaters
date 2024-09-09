package com.github.subat0m1c.hatecheaters

import com.github.subat0m1c.hatecheaters.utils.OdinCheck
import com.github.subat0m1c.hatecheaters.modules.HELLO
import me.odinmain.features.ModuleManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(modid = "hatecheaters", useMetadata = true)
class HateCheaters {
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        OdinCheck

        ModuleManager.addModules(
            HELLO
        )
    }
}