package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.profileName
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.updateProfile
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVPage
import com.github.subat0m1c.hatecheaters.pvgui.v2.PageHandler.playClickSound
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.DropDownDSL
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.TextBox
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.drawPlayerOnScreen
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.dropDownMenu
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.resettableLazy
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorizeNumber
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.commas
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.maxMagicalPower
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.petItem
import com.github.subat0m1c.hatecheaters.utils.LogHandler
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cappedSkillAverage
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.skillAverage
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Box
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Text
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.hc
import com.mojang.authlib.GameProfile
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import me.odinmain.OdinMain.mc
import me.odinmain.utils.round
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import java.util.*
import kotlin.math.floor

object Overview : PVPage("Overview") {

    private val nameBox = Box(mainX, spacer, (mainWidth * 2 / 3) - spacer / 2, mainHeight * 0.1)
    private val dropDownBox =
        Box(mainX + spacer + (mainWidth * 2 / 3) - spacer / 2, spacer, (mainWidth / 3) - spacer / 2, mainHeight * 0.1)

    private val dropDown: DropDownDSL<String> by resettableLazy {
        val profiles = player.profileOrSelected(profileName)
        val options = player.profileList.map { "§a${it.first}§r §8(§7${it.second}§8)" }
        val default = "§a${profiles?.cuteName}§7 §8(§7${profiles?.gameMode}§8)"

        dropDownMenu(dropDownBox, default, options, spacer, ct.button.hc(), ct.roundness) {
            onSelect { selected ->
                updateProfile(selected.substringAfter("§a").substringBefore("§r "))
                playClickSound()
            }

            onExtend {
                playClickSound()
            }
        }
    }

    private val data: List<String> by resettableLazy {
        val mmComps = profile.dungeons.dungeonTypes.mastermode.tierComps.without("total").values.sum()
        val floorComps = profile.dungeons.dungeonTypes.catacombs.tierComps.without("total").values.sum()
        listOf(
            "Level§7: §a${floor(profile.leveling.experience / 100.0).toInt().colorize(500)}",
            "§4Cata Level§7: §${ct.fontCode}${profile.dungeons.dungeonTypes.cataLevel.round(2).colorize(50)}",
            "§6Skill Average§7: §${ct.fontCode}${
                profile.playerData.cappedSkillAverage.round(2).colorize(55)
            } §7(${profile.playerData.skillAverage.round(2)})",
            "§bSecrets§7: ${profile.dungeons.secrets.commas.colorizeNumber(100000)} §7(${
                (profile.dungeons.secrets.toDouble() / (mmComps + floorComps)).round(
                    2
                ).colorize(15.0)
            }§7)",
            "Magical Power: ${profile.assumedMagicalPower.colorize(maxMagicalPower)}",
            "§${ct.fontCode}${profile.pets.activePet?.colorName ?: "None!"} ${profile.pets.pets.find { it.active }?.petItem?.let { "§7(§${ct.fontCode}${it}§7)" } ?: ""}",
        )
    }

    private val dataBox =
        Box(mainX, mainHeight * 0.1 + 2 * spacer, (mainWidth * 2 / 3) - spacer / 2, mainHeight - nameBox.h - spacer)
    private val textBox by resettableLazy {
        TextBox(
            Box(dataBox.x + spacer, dataBox.y + spacer, dataBox.w - 2 * spacer, dataBox.h - 2 * spacer),
            null, 0f, data, 2.5f, spacer.toFloat(), ct.font.hc()
        )
    }

    private val playerBox = Box(
        mainX + spacer + dataBox.w,
        2 * spacer + dropDownBox.h,
        dropDownBox.w,
        dataBox.h
    )

    private var playerEntity: EntityLivingBase? = null

    override fun draw(mouseX: Int, mouseY: Int) {
        Shaders.rect(nameBox, ct.roundness, ct.items.hc())
        Text.fillText(
            "§${ct.fontCode}${player.name}",
            nameBox.x + nameBox.w / 2,
            nameBox.y + nameBox.h / 2,
            nameBox.w - 2 * spacer,
            nameBox.h - 2 * spacer,
            ct.font.hc(),
            alignment = Text.Alignment.MIDDLE
        )

        Shaders.rect(dataBox, ct.roundness, ct.items.hc())
        textBox.draw()

        dropDown.draw(mouseX, mouseY)

        if (!dropDown.extended) {
            Shaders.rect(playerBox, ct.roundness, ct.items.hc())
            playerEntity?.let {
                drawPlayerOnScreen( // i have no idea what these values are but they work
                    playerBox.centerX.toDouble(),
                    playerBox.centerY + 200.0,
                    200,
                    Mouse.getX() + 225,
                    Mouse.getY() - 225,
                    it
                )
            }
        }
    }

    override fun mouseClick(x: Int, y: Int, button: Int) {
        dropDown.click(x, y, button)
    }

    fun setPlayer(player: PlayerInfo) = launch {
        mc.theWorld.playerEntities.forEach { it ->
            if (it.name == player.name) {
                playerEntity = it
                return@launch
            }
        }

        val gameProfile =
            mc.sessionService.fillProfileProperties(GameProfile(getDashedUUID(player.uuid), player.name), true)

        var playerLocationCape: ResourceLocation? = null
        var playerLocationSkin: ResourceLocation? = null
        var playerSkinType: String? = null

        runCatching {
            mc.skinManager.loadProfileTextures(
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
        }.onFailure { LogHandler.Logger.warning("Failed to load skin data for ${player.name}: $it") }

        playerEntity = object : EntityOtherPlayerMP(mc.theWorld, gameProfile) {
            override fun getLocationSkin(): ResourceLocation =
                playerLocationSkin ?: DefaultPlayerSkin.getDefaultSkin(uniqueID)

            override fun getLocationCape(): ResourceLocation = playerLocationCape ?: super.getLocationCape()
            override fun getSkinType(): String = playerSkinType ?: DefaultPlayerSkin.getSkinType(uniqueID)

            override fun getAlwaysRenderNameTagForRender(): Boolean = false
        }
    }

    private fun getDashedUUID(uuidStr: String): UUID {
        val formattedUUID = uuidStr.substring(0, 8) + "-" +
                uuidStr.substring(8, 12) + "-" +
                uuidStr.substring(12, 16) + "-" +
                uuidStr.substring(16, 20) + "-" +
                uuidStr.substring(20, 32)

        return UUID.fromString(formattedUUID)
    }
}