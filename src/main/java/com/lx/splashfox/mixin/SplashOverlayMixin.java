package com.lx.splashfox.mixin;

import com.lx.splashfox.Data.Position;
import com.lx.splashfox.Data.EmptyTexture;
import com.lx.splashfox.SplashFox;
import com.lx.splashfox.render.FoxRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {
	@Shadow private long reloadCompleteTime;
	@Shadow private long reloadStartTime;
	@Shadow @Final private boolean reloading;
	@Shadow @Final private MinecraftClient client;
	@Shadow @Final
	static Identifier LOGO;
	private double elapsed;
	private FoxRenderer renderer;

	@Inject(at = @At("HEAD"), method = "init", cancellable = true)
	private static void init(MinecraftClient client, CallbackInfo ci) {
		// Init is only called once when
		if(SplashFox.config.position == Position.REPLACE_MOJANG) {
			client.getTextureManager().registerTexture(LOGO, new EmptyTexture(LOGO));
			ci.cancel();
		}
	}

	@Inject(at = @At("TAIL"), method = "render")
	private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if(renderer == null) renderer = new FoxRenderer();
		elapsed += delta;
		renderer.render(this.client, matrices, SplashFox.config.position, SplashFox.config, mouseX, mouseY, elapsed, getOverlayAlpha());
	}

	private float getOverlayAlpha() {
		long l = Util.getMeasuringTimeMs();
		float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0F : -1.0F;
		float g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0F : -1.0F;

		if (f >= 1.0F) {
			return 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
		} else if (this.reloading) {
			return MathHelper.clamp(g, 0.0F, 1.0F);
		} else {
			return 1.0F;
		}
	}
}