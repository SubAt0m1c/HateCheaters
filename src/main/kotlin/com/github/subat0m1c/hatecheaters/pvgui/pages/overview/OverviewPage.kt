package com.github.subat0m1c.hatecheaters.pvgui.pages.overview

import com.github.subat0m1c.hatecheaters.pvgui.PVGui.accent
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.c
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.line
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.main
import com.github.subat0m1c.hatecheaters.pvgui.PVGuiPage
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.drawPlayerOnScreen
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.getDashedUUID
import com.github.subat0m1c.hatecheaters.pvgui.ScreenObjects
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.cappedSkillAverage
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.magicalPower
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.petItem
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.skillAverage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorizeNumber
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.commas
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import com.mojang.authlib.GameProfile
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import kotlinx.coroutines.launch
import me.odinmain.OdinMain.mc
import me.odinmain.OdinMain.scope
import me.odinmain.utils.floor
import me.odinmain.utils.render.*
import me.odinmain.utils.round
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import kotlin.math.floor


object OverviewPage: PVGuiPage() {

    private var entityPlayer: EntityLivingBase? = null

    override fun draw(screen: ScreenObjects, player: PlayerInfo) {
        val lineY = floor(screen.mainHeight*0.1)
        val usableY = lineY + screen.outlineThickness + screen.mainY
        val usableHeight = screen.mainHeight - usableY + screen.mainY
        val lineLength = screen.mainWidth * 0.8
        val screenCenterX = screen.mainCenterX
        val fontCenter = screen.lineY + (lineY)/2
        val fontScale = 6f * screen.scale
        val nameWidth = getMCTextWidth(player.name) * fontScale
        mcText(player.name, screenCenterX - nameWidth/2, fontCenter - (getMCTextHeight()*fontScale)/2, fontScale, font, true, false)
        roundedRectangle(screenCenterX - lineLength/2, lineY, lineLength, screen.outlineThickness, line)
        player.profileData.profiles.find { it.selected }?.let {
            mcText("Profile§7: §a${it.cuteName}", screenCenterX, lineY+screen.lineY, (3.5f * screen.scale), font)
        }

        val playerBackWidth = screen.mainWidth/5
        val playerBackHeight = (screen.mainWidth/5)*2
        val playerBackCenterX = screen.mainX + (playerBackWidth)/2
        val playerBackCenterY = usableY + (usableHeight/2)
        roundedRectangle((playerBackCenterX - playerBackWidth/2)-screen.outlineThickness,  (playerBackCenterY - playerBackHeight/2)-screen.outlineThickness, playerBackWidth+screen.outlineThickness*2, playerBackHeight+screen.outlineThickness*2, accent, radius = 10f, edgeSoftness = 1f)
        roundedRectangle(floor(playerBackCenterX - playerBackWidth/2),  playerBackCenterY - playerBackHeight/2, playerBackWidth, playerBackHeight, main, radius = 10f, edgeSoftness = 1f)

        val usableCenterY = usableY + usableHeight/2

        val textScale = 3f * screen.scale
        player.profileData.profiles.find { it.selected }?.members?.get(player.uuid)?.let {
            val mmComps = (it.dungeons.dungeonTypes.mastermode.tierComps.toMutableMap().apply { this.remove("total") }).values.sum()
            val floorComps = (it.dungeons.dungeonTypes.catacombs.tierComps.toMutableMap().apply { this.remove("total") }).values.sum()
            val textList = listOf(
                "Level§7: §a${(it.leveling.experience/100.0).floor().toInt().colorize(500)}",
                "§4Cata Level§7: §$c${it.dungeons.dungeonTypes.cataLevel.round(2).colorize(50)}",
                "§bSecrets§7: ${it.dungeons.secrets.commas.colorizeNumber(100000)} §7(${(it.dungeons.secrets.toDouble()/(mmComps + floorComps)).round(2).colorize(15.0)}§7)",
                "§6Skill Average§7: §$c${it.playerData.cappedSkillAverage.round(2).colorize(55)} §7(${it.playerData.skillAverage.round(2)})",
                "§$c${it.pets.pets.find { it.active }?.colorName ?: "None!"} ${it.pets.pets.find { it.active }?.petItem?.let { "§7(§${c}${it}§7)" } ?: ""}",
                "Magical Power: ${it.magicalPower.colorize(1697)}",
            )

            val entryHeight = (screen.mainHeight-usableY + screen.lineY)/textList.size
            textList.forEachIndexed { i, text ->
                mcText(text, screen.mainCenterX, (usableY + (entryHeight * i) + entryHeight/2) - ((getMCTextHeight()*textScale)/2), textScale, font)
            }
        }

        val skycryptText = "§cCached SkyCrypt Data only!"
        if (player.skyCrypt) mcText(skycryptText, screenCenterX - (skycryptText.mcWidth*3f)/2, usableY + usableHeight - (getMCTextHeight()*textScale) - screen.lineY, 3f, font, center = false)

        entityPlayer?.let {
            drawPlayerOnScreen(
                playerBackCenterX,
                ((playerBackCenterY + playerBackHeight/2) - screen.lineY), (200 * screen.scale).toInt(),  Mouse.getX(),  Mouse.getY(), it, screen)
        }
    }

    /**
     * Taken and modified from [NotEnoughUpdates](https://github.com/NotEnoughUpdates/NotEnoughUpdates) under [GPL-3](https://www.gnu.org/licenses/gpl-3.0.en.html)
     */
    fun getPlayer(player: PlayerInfo) {
        scope.launch {
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
                    return playerLocationSkin?: DefaultPlayerSkin.getDefaultSkin(this.uniqueID)
                }
                override fun getLocationCape(): ResourceLocation {
                    return playerLocationCape ?: super.getLocationCape()
                }

                override fun getSkinType(): String {
                    return playerSkinType ?: DefaultPlayerSkin.getSkinType(this.uniqueID)
                }
            }

            playerE.alwaysRenderNameTag = false
            playerE.customNameTag = ""

            entityPlayer = playerE
        }
    }
}