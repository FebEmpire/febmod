
package com.feb.mod.mixin;

import com.feb.mod.ui.gui.components.BlueButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Mixin(net.minecraft.client.gui.screens.TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    private boolean customUiEnabled = true;

    private final List<Button> replacementButtons = new ArrayList<>();
    private final Map<Button, boolean[]> originalButtonStates = new IdentityHashMap<>();

    private Button customUiToggleButton;
    private Button vanillaUiToggleButton;
    private Button febModButton;
    private Button vanillaFebModButton;

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void reworkButtons(CallbackInfo ci) {
        replacementButtons.clear();
        originalButtonStates.clear();

        List<Button> found = new ArrayList<>();

        for (GuiEventListener listener : this.children()) {
            if (listener instanceof Button button) {
                found.add(button);
                originalButtonStates.put(
                        button,
                        new boolean[]{button.visible, button.active}
                );
            }
        }

        for (Button original : found) {
            if (original instanceof SpriteIconButton
                    || original instanceof PlainTextButton) {
                continue;
            }

            String label = original.getMessage().getString();
            int x = original.getX();
            int y = original.getY();
            int w = original.getWidth();
            int h = original.getHeight();

            Button.OnPress action = switch (label) {
                case String s when s.contains("Singleplayer") ->
                        btn -> this.minecraft.gui.setScreen(
                                new net.minecraft.client.gui.screens.worldselection.SelectWorldScreen(this)
                        );

                case String s when s.contains("Multiplayer") -> btn -> {
                    Screen screen = this.minecraft.options.skipMultiplayerWarning
                            ? new net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen(this)
                            : new net.minecraft.client.gui.screens.multiplayer.SafetyScreen(this);

                    this.minecraft.gui.setScreen(screen);
                };

                case String s when s.contains("Realms") ->
                        btn -> this.minecraft.gui.setScreen(
                                new com.mojang.realmsclient.RealmsMainScreen(this)
                        );

                case String s when s.contains("Options") ->
                        btn -> this.minecraft.gui.setScreen(
                                new net.minecraft.client.gui.screens.options.OptionsScreen(
                                        this, this.minecraft.options, false
                                )
                        );

                case String s when s.contains("Quit") ->
                        btn -> this.minecraft.stop();

                default -> btn -> {};
            };

            Button replacement = BlueButton.of(x, y, w, h, label, action);
            replacementButtons.add(replacement);
            this.addRenderableWidget(replacement);
        }

        int febModX = this.width / 2 - 100;
        int febModY = this.height / 2 + 50;

        febModButton = BlueButton.of(
                febModX,
                febModY,
                200,
                20,
                "FebMod",
                btn -> this.minecraft.gui.setScreen(
                        new com.feb.mod.ui.gui.FebModScreen(this)
                )
        );

        vanillaFebModButton = Button.builder(
                Component.literal("FebMod"),
                btn -> this.minecraft.gui.setScreen(
                        new com.feb.mod.ui.gui.FebModScreen(this)
                )
        ).bounds(febModX, febModY, 200, 20).build();

        this.addRenderableWidget(febModButton);
        this.addRenderableWidget(vanillaFebModButton);

        int toggleX = 10;
        int toggleY = this.height - 30;

        customUiToggleButton = BlueButton.of(
                toggleX,
                toggleY,
                75,
                20,
                "UI: ON",
                btn -> {
                    customUiEnabled = false;
                    updateUiVisibility();
                }
        );

        vanillaUiToggleButton = Button.builder(
                Component.literal("UI: OFF"),
                btn -> {
                    customUiEnabled = true;
                    updateUiVisibility();
                }
        ).bounds(toggleX, toggleY, 75, 20).build();

        this.addRenderableWidget(customUiToggleButton);
        this.addRenderableWidget(vanillaUiToggleButton);

        updateUiVisibility();
    }

    private void updateUiVisibility() {
        for (Map.Entry<Button, boolean[]> entry : originalButtonStates.entrySet()) {
            Button button = entry.getKey();
            boolean[] state = entry.getValue();

            if (customUiEnabled && !(button instanceof PlainTextButton)) {
                button.visible = false;
                button.active = false;
            } else {
                button.visible = state[0];
                button.active = state[1];
            }
        }

        for (Button button : replacementButtons) {
            button.visible = customUiEnabled;
            button.active = customUiEnabled;
        }

        customUiToggleButton.visible = customUiEnabled;
        customUiToggleButton.active = customUiEnabled;

        vanillaUiToggleButton.visible = !customUiEnabled;
        vanillaUiToggleButton.active = !customUiEnabled;

        febModButton.visible = customUiEnabled;
        febModButton.active = customUiEnabled;

        vanillaFebModButton.visible = !customUiEnabled;
        vanillaFebModButton.active = !customUiEnabled;
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void replaceBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        if (!customUiEnabled) {
            return;
        }

        graphics.fillGradient(
                0,
                0,
                this.width,
                this.height,
                0xFF001a33,
                0xFF00121f
        );
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void replaceLogo(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        if (!customUiEnabled) {
            return;
        }

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
