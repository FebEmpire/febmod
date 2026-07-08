package com.feb.mod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.ChatAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
* Cred to Oblongboot.dev for this file
* https://oblongboot.dev/  󠁛󠀣󠀱󠁥󠀰󠀷󠀷󠀷󠀬󠀣󠀰󠁢󠀵󠀴󠀸󠀴󠁝
 */

@Mixin(Minecraft.class)
public class ChatRestrictionBypassMixin {
    @Inject(method = "computeChatAbilities", at = @At("HEAD"), cancellable = true)
    private void onComputeChatAbilities(CallbackInfoReturnable<ChatAbilities> info) {
        ChatAbilities.Builder builder = new ChatAbilities.Builder();
        info.setReturnValue(builder.build());
    }
}