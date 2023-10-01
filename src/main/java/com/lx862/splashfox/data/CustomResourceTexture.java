package com.lx862.splashfox.data;

import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public class CustomResourceTexture extends ResourceTexture {
    public CustomResourceTexture(Identifier location) {
        super(location);
    }

    protected TextureData loadTextureData(ResourceManager resourceManager) {
        try {
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/" + location.getNamespace() + "/" + location.getPath());
            TextureData texture = null;

            if(input != null) {
                try {
                    texture = new TextureData(new TextureResourceMetadata(true, true), NativeImage.read(input));
                } finally {
                    input.close();
                }
            }

            return texture;
        } catch (IOException exception) {
            return new TextureData(exception);
        }
    }
}
