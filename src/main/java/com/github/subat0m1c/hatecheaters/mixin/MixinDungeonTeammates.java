package com.github.subat0m1c.hatecheaters.mixin;

import com.github.subat0m1c.hatecheaters.events.impl.LoadDungeonPlayers;
import me.odinmain.utils.skyblock.dungeon.DungeonPlayer;
import me.odinmain.utils.skyblock.dungeon.DungeonUtils;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static me.odinmain.utils.Utils.postAndCatch;

@Mixin(DungeonUtils.class)
public class MixinDungeonTeammates {

    @Inject(method = "getDungeonTeammates(Ljava/util/ArrayList;Ljava/util/List;)Ljava/util/ArrayList;", at = @At("RETURN"), remap = false)
    public void onTeammateLoad(@NotNull ArrayList<DungeonPlayer> previousTeammates, List<? extends S38PacketPlayerListItem.AddPlayerData> tabList, CallbackInfoReturnable<ArrayList<DungeonPlayer>> cir) {
        if (!previousTeammates.isEmpty()) postAndCatch(new LoadDungeonPlayers(previousTeammates));
    }
}
