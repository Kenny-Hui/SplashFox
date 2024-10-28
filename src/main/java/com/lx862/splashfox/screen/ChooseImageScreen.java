package com.lx862.splashfox.screen;

import com.lx862.splashfox.config.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

public class ChooseImageScreen extends Screen {
    private static final double CHOOSER_WIDTH_FACTOR = 0.75;
    private final Config sessionInstance;
    private final Screen parentScreen;
    private final ButtonWidget doneButton;
    private final ChooseImageWidget chooseBuiltinImage;

    public ChooseImageScreen(Screen parentScreen, Config sessionInstance) {
        super(Text.literal("Choose an image"));
        this.parentScreen = parentScreen;
        this.sessionInstance = sessionInstance;
        doneButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.done"), (btn) -> {
            close();
        }).size(200, 20).build();
        chooseBuiltinImage = new ChooseImageWidget(this::addSelectableChild, sessionInstance.usesCustomImage() ? sessionInstance.customPath : sessionInstance.imagePath, sessionInstance);
    }

    @Override
    protected void init() {
        super.init();

        doneButton.setX((client.getWindow().getScaledWidth() / 2) - (doneButton.getWidth() / 2));
        doneButton.setY(client.getWindow().getScaledHeight() - 30);

        int fw = (int)(client.getWindow().getScaledWidth() * CHOOSER_WIDTH_FACTOR);
        int startX = (client.getWindow().getScaledWidth() - fw) / 2;
        chooseBuiltinImage.setX(startX);
        chooseBuiltinImage.setY(30);
        chooseBuiltinImage.setWidth(fw);
        chooseBuiltinImage.setHeight(client.getWindow().getScaledHeight() - 70);
        chooseBuiltinImage.init();
        addDrawableChild(doneButton);
        addSelectableChild(chooseBuiltinImage);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        drawContext.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFFFF);
        drawContext.drawTexture(RenderLayer::getGuiTextured, Screen.HEADER_SEPARATOR_TEXTURE, 0, 30 - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
        drawContext.drawTexture(RenderLayer::getGuiTextured, Screen.FOOTER_SEPARATOR_TEXTURE, 0, height - 40, 0.0F, 0.0F, this.width, 2, 32, 2);

        chooseBuiltinImage.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(parentScreen);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double scrollAmount) {
        chooseBuiltinImage.scrollRelative(scrollAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, scrollAmount);
    }

    public ChooseImageWidget getActiveWidget() {
        return chooseBuiltinImage;
    }

/*
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double scrollAmount) {
        scrollRelative(scrollAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, scrollAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle the key press first, so the new element that get selected is reflected before we do our check
        boolean bl = super.keyPressed(keyCode, scanCode, modifiers);
        return bl;
    }

    private void scrollRelative(double amount) {
        setScrollOffset(Math.min(scrolledOffset - (amount * SCROLL_MULTIPLIER), totalHeight - height));
    }

    private void setScrollOffset(double scrollOffset) {
        this.scrolledOffset = Math.max(0, scrollOffset);
        positionButtonOffset(this.scrolledOffset);
    }*/
}
