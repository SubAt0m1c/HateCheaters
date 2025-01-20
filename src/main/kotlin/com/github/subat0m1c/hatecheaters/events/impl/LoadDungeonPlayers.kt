package com.github.subat0m1c.hatecheaters.events.impl

import me.odinmain.utils.skyblock.dungeon.DungeonPlayer
import net.minecraftforge.fml.common.eventhandler.Event

class LoadDungeonPlayers(val teammates: ArrayList<DungeonPlayer>) : Event() {}