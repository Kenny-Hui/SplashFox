package com.lx862.splashfox.screen.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ChooseButton extends ButtonWidget {
    private boolean selected;
    private int baseY;
    private final Identifier buttonTexture;
    public static final int PADDING = 4;

    public ChooseButton(int x, int y, int width, int height, boolean selected, Identifier buttonTexture, ButtonWidget.PressAction pressAction, Text text) {
        super(x, y, width, height, text, pressAction, DEFAULT_NARRATION_SUPPLIER);
        this.baseY = y;
        this.buttonTexture = buttonTexture;
        this.selected = selected;
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        int startX = getX() - PADDING;
        int startY = getY() - PADDING;
        int sizeWidth = getWidth() + PADDING + PADDING;
        int sizeHeight = getHeight() + PADDING + PADDING;
        int endX = getX() + getWidth() + PADDING;
        int endY = getY() + getHeight() + PADDING;
        if(selected) {
            drawContext.fill(startX, startY, endX, endY, 0xFF000000);
            drawContext.drawBorder(startX, startY, sizeWidth, sizeHeight, 0xFFFFFFFF);
        } else if (this.isHovered() || this.isFocused()) {
            // Selected
            drawContext.drawBorder(startX, startY, sizeWidth, sizeHeight, 0x66FFFFFF);
        }
        drawContext.drawTexture(RenderLayer::getGuiTextured, buttonTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
    }

    public void setSelected(boolean bl) {
        this.selected = bl;
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.baseY = y;
    }

    public void setYOffset(int offset) {
        super.setY(baseY + offset);
    }
}
