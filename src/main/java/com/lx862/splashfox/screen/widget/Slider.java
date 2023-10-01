package com.lx862.splashfox.screen.widget;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class Slider extends SliderWidget {
    private final int scale;
    private final Consumer<Slider> onApplyValue;
    public Slider(int x, int y, int width, int height, Text text, double value, int scale, Consumer<Slider> onApplyValue) {
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
        setMessage(Text.literal(String.valueOf(Math.round((value * scale) * 100.0) / 100.0)));
    }

    public double getValue() {
        return Math.round((value * scale) * 100.0) / 100.0;
    }
}
