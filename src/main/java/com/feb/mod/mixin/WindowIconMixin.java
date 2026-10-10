package com.feb.mod.mixin;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Mixin(Minecraft.class)
public class WindowIconMixin {
    @Shadow
    private Window window;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void febmod$setWindowIcon(CallbackInfo ci) {
        try (InputStream stream = WindowIconMixin.class.getResourceAsStream("/assets/febmod/icon.png")) {
            if (stream == null) return;

            byte[] bytes = stream.readAllBytes();
            ByteBuffer encoded = MemoryUtil.memAlloc(bytes.length);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                encoded.put(bytes).flip();

                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                ByteBuffer pixels = STBImage.stbi_load_from_memory(encoded, width, height, channels, 4);

                if (pixels == null) return;

                try {
                    GLFWImage.Buffer icon = GLFWImage.malloc(1, stack);
                    icon.position(0).width(width.get(0)).height(height.get(0)).pixels(pixels);
                    GLFW.glfwSetWindowIcon(window.handle(), icon);
                } finally {
                    STBImage.stbi_image_free(pixels);
                }
            } finally {
                MemoryUtil.memFree(encoded);
            }
        } catch (Exception ignored) {
        }
    }
}