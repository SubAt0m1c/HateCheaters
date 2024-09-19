package com.github.subat0m1c.hatecheaters

import com.github.subat0m1c.hatecheaters.commands.impl.DevCommand
import com.github.subat0m1c.hatecheaters.commands.impl.ItemCommand
import com.github.subat0m1c.hatecheaters.commands.registerCommands
import com.github.subat0m1c.hatecheaters.modules.BetterPartyFinder
import kotlinx.coroutines.*
import com.github.subat0m1c.hatecheaters.modules.HateCheaters
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import com.github.subat0m1c.hatecheaters.utils.OdinCheck.checkIfOdinIsLoaded
import me.odinmain.features.ModuleManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

object HateCheatersObject {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
}

@Mod(modid = "hatecheaters", useMetadata = true)
class HateCheaters {

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        checkIfOdinIsLoaded()

        ModuleManager.addModules(
            BetterPartyFinder, HateCheaters
        )

        registerCommands(DevCommand, ItemCommand)

        logger.info("Hate Cheaters Loaded!")

        //listOf(
        //    ChatUtils
        //.forEach { MinecraftForge.EVENT_BUS.register(it) }
    }
}