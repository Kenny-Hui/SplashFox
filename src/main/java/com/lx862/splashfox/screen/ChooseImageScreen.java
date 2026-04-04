package com.lx862.splashfox.screen;

import com.lx862.splashfox.config.Config;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class ChooseImageScreen extends Screen {
    private static final double CHOOSER_WIDTH_FACTOR = 0.75;
    private final Screen parentScreen;
    private final ChooseImageWidget chooseImageWidget;

    public ChooseImageScreen(Screen parentScreen, Config configInstance) {
        super(Component.translatable("splashfox.gui.choose_img"));
        this.parentScreen = parentScreen;
        chooseImageWidget = new ChooseImageWidget(getFont(), this::addWidget, configInstance.usesCustomImage() ? configInstance.customPath : configInstance.imagePath, configInstance);
    }

    @Override
    protected void init() {
        super.init();
        Window window = minecraft.getWindow();

        Button doneButton = new Button.Builder(Component.translatable("splashfox.gui.done"), (btn) -> this.onClose())
                .size(200, 20)
                .pos((window.getGuiScaledWidth() / 2) - (200 / 2), window.getGuiScaledHeight() - 30)
                .build();
        addRenderableWidget(doneButton);

        int availWidth = (int)(minecraft.getWindow().getGuiScaledWidth() * CHOOSER_WIDTH_FACTOR);
        int startX = (minecraft.getWindow().getGuiScaledWidth() - availWidth) / 2;
        chooseImageWidget.setX(startX);
        chooseImageWidget.setY(30);
        chooseImageWidget.setWidth(availWidth);
        chooseImageWidget.setHeight(minecraft.getWindow().getGuiScaledHeight() - 70);
        chooseImageWidget.init();
        addWidget(chooseImageWidget);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, delta);

        guiGraphicsExtractor.centeredText(font, title, width / 2, 10, 0xFFFFFFFF);
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, 0, 30 - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, height - 40, 0.0F, 0.0F, this.width, 2, 32, 2);

        chooseImageWidget.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double scrollAmount) {
        chooseImageWidget.scrollRelative(scrollAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, scrollAmount);
    }
}
