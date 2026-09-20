package com.feb.mod.mixin.client;

import com.feb.mod.feature.Zoom;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(Camera.class)
public class CameraMixin {

    @ModifyReturnValue(
            method = "calculateFov",
            at = @At("RETURN")
    )
    private float febmod$modifyFov(float fov) {
        return Zoom.INSTANCE.modifyFov(fov);
    }
}