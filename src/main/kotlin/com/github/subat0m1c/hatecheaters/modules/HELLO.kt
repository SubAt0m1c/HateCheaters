package com.github.subat0m1c.hatecheaters.modules

import me.odinmain.events.impl.ClickEvent
import me.odinmain.events.impl.GuiEvent
import me.odinmain.events.impl.TerminalSolvedEvent
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.impl.floor7.p3.TerminalSolver
import me.odinmain.features.impl.floor7.p3.TerminalSolver.currentTerm
import me.odinmain.features.impl.floor7.p3.TerminalTypes
import me.odinmain.utils.equalsOneOf
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object HELLO : Module(
    name = "Block Wrong Clicks",
    description = "Blocks wrong clicks in terminals (except melody)",
    category = Category.FLOOR7
) {
    @SubscribeEvent
    fun onSlotClick(event: GuiEvent.GuiMouseClickEvent) {
        if (currentTerm == TerminalTypes.NONE) return
        val slot = (event.gui as? GuiChest)?.slotUnderMouse?.slotIndex ?: return
        clickSlot(slot, event)
    }

    @SubscribeEvent
    fun onCustomSlotClick(event: GuiEvent.CustomTermGuiClick) {
        if (currentTerm == TerminalTypes.NONE) return
        clickSlot(event.slot, event)
    }

    private fun clickSlot(slot: Int, event: GuiEvent) {
        if (
            (!currentTerm.equalsOneOf(TerminalTypes.MELODY, TerminalTypes.ORDER) && slot in TerminalSolver.solution) ||
            (currentTerm == TerminalTypes.ORDER && slot == TerminalSolver.solution.first()) ||
            (currentTerm == TerminalTypes.MELODY)
        ) return
        event.isCanceled = true
    }
}