package com.lx.splashfox.render;

import com.lx.splashfox.config.Config;
import com.lx.splashfox.data.FoxPosition;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class FoxRenderer {
    private double shiftY = 0;
    private double animationProgress = 0;
    public void render(MinecraftClient client, DrawContext drawContext, FoxPosition foxPosition, Config config, int mouseX, int mouseY, double elapsed, float alpha) {
        MatrixStack matrices = drawContext.getMatrices();
        double d = Math.min((double)client.getWindow().getScaledWidth() * 0.75, client.getWindow().getScaledHeight()) * 0.25;
        int splashScreenScale = (int)(d * 0.5);

        double size = config.foxSize * splashScreenScale;
        double dropHeight = config.dropHeight * splashScreenScale;
        double speedFactor = config.speed;
        boolean wobbly = config.wobbly;
        boolean flipped = config.flipped;
        Identifier foxImage = config.getFoxImageId();

        double centeredScreenWidth = (client.getWindow().getScaledWidth() / 2.0) - (size / 2);
        double centeredScreenHeight = (client.getWindow().getScaledHeight() / 2.0) - dropHeight;
        double x = centeredScreenWidth;
        double y = shiftY;

        if(foxPosition == FoxPosition.LEFT_TO_MOJANG) {
            x = centeredScreenWidth - (6 * splashScreenScale);
            y = centeredScreenHeight + shiftY;
        }

        if(foxPosition == FoxPosition.RIGHT_TO_MOJANG) {
            x = centeredScreenWidth + (6 * splashScreenScale);
            y = centeredScreenHeight + shiftY;
        }

        if(foxPosition == FoxPosition.ABOVE_MOJANG) {
            x = centeredScreenWidth;
            y = centeredScreenHeight - (splashScreenScale) - size + shiftY;
        }

        if(foxPosition == FoxPosition.REPLACE_MOJANG) {
            x = centeredScreenWidth;
            y = centeredScreenHeight + shiftY;
        }

        if(foxPosition == FoxPosition.FOLLOW_MOUSE) {
            x = mouseX;
            y = mouseY + shiftY;
        }

        // Preview mode
        if(foxPosition == null) {
            x = client.getWindow().getScaledHeight() > 450 ? centeredScreenWidth : (flipped ? 0 : client.getWindow().getScaledWidth() - size);
            y = centeredScreenHeight + shiftY;
        }

        matrices.push();
        matrices.translate(x, y, 0);

        if(wobbly && animationProgress <= 0) {
            // Deform the fox
            float deformScale = (float)(animationProgress * (config.dropHeight * 0.75));
            matrices.translate(size / 2.0, size, 0);
            matrices.scale(1 - deformScale, 1 + deformScale, 1);
            matrices.translate(-(size / 2.0), -size, 0);
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        drawContext.drawTexture(foxImage, 0, 0, 0, 0, (int)size, (int)size, (int)(flipped ? -size : size), (int)size);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.pop();

        animationProgress = getBounceProgress(speedFactor, elapsed / 10, wobbly);
        shiftY = (dropHeight * Math.min(0, -animationProgress)) + dropHeight;
    }

    private double getBounceProgress(double speedFactor, double x, boolean wobbly) {
        if(wobbly) {
            return Math.abs(Math.sin(x * speedFactor) * 1.2) - 0.2;
        } else {
            return Math.abs(Math.sin(x * speedFactor));
        }
    }
}
