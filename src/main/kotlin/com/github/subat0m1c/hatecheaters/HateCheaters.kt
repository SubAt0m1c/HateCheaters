package com.github.subat0m1c.hatecheaters

import com.github.subat0m1c.hatecheaters.commands.CommandRegistry
import com.github.subat0m1c.hatecheaters.commands.impl.PVCommand
import com.github.subat0m1c.hatecheaters.modules.dungeons.BetterPartyFinder
import com.github.subat0m1c.hatecheaters.modules.dungeons.ClearSecrets
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.pvCommand
import com.github.subat0m1c.hatecheaters.modules.skyblock.HateCheatersModule
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.ItemGridDSL
import com.github.subat0m1c.hatecheaters.utils.ExtraStatsHandler
import com.github.subat0m1c.hatecheaters.utils.LogHandler
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.OdinCheck.checkIfOdinIsLoaded
import com.github.subat0m1c.hatecheaters.utils.toasts.ToastRenderer
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import me.odinmain.OdinMain.mc
import me.odinmain.features.ModuleManager
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.coroutines.CoroutineContext

@Mod(modid = "hatecheaters", useMetadata = true)
class HateCheaters {

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        LogHandler
        checkIfOdinIsLoaded()

        listOf(
            this, ItemGridDSL, ExtraStatsHandler, ToastRenderer
        ).forEach { MinecraftForge.EVENT_BUS.register(it) }
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent){
        if (screen == null) return
        Logger.info("Displaying screen $screen")
        mc.displayGuiScreen(screen)
        screen = null
    }

    @Mod.EventHandler
    fun load(event: FMLLoadCompleteEvent) {
        ModuleManager.addModules(
            BetterPartyFinder, HateCheatersModule, ProfileViewer, ClearSecrets
        )
    }

    companion object {
        val version = "@MODVERSION@"

        val json: Json = Json { ignoreUnknownKeys = true }

        var screen: GuiScreen? = null
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        fun onOdinLoad() {
            if (pvCommand) CommandRegistry.add(PVCommand)
            CommandRegistry.register()
        }

        fun launch(context: CoroutineContext = Dispatchers.IO, func: suspend CoroutineScope.() -> Unit) = scope.launch(context) { func(this) }

        fun <R> launchDeferred(fn: suspend () -> R): Deferred<R> {
            val job = CompletableDeferred<R>()
            launch {
                job.complete(fn())
            }
            return job
        }
    }
}