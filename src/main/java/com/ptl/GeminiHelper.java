package com.ptl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class GeminiHelper {

    // ❌ 不再写死 Key 和 URL
    // ✅ 改为动态构建 URL，每次调用都去配置里读最新的

    private static String getApiUrl() {
        String model = PtlConfig.get().modelName;
        String key = PtlConfig.get().apiKey;
        return "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + key;
    }

    public static void chat(String userMessage, Callback callback) {
        // 1. 检查 Key 是否填了
        String currentKey = PtlConfig.get().apiKey;
        if (currentKey == null || currentKey.contains("在此处填入") || currentKey.isEmpty()) {
            callback.onSuccess("（请先去 .minecraft/config/ptl_config.json 填入 API Key！）");
            return;
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("你现在扮演Minecraft模组生物'彭铁林'。设定：男大学生，爱健身爱打游戏，性格阳光带点憨。");
        prompt.append("模组特性：你每隔几分钟会掉落'大便'物品，且极度喜欢吃'大便'。");
        prompt.append("如果玩家手里拿着大便，你会很兴奋。说话简短有趣，多用游戏术语和健身梗。");
        prompt.append("玩家对你说：").append(userMessage);

        JsonObject part = new JsonObject();
        part.addProperty("text", prompt.toString());

        JsonArray parts = new JsonArray();
        parts.add(part);
        JsonObject content = new JsonObject();
        content.add("parts", parts);
        JsonArray contents = new JsonArray();
        contents.add(content);
        JsonObject payload = new JsonObject();
        payload.add("contents", contents);

        // 2. 使用配置里的代理端口
        int port = PtlConfig.get().proxyPort;

        HttpClient client = HttpClient.newBuilder()
                .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", port)))
                .build();

        // 3. 使用动态 URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getApiUrl()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        CompletableFuture.runAsync(() -> {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    try {
                        String reply = json.getAsJsonArray("candidates")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("content")
                                .getAsJsonArray("parts")
                                .get(0).getAsJsonObject()
                                .get("text").getAsString();
                        callback.onSuccess(reply.trim());
                    } catch (Exception parseEx) {
                        System.out.println("JSON 解析失败: " + response.body());
                        callback.onSuccess("（脑子有点乱...）");
                    }
                } else {
                    System.out.println("API Error: " + response.statusCode() + " | " + response.body());
                    callback.onSuccess("（连接错误: " + response.statusCode() + "，请检查配置）");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onSuccess("（连接超时，请检查代理端口是否为 " + port + "）");
            }
        });
    }

    public interface Callback {
        void onSuccess(String reply);
    }
}