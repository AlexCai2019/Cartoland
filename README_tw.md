<p align="center">
  <img src="https://cdn.discordapp.com/attachments/889200718886608966/1080592685473218621/image.png" alt="banner">
</p>

# Cartoland Bot
#### [English](https://github.com/AlexCai2019/Cartoland/blob/master/README.md) / 台灣正體 / [台語文字](https://github.com/AlexCai2019/Cartoland/blob/master/README_ta.md) / [粵語漢字](https://github.com/AlexCai2019/Cartoland/blob/master/README_hk.md) / [简体中文](https://github.com/AlexCai2019/Cartoland/blob/master/README_cn.md)

## 簡介
屬於名為創世聯邦的Discord伺服器的Cartoland Bot的原始碼。透過此連結加入創世聯邦：https://discord.gg/UMYxwHyRNE

## 必備的資料夾和檔案
為了節省效能，本機器人並沒有檢查必要的資料夾和檔案是否存在。因此，你必須準備好以下的資料夾和檔案，才能讓機器人正常運作：
- 📁`lang/`，以及本專案的 `lang/` 資料夾內的所有 `.json` 檔案。
- 📁`logs/`
- 📄`users.ser`，從一個索引為`Long`、值為`String`的`HashMap`串聯化而來。
- 📄`command_blocks.ser`，從一個索引和值皆為`Long`的`HashMap`串聯化而來。
- 📄`idled_questions.ser`，從一個值為`Long`的`HashSet`串聯化而來。

## 啟動
透過在終端機輸入以下的指令啟動機器人：
```
java -jar Cartoland.jar <權杖>
```
用你自己的機器人的權杖取代 `<權杖>` 引數。務必確保你有必備的資料夾和檔案。