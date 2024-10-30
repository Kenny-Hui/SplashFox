package com.lx862.splashfox.screen;

import com.lx862.splashfox.config.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

public class ChooseImageScreen extends Screen {
    private static final double CHOOSER_WIDTH_FACTOR = 0.75;
    private final Screen parentScreen;
    private final ButtonWidget doneButton;
    private final ChooseImageWidget chooseImageWidget;

    public ChooseImageScreen(Screen parentScreen, Config configInstance) {
        super(Text.translatable("splashfox.gui.choose_img"));
        this.parentScreen = parentScreen;
        doneButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.done"), (btn) -> {
            close();
        }).size(200, 20).build();
        chooseImageWidget = new ChooseImageWidget(this::addSelectableChild, configInstance.usesCustomImage() ? configInstance.customPath : configInstance.imagePath, configInstance);
    }

    @Override
    protected void init() {
        super.init();

        doneButton.setX((client.getWindow().getScaledWidth() / 2) - (doneButton.getWidth() / 2));
        doneButton.setY(client.getWindow().getScaledHeight() - 30);

        int fw = (int)(client.getWindow().getScaledWidth() * CHOOSER_WIDTH_FACTOR);
        int startX = (client.getWindow().getScaledWidth() - fw) / 2;
        chooseImageWidget.setX(startX);
        chooseImageWidget.setY(30);
        chooseImageWidget.setWidth(fw);
        chooseImageWidget.setHeight(client.getWindow().getScaledHeight() - 70);
        chooseImageWidget.init();
        addDrawableChild(doneButton);
        addSelectableChild(chooseImageWidget);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        drawContext.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFFFF);
        drawContext.drawTexture(RenderLayer::getGuiTextured, Screen.HEADER_SEPARATOR_TEXTURE, 0, 30 - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
        drawContext.drawTexture(RenderLayer::getGuiTextured, Screen.FOOTER_SEPARATOR_TEXTURE, 0, height - 40, 0.0F, 0.0F, this.width, 2, 32, 2);

        chooseImageWidget.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(parentScreen);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double scrollAmount) {
        chooseImageWidget.scrollRelative(scrollAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, scrollAmount);
    }
}
