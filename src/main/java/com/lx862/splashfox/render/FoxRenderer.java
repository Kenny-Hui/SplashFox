package com.lx862.splashfox.render;

import com.lx862.splashfox.config.Config;
import com.lx862.splashfox.data.ImagePosition;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.joml.Matrix3x2fStack;

public class FoxRenderer {
    private static final RenderPipeline GUI_NO_CULL = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET).withLocation("pipeline/gui_textured").withCull(false).build());
    private double shiftY = 0;
    private double animationProgress = 0;

    public void render(Minecraft minecraft, GuiGraphicsExtractor guiGraphics, ImagePosition imagePosition, Config config, int mouseX, int mouseY, double elapsed, float alpha) {
        Identifier foxImage = config.getImageId();

        Matrix3x2fStack matrices = guiGraphics.pose();
        double scaleBasis = Math.min((double)minecraft.getWindow().getGuiScaledWidth() * 0.75, minecraft.getWindow().getGuiScaledHeight()) * 0.25;
        int splashScreenScale = (int)(scaleBasis * 0.5);

        float size = (float)config.foxSize * splashScreenScale;
        double dropHeight = config.dropHeight * splashScreenScale;
        double speedFactor = config.speed;
        boolean wobbly = config.wobbly;
        boolean flipped = config.flipped;

        final double centeredScreenWidth = (minecraft.getWindow().getGuiScaledWidth() / 2.0) - (size / 2);
        final double centeredScreenHeight = (minecraft.getWindow().getGuiScaledHeight() / 2.0) - dropHeight;
        final double x;
        final double y;

        switch(imagePosition) {
            case LEFT_TO_MOJANG -> {
                x = centeredScreenWidth - (6 * splashScreenScale);
                y = centeredScreenHeight + shiftY;
            }
            case RIGHT_TO_MOJANG -> {
                x = centeredScreenWidth + (6 * splashScreenScale);
                y = centeredScreenHeight + shiftY;
            }
            case ABOVE_MOJANG -> {
                x = centeredScreenWidth;
                y = centeredScreenHeight - (splashScreenScale) - size + shiftY;
            }
            case REPLACE_MOJANG -> {
                x = centeredScreenWidth;
                y = centeredScreenHeight + shiftY;
            }
            case FOLLOW_MOUSE -> {
                x = mouseX;
                y = mouseY + shiftY;
            }
            case GUI_PREVIEW -> {
                x = minecraft.getWindow().getGuiScaledHeight() > 450 ? centeredScreenWidth : (flipped ? 0 : minecraft.getWindow().getGuiScaledWidth() - size);
                y = centeredScreenHeight + shiftY;
            }
            default -> {
                x = centeredScreenWidth;
                y = shiftY;
            }
        }

        matrices.pushMatrix();
        matrices.translate((float)x, (float)y);

        if(flipped) {
            matrices.scale(-1, 1);
            matrices.translate(-size, 0);
        }

        if(wobbly && animationProgress <= 0) {
            // Deform the fox
            float deformScale = (float)(animationProgress * (config.dropHeight * 0.75));
            matrices.translate(size / 2f, size);
            matrices.scale(1 - deformScale, 1 + deformScale);
            matrices.translate(-(size / 2f), -size);
        }

        guiGraphics.blit(GUI_NO_CULL, foxImage, 0, 0, 0, 0, (int)size, (int)size, (int)size, (int)size, ARGB.color(alpha, CommonColors.WHITE));
        matrices.popMatrix();

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
