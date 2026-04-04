package com.lx862.splashfox.screen;

import com.lx862.splashfox.config.Config;
import com.lx862.splashfox.data.ImagePosition;
import com.lx862.splashfox.data.ScreenAlignment;
import com.lx862.splashfox.SplashFox;
import com.lx862.splashfox.render.FoxRenderer;
import com.lx862.splashfox.screen.widget.SplashFoxSlider;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigScreen extends Screen {
    private static final int MAX_WIDTH = 320;
    private final Config tmpConfigInstance;
    private final FoxRenderer foxRenderer;
    private final Screen parentScreen;
    private double elapsed;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("splashfox.gui.config_title"));
        this.parentScreen = parent;
        // Make another instance of config so changes only apply if the user click save
        tmpConfigInstance = Config.readConfig();
        foxRenderer = new FoxRenderer();
    }

    @Override
    protected void init() {
        super.init();

        int curY = 40;
        List<Tuple<String, Integer>> labels = new ArrayList<>();

        Button chooseImageButton = new Button.Builder(Component.translatable("splashfox.gui.choose"), (d) -> {
            ChooseImageScreen chooseImageScreen = new ChooseImageScreen(this, tmpConfigInstance);
            minecraft.setScreen(chooseImageScreen);
        }).pos(getX(100, ScreenAlignment.RIGHT), curY).width(100).build();
        labels.add(new Tuple<>("splashfox.gui.choose_img", curY));
        addRenderableWidget(chooseImageButton);

        curY += 20;

        SplashFoxSlider speedSlider = new SplashFoxSlider(getX(100, ScreenAlignment.RIGHT), curY, 100, 20, Component.literal(String.valueOf(tmpConfigInstance.speed)), tmpConfigInstance.speed, 2, (slider) -> {
            tmpConfigInstance.speed = slider.getValue();
        });
        labels.add(new Tuple<>("splashfox.gui.speed", curY));
        addRenderableWidget(speedSlider);

        curY += 20;

        SplashFoxSlider dropHeightSlider = new SplashFoxSlider(getX(100, ScreenAlignment.RIGHT), curY, 100, 20, Component.literal(String.valueOf(tmpConfigInstance.dropHeight)), tmpConfigInstance.dropHeight, 3, (slider) -> {
            tmpConfigInstance.dropHeight = slider.getValue();
        });
        labels.add(new Tuple<>("splashfox.gui.drop_height", curY));
        addRenderableWidget(dropHeightSlider);

        curY += 20;

        SplashFoxSlider foxSizeSlider = new SplashFoxSlider(getX(100, ScreenAlignment.RIGHT), curY, 100, 20, Component.literal(String.valueOf(tmpConfigInstance.foxSize)), tmpConfigInstance.foxSize, 2, (slider) -> {
            tmpConfigInstance.foxSize = slider.getValue();
        });
        labels.add(new Tuple<>("splashfox.gui.blobfox_size", curY));
        addRenderableWidget(foxSizeSlider);

        curY += 20;

        Checkbox flippedCheckbox = Checkbox.builder(Component.literal(""), font)
                .selected(tmpConfigInstance.flipped)
                .pos(0, curY)
                .onValueChange((btn, checked) -> {
                    tmpConfigInstance.flipped = checked;
                }).build();
        flippedCheckbox.setX(getX(flippedCheckbox, ScreenAlignment.RIGHT));
        labels.add(new Tuple<>("splashfox.gui.flipped", curY));
        addRenderableWidget(flippedCheckbox);

        curY += 20;

        Checkbox wobblyCheckbox = Checkbox.builder(Component.literal(""), font)
                .selected(tmpConfigInstance.wobbly)
                .pos(0, curY)
                .onValueChange((btn, checked) -> {
                    tmpConfigInstance.wobbly = checked;
                }).build();
        wobblyCheckbox.setX(getX(wobblyCheckbox, ScreenAlignment.RIGHT));
        labels.add(new Tuple<>("splashfox.gui.wobbly", curY));
        addRenderableWidget(wobblyCheckbox);

        curY += 20;

        Button positionButton = new Button.Builder(Component.translatable("splashfox.gui.position." + tmpConfigInstance.position.toString()), (d) -> {
            int index = tmpConfigInstance.position.ordinal();
            ImagePosition[] imagePositions = Arrays.stream(ImagePosition.values()).filter(e -> e.selectable).toArray(ImagePosition[]::new);
            tmpConfigInstance.position = imagePositions[(index + 1) % imagePositions.length];
            d.setMessage(Component.translatable("splashfox.gui.position." + tmpConfigInstance.position.toString()));
        }).pos(getX(120, ScreenAlignment.RIGHT), curY).width(120).build();

        labels.add(new Tuple<>("splashfox.gui.position", curY));
        addRenderableWidget(positionButton);

        Button discardButton = new Button.Builder(Component.translatable("splashfox.gui.discard_config"),
                (d) -> onClose()
        ).pos(getX((MAX_WIDTH/2), ScreenAlignment.LEFT), this.height - 30).width(MAX_WIDTH / 2).build();
        addRenderableWidget(discardButton);

        Button saveButton = new Button.Builder(Component.translatable("splashfox.gui.save_config"), (d) -> {
            Config.needUpdateTexture = true;
            Config.writeConfig(tmpConfigInstance);
            SplashFox.config = Config.readConfig();
            onClose();
        }).pos(getX((MAX_WIDTH/2), ScreenAlignment.RIGHT), this.height - 30).width(MAX_WIDTH / 2).build();
        addRenderableWidget(saveButton);

        for(Tuple<String, Integer> label : labels) {
            StringWidget labelWidget = new StringWidget(Component.translatable(label.getA()), font);
            labelWidget.setX(getStartX());
            labelWidget.setY(label.getB() + font.lineHeight);
            addRenderableWidget(labelWidget);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
        elapsed += delta;

        // Render fox preview :D
        foxRenderer.render(minecraft, guiGraphics, ImagePosition.GUI_PREVIEW, tmpConfigInstance, mouseX, mouseY, elapsed, 1.0f);

        guiGraphics.centeredText(font, title, this.width / 2, 12, CommonColors.WHITE);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, 0, 30, 0.0F, 0.0F, this.width, 2, 32, 2);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height - 40, 0.0F, 0.0F, this.width, 2, 32, 2);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }

    private int getX(int width, ScreenAlignment type) {
        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        int startX = (scaledWidth - MAX_WIDTH) / 2;

        if(type == ScreenAlignment.LEFT) {
            return startX;
        }

        if(type == ScreenAlignment.CENTERED) {
            return (scaledWidth / 2) - (width / 2);
        }

        if(type == ScreenAlignment.RIGHT) {
            return scaledWidth - startX - width;
        }

        return 0;
    }

    private int getX(AbstractWidget widget, ScreenAlignment type) {
        return getX(widget.getWidth(), type);
    }


    private int getStartX() {
        return getX(0, ScreenAlignment.LEFT);
    }
}
