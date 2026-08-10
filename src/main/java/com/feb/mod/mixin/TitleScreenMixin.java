package com.feb.mod.mixin;

import com.feb.mod.ui.gui.components.BlueButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void reworkButtons(CallbackInfo ci) {
        java.util.List<Button> found = new java.util.ArrayList<>();
        for (GuiEventListener listener : this.children()) {
            if (listener instanceof Button button) {
                found.add(button);
            }
        }

        for (Button original : found) {
            if (original instanceof SpriteIconButton) {
                original.visible = false;
                original.active = false;
                continue;
            }
            if (original instanceof PlainTextButton) {
                continue;
            }

            original.visible = false;
            original.active = false;

            String label = original.getMessage().getString();
            int x = original.getX(), y = original.getY(), w = original.getWidth(), h = original.getHeight();

            Button.OnPress action = switch (label) {
                case String s when s.contains("Singleplayer") ->
                        btn -> this.minecraft.setScreen(new net.minecraft.client.gui.screens.worldselection.SelectWorldScreen(this));
                case String s when s.contains("Multiplayer") -> btn -> {
                    Screen screen = this.minecraft.options.skipMultiplayerWarning
                            ? new net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen(this)
                            : new net.minecraft.client.gui.screens.multiplayer.SafetyScreen(this);
                    this.minecraft.setScreen(screen);
                };
                case String s when s.contains("Realms") ->
                        btn -> this.minecraft.setScreen(new com.mojang.realmsclient.RealmsMainScreen(this));
                case String s when s.contains("Options") ->
                        btn -> this.minecraft.setScreen(new net.minecraft.client.gui.screens.options.OptionsScreen(this, this.minecraft.options, false));
                case String s when s.contains("Quit") ->
                        btn -> this.minecraft.stop();
                default -> btn -> { };
            };

            this.addRenderableWidget(BlueButton.of(x, y, w, h, label, action));
        }

        this.addRenderableWidget(
                BlueButton.of(this.width / 2 - 100, this.height / 2 + 50, 200, 20, "FebMod", btn ->
                        this.minecraft.setScreen(new com.feb.mod.ui.gui.FebModScreen(this))
                )
        );
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void replaceBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        graphics.fillGradient(0, 0, this.width, this.height, 0xFF001a33, 0xFF00121f);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void replaceLogo(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        graphics.fill(0, 20, this.width, 110, 0xFF001a33);

        String title = "FEBMOD";
        float scale = 3.0f;
        int scaledWidth = (int) (this.font.width(title) * scale);

        graphics.pose().pushMatrix();
        graphics.pose().translate((this.width - scaledWidth) / 2f, 40);
        graphics.pose().scale(scale, scale);
        graphics.text(this.font, title, 0, 0, 0xFF66ccff, true);
        graphics.pose().popMatrix();
    }
}