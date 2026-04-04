package com.lx862.splashfox.screen.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SplashFoxSlider extends AbstractSliderButton {
    private final Consumer<SplashFoxSlider> onApplyValue;
    private final int scale;

    public SplashFoxSlider(int x, int y, int width, int height, Component text, double value, int scale, Consumer<SplashFoxSlider> onApplyValue) {
        super(x, y, width, height, text, value / scale);
        this.onApplyValue = onApplyValue;
        this.scale = scale;
    }

    @Override
    protected void updateMessage() {
    }

    @Override
    protected void applyValue() {
        value = Math.round(value * 100.0) / 100.0;
        onApplyValue.accept(this);
        setMessage(Component.literal(String.valueOf(Math.round((value * scale) * 100.0) / 100.0)));
    }

    public double getValue() {
        return Math.round((value * scale) * 100.0) / 100.0;
    }
}
