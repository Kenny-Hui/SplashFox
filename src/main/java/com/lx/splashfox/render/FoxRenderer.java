package com.lx.splashfox.render;

import com.lx.splashfox.config.Config;
import com.lx.splashfox.data.Position;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class FoxRenderer {
    private double shiftY = 0;
    private double shiftYProgress = 0;
    public void render(MinecraftClient client, MatrixStack matrices, Position position, Config config, int mouseX, int mouseY, double elapsed, float alpha) {
        double d = Math.min((double)client.getWindow().getScaledWidth() * 0.75, client.getWindow().getScaledHeight()) * 0.25;
        int splashScreenScale = (int)(d * 0.5);

        double size = config.foxSize * splashScreenScale;
        double dropHeight = config.dropHeight * splashScreenScale;
        double speedFactor = config.speed;
        boolean wobbly = config.wobbly;
        boolean flipped = config.flipped;
        String imagePath = config.imagePath;

        double centeredWidth = (client.getWindow().getScaledWidth() / 2.0) - (size / 2);
        double centeredHeight = (client.getWindow().getScaledHeight() / 2.0) - dropHeight;
        double x = centeredWidth;
        double y = shiftY;

        if(position == Position.LEFT_TO_MOJANG) {
            x = centeredWidth - (6 * splashScreenScale);
            y = centeredHeight + shiftY;
        }

        if(position == Position.RIGHT_TO_MOJANG) {
            x = centeredWidth + (6 * splashScreenScale);
            y = centeredHeight + shiftY;
        }

        if(position == Position.ABOVE_MOJANG) {
            x = centeredWidth;
            y = centeredHeight - (splashScreenScale) - size + shiftY;
        }

        if(position == Position.REPLACE_MOJANG) {
            x = centeredWidth;
            y = centeredHeight + shiftY;
        }

        if(position == Position.FOLLOW_MOUSE) {
            x = mouseX;
            y = mouseY + shiftY;
        }

        // Preview mode
        if(position == null) {
            x = client.getWindow().getScaledHeight() > 450 ? centeredWidth : (flipped ? 0 : client.getWindow().getScaledWidth() - size);
            y = centeredHeight + shiftY;
        }

        matrices.push();
        matrices.translate(x, y, 0);

        if(wobbly && shiftYProgress <= 0) {
            // Deform the fox
            float deformScale = (float)(shiftYProgress * (config.dropHeight * 0.75));
            matrices.translate(size / 2.0, size, 0);
            matrices.scale(1 - deformScale, 1 + deformScale, 1);
            matrices.translate(-(size / 2.0), -size, 0);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, new Identifier(imagePath));
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, (int)size, (int)size, (int)(flipped ? -size : size), (int)size);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.pop();

        shiftYProgress = getBounceProgress(speedFactor, elapsed / 10, wobbly);
        shiftY = (dropHeight * Math.min(0, -shiftYProgress)) + dropHeight;
    }

    private double getBounceProgress(double speedFactor, double x, boolean wobbly) {
        if(wobbly) {
            return Math.abs(Math.sin(x * speedFactor) * 1.2) - 0.2;
        } else {
            return Math.abs(Math.sin(x * speedFactor));
        }
    }
}
