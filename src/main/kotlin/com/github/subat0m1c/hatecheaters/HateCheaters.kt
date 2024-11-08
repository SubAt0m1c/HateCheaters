package com.github.subat0m1c.hatecheaters

import com.github.subat0m1c.hatecheaters.commands.impl.*
import com.github.subat0m1c.hatecheaters.commands.registerCommands
import com.github.subat0m1c.hatecheaters.modules.BetterPartyFinder
import com.github.subat0m1c.hatecheaters.modules.HateCheatersModule
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer
import com.github.subat0m1c.hatecheaters.utils.LogHandler
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import com.github.subat0m1c.hatecheaters.utils.OdinCheck.checkIfOdinIsLoaded
import kotlinx.coroutines.*
import me.odinmain.OdinMain.mc
import me.odinmain.features.ModuleManager
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.coroutines.CoroutineContext

@Mod(modid = "hatecheaters", useMetadata = true)
class HateCheaters {

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        LogHandler

        checkIfOdinIsLoaded()

        ModuleManager.addModules(
            BetterPartyFinder, HateCheatersModule, ProfileViewer
        )

        registerCommands(DevCommand, ItemCommand, HCPVCommand, StatsCommand, HCCommand)

        logger.info("Hate Cheaters Loaded!")

        listOf(
           this
        ).forEach { MinecraftForge.EVENT_BUS.register(it) }
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent){
        if (screen == null) return
        logger.info("Displaying screen $screen")
        mc.displayGuiScreen(screen)
        screen = null
    }

    companion object {
        val version = "@MODVERSION@"

        var screen: GuiScreen? = null
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        fun scope(context: CoroutineContext = Dispatchers.IO, func: suspend CoroutineScope.() -> Unit) = scope.launch(context) { func(this) }
    }
}