package com.ptl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PtlConfig {

    // 配置文件路径：.minecraft/config/ptl_config.json
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("ptl_config.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 单例实例
    private static ConfigData instance = new ConfigData();

    // ==========================================
    // 配置数据结构
    // ==========================================
    public static class ConfigData {
        // 你的 API Key (默认是空的，提示用户去填)
        public String apiKey = "在此处填入你的_AIza_开头的Key";

        // 代理端口 (默认 7890，方便你修改)
        public int proxyPort = 7890;

        // AI 模型名称 (方便以后升级)
        public String modelName = "gemini-1.5-flash";
    }

    // ==========================================
    // 加载配置
    // ==========================================
    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                instance = GSON.fromJson(reader, ConfigData.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            save(); // 如果文件不存在，就创建一个默认的
        }
    }

    // ==========================================
    // 保存配置
    // ==========================================
    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取当前配置
    public static ConfigData get() {
        return instance;
    }
}