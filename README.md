# 🧍‍♂️ 彭铁林模组 (PTL Mod)

![Fabric](https://img.shields.io/badge/Loader-Fabric-beige?style=flat&logo=fabric)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21%2B-green)
![AI-Powered](https://img.shields.io/badge/AI-Gemini%20Integrated-blue)

**在 Minecraft 中添加生物“彭铁林”，以及独特的“大便”循环生态系统。现在，他甚至接入了 Google Gemini AI，可以和你进行真实的对话！**

---

## ✨ 核心特色 (Features)

### 🤖 1. AI 智能对话 (AI Chat)
* **拥有灵魂**：彭铁林不再是一个只会乱走的 NPC。他拥有独立的人格（男大学生、健身狂魔、游戏宅）。
* **自由交互**：右键点击他，然后在聊天栏输入任何你想说的话，他都会根据人设进行回复。
* **语音反馈**：回复时会伴随独特的音效。

### 🥚 2. 新生物：彭铁林
* **自然生成**：在主世界的**平原**和**草原**群系自动刷新。
* **独家定制**：拥有独立建模、高清皮肤以及自定义语音（闲逛与受伤音效）。
* **完整动画**：包含行走的腿部摆动与手臂挥动动画。

### 💩 3. 核心机制：大便 (Poop)
* **自动生产**：成年彭铁林每隔 5~10 分钟会随机掉落一个 **“大便”** 物品。
* **繁殖诱饵**：手持大便可吸引彭铁林跟随。
* **繁衍后代**：给两只彭铁林喂食大便，可繁殖出**迷你彭铁林 (Baby)**。

---

## ⚙️ AI 功能配置指南 (AI Configuration)

**⚠️ 注意：要使用对话功能，你必须配置 Google Gemini API Key。**

### 第一步：获取 API Key
1. 前往 [Google AI Studio](https://aistudio.google.com/app/apikey)。
2. 登录 Google 账号并点击 **Create API key**。
3. 复制以 `AIza` 开头的密钥字符串。

### 第二步：生成配置文件
1. 启动一次游戏并进入主界面（或直接运行模组）。
2. 关闭游戏。
3. 前往你的 `.minecraft/config/` 目录，找到 **`ptl_config.json`** 文件。

### 第三步：填入配置
用记事本打开 `ptl_config.json`，填入你的 Key 和代理端口（国内用户必备）：

```json
{
  "apiKey": "把你的_AIza_开头的Key_粘贴在这里",
  "proxyPort": 7890,
  "modelName": "gemini-1.5-flash"
}···

## 🛠️ 前置需求 (Requirements)

* **Minecraft** 1.21+
* **Fabric Loader**
* **Fabric API**
