package com.feb.mod.mixin;

import com.feb.mod.ModInfo;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class WindowTitleMixin {
    @Inject(method = "updateTitle", at = @At("RETURN"))
    private void changeWindowTitle(CallbackInfo ci) {
        Minecraft.getInstance().getWindow().setTitle(
                ModInfo.INSTANCE.getMOD_NAME() + " " + ModInfo.INSTANCE.getVERSION()
        );
    }
}