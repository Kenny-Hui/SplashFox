package com.lx.splashfox.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lx.splashfox.Data.Position;
import com.lx.splashfox.SplashFox;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class Config {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("splashfox");
    public double dropHeight = 1.5;
    public double foxSize = 1.5;
    public double speed = 1;
    public boolean flipped = false;
    public boolean wobbly = true;
    public String imagePath = "splashfox:textures/gui/blobfox.png";
    public Position position = Position.ABOVE_MOJANG;

    public static Config readConfig() {
        Path configFile = CONFIG_PATH.resolve("config.json");
        if(Files.exists(configFile)) {
            SplashFox.LOGGER.info("[SplashFox] Reading Config...");
            try {
                return new Gson().fromJson(Files.readString(configFile), Config.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            SplashFox.LOGGER.info("[SplashFox] Config not found, generating one...");
            writeConfig(new Config());
        }
        return new Config();
    }

    public static void writeConfig(Config instance) {
        Gson gson = new Gson();
        final JsonElement element = gson.toJsonTree(instance);
        try {
            CONFIG_PATH.toFile().mkdirs();
            Files.write(CONFIG_PATH.resolve("config.json"), Collections.singleton(new GsonBuilder().setPrettyPrinting().create().toJson(element)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
