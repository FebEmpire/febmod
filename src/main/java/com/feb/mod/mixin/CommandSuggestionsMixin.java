package com.feb.mod.mixin;

import com.feb.mod.api.command.CommandApi;
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
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    public abstract void showSuggestions(boolean immediateNarration);

    @Inject(method = "updateCommandInfo", at = @At("HEAD"), cancellable = true)
    private void feb$updateCommandInfo(CallbackInfo ci) {
        if (!(this.screen instanceof ChatScreen)) {
            return;
        }

        String text = this.input.getValue();

        if (!text.startsWith(".")) {
            return;
        }

        String command = text.substring(1);

        SuggestionsBuilder builder = new SuggestionsBuilder(text, 1);

        for (String name : CommandApi.getAllCommands()) {
            if (name.regionMatches(
                    true, 0, command, 0, command.length()
            )) {
                builder.suggest(name);
            }
        }

        this.pendingSuggestions = builder.buildFuture();
        this.showSuggestions(false);

        ci.cancel();
    }
}