package com.lx862.splashfox.screen;

import com.lx862.splashfox.SplashFox;
import com.lx862.splashfox.screen.widget.ChooseButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChooseImageScreen extends Screen {
    private static final double SCREEN_WIDTH_FACTOR = 0.75;
    private static final int SCROLL_MULTIPLIER = 8;
    private static final int BUTTON_Y_MARGIN = 20;
    private static final int BUTTON_SIZE = 40;
    private final Consumer<Identifier> callback;
    private final List<ChooseButton> iconButtons;
    private final Screen parentScreen;
    private Identifier selectedPath;
    private double scrolledOffset = 0;
    private int totalHeight = 0;
    private final ButtonWidget cancelButton;

    public ChooseImageScreen(Screen parentScreen, Identifier initialPath, Consumer<Identifier> callback) {
        super(Text.literal("Choose an image"));
        this.selectedPath = initialPath;
        this.callback = callback;
        this.parentScreen = parentScreen;
        cancelButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.cancel"), (btn) -> {
            close();
        }).size(75, 20).build();
        iconButtons = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();

        cancelButton.setX(client.getWindow().getScaledWidth() - cancelButton.getWidth());
        addDrawableChild(cancelButton);

        try {
            iconButtons.clear();
            URI localResource = SplashFox.class.getResource("/assets/splashfox/textures/gui/").toURI();
            Path localResourcePath = Path.of(localResource);
            Files.list(localResourcePath).forEach(filePath -> {
                String fileName = filePath.getFileName().toString();
                String fileNameNoExtension = FilenameUtils.removeExtension(fileName);
                Identifier textureID = new Identifier("splashfox", "textures/gui/" + fileName);
                MinecraftClient.getInstance().getTextureManager().getTexture(textureID).bindTexture();

                ChooseButton chooseButton = new ChooseButton(0, 0, BUTTON_SIZE, BUTTON_SIZE, selectedPath.equals(textureID), textureID, e -> {
                    selectedPath = textureID;
                    close();
                }, Text.literal(fileNameNoExtension));

                chooseButton.setTooltip(Tooltip.of(Text.literal(fileNameNoExtension)));
                iconButtons.add(chooseButton);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        double fullScreenWidth = client.getWindow().getScaledWidth() * SCREEN_WIDTH_FACTOR;
        int startX = (int)(client.getWindow().getScaledWidth() - fullScreenWidth) / 2;
        int x = 0;
        int y = 0;

        for (ChooseButton button : iconButtons) {
            int nextX = x + button.getWidth();
            if (nextX > fullScreenWidth) {
                x = 0;
                y += button.getHeight() + BUTTON_Y_MARGIN;
            }
            button.setX(startX + x);
            button.setY(y);
            x += button.getWidth() + BUTTON_Y_MARGIN;
            addDrawableChild(button);
        }

        totalHeight = y + BUTTON_SIZE;
    }

    private void positionButtonOffset(double offset) {
        for(ChooseButton button : iconButtons) {
            button.setYOffset((int)-offset);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double scrollAmount) {
        scrollRelative(scrollAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, scrollAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle the key press first, so the new element that get selected is reflected before we do our check
        boolean bl = super.keyPressed(keyCode, scanCode, modifiers);

        for(ChooseButton chooseButton : iconButtons) {
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

    @Override
    public void close() {
        this.client.setScreen(parentScreen);
        callback.accept(selectedPath);
    }

    private void scrollRelative(double amount) {
        setScrollOffset(Math.min(scrolledOffset - (amount * SCROLL_MULTIPLIER), totalHeight - height));
    }

    private void setScrollOffset(double scrollOffset) {
        this.scrolledOffset = Math.max(0, scrollOffset);
        positionButtonOffset(this.scrolledOffset);
    }
}
