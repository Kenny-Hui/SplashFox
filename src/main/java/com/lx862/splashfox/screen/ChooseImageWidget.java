package com.lx862.splashfox.screen;

import com.lx862.splashfox.SplashFox;
import com.lx862.splashfox.config.Config;
import com.lx862.splashfox.data.CustomResourceTexture;
import com.lx862.splashfox.screen.widget.ChooseButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChooseImageWidget extends ClickableWidget {
    private static final int SCROLL_MULTIPLIER = 8;
    private static final int BUTTON_Y_MARGIN = 20;
    private static final int BUTTON_SIZE = 40;
    private final List<ChooseButton> subWidgets;
    private final Consumer<ButtonWidget> addDrawableChild;
    private final String initialSelection;
    private final Config sessionInstance;
    private final TextRenderer textRenderer;
    private double scrolledOffset = 0;
    private int totalHeight = 0;
    private int customImageSeparatorY;

    public ChooseImageWidget(Consumer<ButtonWidget> addSelectableChild, String initialSelection, Config sessionInstance) {
        super(50, 50, 50, 50, Text.literal(""));
        this.initialSelection = initialSelection;
        this.addDrawableChild = addSelectableChild;
        this.sessionInstance = sessionInstance;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        subWidgets = new ArrayList<>();
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
        } catch (Exception ex) {
            ex.printStackTrace();
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
        Identifier id = custom ? sessionInstance.getCustomImageIdentifier(fileName) : Identifier.of("splashfox", "textures/gui/" + fileName);
        if(!custom) {
            MinecraftClient.getInstance().getTextureManager().getTexture(id).bindTexture();
        } else {
            MinecraftClient.getInstance().getTextureManager().registerTexture(id, new CustomResourceTexture(fileName, id));
        }

        ChooseButton chooseButton = new ChooseButton(0, 0, BUTTON_SIZE, BUTTON_SIZE, initialSelection.equals(custom ? fileName : id.toString()), id, e -> {
            sessionInstance.imagePath = custom ? null : id.toString();
            sessionInstance.customPath = custom ? fileName : null;
            for(ChooseButton btn : subWidgets) {
                btn.setSelected(false);
            }
            ((ChooseButton)e).setSelected(true);
        }, Text.literal(fileNameNoExtension));

        chooseButton.setTooltip(Tooltip.of(Text.literal(custom ? "Custom: " + fileNameNoExtension : fileNameNoExtension)));
        return chooseButton;
    }

    private void positionButtonOffset(double offset) {
        for(ChooseButton button : subWidgets) {
            button.setYOffset((int)-offset);
        }
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());

        for(ButtonWidget button : subWidgets) {
            final boolean buttonInVisibleArea = mouseY >= 30 && mouseY <= MinecraftClient.getInstance().getWindow().getScaledHeight() - 40;
            button.render(context, mouseX, mouseY, delta);
            button.active = buttonInVisibleArea;
        }

        context.getMatrices().push();
        context.getMatrices().translate(getX(), getY() - scrolledOffset, 0);
        Text customImageText = Text.translatable("splashfox.gui.custom_img");
        context.drawTextWithShadow(textRenderer, customImageText, 0, customImageSeparatorY, 0xFFFFFF);
        context.fill(textRenderer.getWidth(customImageText) + 4, customImageSeparatorY + (textRenderer.fontHeight / 2), getWidth(), customImageSeparatorY + (textRenderer.fontHeight / 2) + 1, 0xFFAAAAAA);
        context.getMatrices().pop();

        context.disableScissor();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle the key press first, so the new element that get selected is reflected before we do our check
        boolean bl = super.keyPressed(keyCode, scanCode, modifiers);

        for(ChooseButton chooseButton : subWidgets) {
            if(chooseButton.isSelected()) {
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
