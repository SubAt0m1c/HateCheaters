package com.github.subat0m1c.hatecheaters.mixin;

import com.github.subat0m1c.hatecheaters.HateCheaters;
import me.odinmain.OdinMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OdinMain.class)
public class MixinOdinMain {

    @Inject(method = "loadComplete", at = @At("RETURN"), remap = false)
    public void loadComplete(CallbackInfo ci) {
        HateCheaters.Companion.onOdinLoad();
    }
}
