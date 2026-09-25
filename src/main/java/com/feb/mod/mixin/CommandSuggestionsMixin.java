package com.feb.mod.mixin;

import com.feb.mod.api.command.CommandApi;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {
    @Shadow @Final
    private EditBox input;

    @Shadow @Final
    private Screen screen;

    @Shadow
    CompletableFuture<Suggestions> pendingSuggestions;

    @Inject(method = "updateCommandInfo", at = @At("HEAD"), cancellable = true)
    private void feb$updateCommandInfo(CallbackInfo ci) {
        if (!(screen instanceof ChatScreen)) {
            return;
        }

        String text = input.getValue();

        if (!text.startsWith(".")) {
            return;
        }

        String command = text.substring(1);
        String commandLower = command.toLowerCase();

        SuggestionsBuilder builder = new SuggestionsBuilder(text, 1);

        for (String name : CommandApi.getAllCommands()) {
            if (name.toLowerCase().startsWith(commandLower)) {
                builder.suggest(name);
            }
        }

        this.pendingSuggestions = builder.buildFuture();

        ci.cancel();
    }
}