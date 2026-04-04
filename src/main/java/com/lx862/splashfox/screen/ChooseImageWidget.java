package com.lx862.splashfox.screen;

import com.lx862.splashfox.SplashFox;
import com.lx862.splashfox.config.Config;
import com.lx862.splashfox.data.FileSystemResourceTexture;
import com.lx862.splashfox.screen.widget.ChooseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChooseImageWidget extends AbstractWidget {
    private static final int SCROLL_MULTIPLIER = 8;
    private static final int BUTTON_Y_MARGIN = 20;
    private static final int BUTTON_SIZE = 40;
    private final List<ChooseButton> subWidgets;
    private final Consumer<Button> addDrawableChild;
    private final String initialSelection;
    private final Config sessionInstance;
    private final Font font;
    private double scrolledOffset = 0;
    private int totalHeight = 0;
    private int customImageSeparatorY;

    public ChooseImageWidget(Font font, Consumer<Button> addSelectableChild, String initialSelection, Config sessionInstance) {
        super(50, 50, 50, 50, Component.literal("Choose an image"));
        this.initialSelection = initialSelection;
        this.addDrawableChild = addSelectableChild;
        this.sessionInstance = sessionInstance;
        this.font = font;
        this.subWidgets = new ArrayList<>();
    }

    public void init() {
        final List<ChooseButton> builtInImages = new ArrayList<>();
        final List<ChooseButton> customImages = new ArrayList<>();

        try {
            Path builtInPath = Path.of(SplashFox.class.getResource("/assets/splashfox/textures/gui/").toURI());
            Path customPath = Config.CUSTOM_IMG_PATH;

            Files.list(builtInPath).forEach(filePath -> {
                builtInImages.add(addImageButton(filePath, false));
            });
            Files.list(customPath).forEach(filePath -> {
                customImages.add(addImageButton(filePath, true));
            });
        } catch (Exception e) {
            SplashFox.LOGGER.error("", e);
        }

        subWidgets.clear();
        subWidgets.addAll(builtInImages);
        subWidgets.addAll(customImages);

        double widgetWidth = getWidth();
        int startX = getX();
        int startY = getY();
        int x = ChooseButton.PADDING;
        int y = ChooseButton.PADDING;

        for (ChooseButton button : builtInImages) {
            int nextX = x + button.getWidth();
            if (nextX > widgetWidth) {
                x = ChooseButton.PADDING;
                y += button.getHeight() + BUTTON_Y_MARGIN;
            }
            button.setX(startX + x);
            button.setY(startY + y);
            x += button.getWidth() + BUTTON_Y_MARGIN;
            addDrawableChild.accept(button);
        }

        // Open new line for custom images
        x = ChooseButton.PADDING;
        y += (int)(BUTTON_Y_MARGIN * 2.5);
        customImageSeparatorY = y;
        y += BUTTON_Y_MARGIN;

        for (ChooseButton button : customImages) {
            int nextX = x + button.getWidth();
            if (nextX > widgetWidth) {
                x = getX();
                y += button.getHeight() + BUTTON_Y_MARGIN;
            }
            button.setX(startX + x);
            button.setY(startY + y);
            x += button.getWidth() + BUTTON_Y_MARGIN;
            addDrawableChild.accept(button);
        }

        totalHeight = y + BUTTON_SIZE;
        scrollRelative(0);
    }

    public ChooseButton addImageButton(Path filePath, boolean custom) {
        String fileName = filePath.getFileName().toString();
        String fileNameNoExtension = FilenameUtils.removeExtension(fileName);
        Identifier imageId = custom ? sessionInstance.getCustomImageId(fileName) : Identifier.fromNamespaceAndPath("splashfox", "textures/gui/" + fileName);
        if(custom) {
            Minecraft.getInstance().getTextureManager().registerAndLoad(imageId, new FileSystemResourceTexture(fileName, imageId));
        }

        ChooseButton chooseButton = new ChooseButton(0, 0, BUTTON_SIZE, BUTTON_SIZE, initialSelection.equals(custom ? fileName : imageId.toString()), imageId, e -> {
            sessionInstance.imagePath = custom ? null : imageId.toString();
            sessionInstance.customPath = custom ? fileName : null;
            for(ChooseButton btn : subWidgets) {
                btn.setSelected(false);
            }
            ((ChooseButton)e).setSelected(true);
        }, Component.literal(fileNameNoExtension));

        chooseButton.setTooltip(Tooltip.create(Component.literal(custom ? "Custom: " + fileNameNoExtension : fileNameNoExtension)));
        return chooseButton;
    }

    private void positionButtonOffset(double offset) {
        for(ChooseButton button : subWidgets) {
            button.setYOffset((int)-offset);
        }
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        guiGraphicsExtractor.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());

        for(Button button : subWidgets) {
            final boolean buttonInVisibleArea = mouseY >= 30 && mouseY <= Minecraft.getInstance().getWindow().getGuiScaledHeight() - 40;
            button.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, delta);
            button.active = buttonInVisibleArea;
        }

        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate(getX(), (float)(getY() - scrolledOffset));
        Component customImageText = Component.translatable("splashfox.gui.custom_img");
        guiGraphicsExtractor.text(font, customImageText, 0, customImageSeparatorY, CommonColors.WHITE);
        guiGraphicsExtractor.fill(font.width(customImageText) + 4, customImageSeparatorY + (font.lineHeight / 2), getWidth(), customImageSeparatorY + (font.lineHeight / 2) + 1, 0xFFAAAAAA);
        guiGraphicsExtractor.pose().popMatrix();

        guiGraphicsExtractor.disableScissor();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    public boolean keyPressed(KeyEvent keyInput) {
        // Handle the key press first, so the new element that get selected is reflected before we do our check
        boolean bl = super.keyPressed(keyInput);

        for(ChooseButton chooseButton : subWidgets) {
            if(chooseButton.isHoveredOrFocused()) {
                int highestY = chooseButton.getY() - ChooseButton.PADDING;
                int lowestY = chooseButton.getY() + chooseButton.getHeight() + ChooseButton.PADDING;
                if(lowestY > height + scrolledOffset) {
                    setScrollOffset(lowestY - (height - scrolledOffset));
                }

                if(highestY < 0) {
                    scrollRelative(-highestY);
                }
            }
        }

        return bl;
    }

    public void scrollRelative(double amount) {
        setScrollOffset(Math.min(scrolledOffset - (amount * SCROLL_MULTIPLIER), totalHeight - height));
    }

    private void setScrollOffset(double scrollOffset) {
        this.scrolledOffset = Math.max(0, scrollOffset);
        positionButtonOffset(this.scrolledOffset);
    }
}
