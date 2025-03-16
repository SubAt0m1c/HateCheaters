package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.profileName
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.updateProfile
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.playClickSound
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.DropDownDSL
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.drawPlayerOnScreen
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.dropDownMenu
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.profileLazy
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorizeNumber
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.commas
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.assumedMagicalPower
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.petItem
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.profileList
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.profileOrSelected
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cappedSkillAverage
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.skillAverage
import com.mojang.authlib.GameProfile
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.Box
import me.odinmain.utils.render.Color
import me.odinmain.utils.render.getMCTextHeight
import me.odinmain.utils.render.roundedRectangle
import me.odinmain.utils.round
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import java.util.*
import kotlin.math.floor

object Overview: Pages.PVPage("Overview") {
    private val dropDown: DropDownDSL<String> by profileLazy {
        val profiles = player.profileOrSelected(profileName)
        val options = player.profileList.map { "§a${it.first}§7 §8(§7${it.second}§8)" }
        val longest = floor(options.maxByOrNull { it.mcWidth }?.mcWidth?.times(3.5) ?: 0.0).toInt()
        val default = "§a${profiles?.cuteName}§7 §8(§7${profiles?.gameMode}§8)"
        val dropDownBox = Box(mainCenterX - lineY - ((longest)/2), mainHeight * 0.1 + lineY, longest + lineY*2, floor(getMCTextHeight()*3.5 + lineY*2))
        dropDownMenu(dropDownBox, ot, default, options, 3.5, ct.button, ct.accent, ct.roundness, 1f,) {
            onSelect { selected ->
                updateProfile(selected.substringAfter("§a").substringBefore("§7 "))
                playClickSound()
            }

            onExtend {
                playClickSound()
            }
        }
    }

    private const val SKYCRYPT_TEXT: String = "Skycrypt cache only!"
    private val skycryptPosition = lineY + mainHeight - (getMCTextHeight()*2.5)/2

    private val data: List<String> by profileLazy {
        val mmComps = profile.dungeons.dungeonTypes.mastermode.tierComps.without("total").values.sum()
        val floorComps = profile.dungeons.dungeonTypes.catacombs.tierComps.without("total").values.sum()
        listOf(
            "Level§7: §a${floor(profile.leveling.experience/100.0).toInt().colorize(500)}",
            "§4Cata Level§7: §${ct.fontCode}${profile.dungeons.dungeonTypes.cataLevel.round(2).colorize(50)}",
            "§6Skill Average§7: §${ct.fontCode}${profile.playerData.cappedSkillAverage.round(2).colorize(55)} §7(${profile.playerData.skillAverage.round(2)})",
            "§bSecrets§7: ${profile.dungeons.secrets.commas.colorizeNumber(100000)} §7(${(profile.dungeons.secrets.toDouble()/(mmComps + floorComps)).round(2).colorize(15.0)}§7)",
            "Magical Power: ${profile.assumedMagicalPower.colorize(1709)}",
            "§${ct.fontCode}${profile.pets.pets.find { it.active }?.colorName ?: "None!"} ${profile.pets.pets.find { it.active }?.petItem?.let { "§7(§${ct.fontCode}${it}§7)" } ?: ""}",
        )
    }

    private val entryHeight: Double by profileLazy {( mainHeight * 0.9 - getMCTextHeight()*2.5 - lineY -floor(getMCTextHeight()*3.5 + lineY*2)) / data.size }

    var playerEntity: EntityLivingBase? = null

    private val playerX = mainX + mainWidth * 5/6

    private val textCenterY = ((mainHeight * 0.1) + lineY)/2

    override fun draw() {
        roundedRectangle(mainCenterX-((mainWidth*0.8)/2), mainHeight * 0.1, mainWidth*0.8, ot, ct.line)

        centeredText(player.name, mainCenterX, textCenterY, scale = 5, color = Color.WHITE)

        data.forEachIndexed { i, text ->
            val y = (mainHeight * 0.1 + lineY)+floor(getMCTextHeight()*3.5 + lineY*2) + entryHeight*i + entryHeight/2
            centeredText(text, mainX + mainWidth/3, y, 2.5, ct.font)
        }

        playerEntity?.let { drawPlayerOnScreen(playerX.toDouble(), lineY + mainHeight/2.0 + 175, 175, Mouse.getX() + 325, Mouse.getY() - 225, it) }

        if (player.skyCrypt) centeredText(SKYCRYPT_TEXT, mainCenterX, skycryptPosition, 2.5, Color.RED)
        dropDown.draw()
    }

    override fun mouseClick(x: Int, y: Int, button: Int) {
        dropDown.click(mouseX.toInt(), mouseY.toInt(), button)
    }

    fun setPlayer(player: PlayerInfo) = launch {
        val gameProfile = mc.sessionService.fillProfileProperties(GameProfile(getDashedUUID(player.uuid), player.name), true)

        var playerLocationCape: ResourceLocation? = null
        var playerLocationSkin: ResourceLocation? = null
        var playerSkinType: String? = null

        try {
            Minecraft.getMinecraft().skinManager.loadProfileTextures(
                gameProfile,
                { type, location1, profileTexture ->
                    when (type) {
                        MinecraftProfileTexture.Type.SKIN -> {
                            playerLocationSkin = location1
                            playerSkinType = profileTexture.getMetadata("model") ?: "default"
                        }
                        MinecraftProfileTexture.Type.CAPE -> {
                            playerLocationCape = location1
                        }
                        else -> return@loadProfileTextures
                    }
                },
                false
            )
        } catch (_: Exception) {}


        val playerE = object : EntityOtherPlayerMP(Minecraft.getMinecraft().theWorld, gameProfile) {
            override fun getLocationSkin(): ResourceLocation {
                return playerLocationSkin?: DefaultPlayerSkin.getDefaultSkin(uniqueID)
            }
            override fun getLocationCape(): ResourceLocation {
                return playerLocationCape ?: super.getLocationCape()
            }

            override fun getSkinType(): String {
                return playerSkinType ?: DefaultPlayerSkin.getSkinType(uniqueID)
            }

            override fun getAlwaysRenderNameTagForRender(): Boolean = false
        }

        playerEntity = playerE
    }

    private fun getDashedUUID(uuidStr: String): UUID {
        val formattedUUID = uuidStr.substring(0, 8) + "-" +
                uuidStr.substring(8, 12) + "-" +
                uuidStr.substring(12, 16) + "-" +
                uuidStr.substring(16, 20) + "-" +
                uuidStr.substring(20, 32);

        return UUID.fromString(formattedUUID)
    }
}