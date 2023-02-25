# Cartoland Bot
#### [English](https://github.com/AlexCai2019/Cartoland/blob/master/README.md) / 繁體中文 / [简体中文](https://github.com/AlexCai2019/Cartoland/blob/master/README_zh-Hans.md)

#簡介
屬於名為創世聯邦的Discord伺服器的Cartoland Bot的原始碼。 透過此連結加入創世聯邦：https://discord.gg/UMYxwHyRNE

## 必備的資料夾和檔案
為了節省效能，本機器人並沒有檢查必要的資料夾和檔案是否存在。因此，你必須準備好以下的資料夾和檔案，才能讓機器人正常運作：
- 📁`lang/`，以及本專案的 `lang/` 資料夾內的所有 `.json` 檔案。
- 📁`logs/`
- 📄`users.json`，以及一對大括號做為內容。
- 📄`command_blocks.json`，以及一對大括號做為內容。

## 啟動
透過在終端機輸入以下的指令啟動機器人：
```
java -jar Cartoland.jar <權杖>
```
用你自己的機器人的權杖取代 `<權杖>` 引數。務必確保你有必備的資料夾和檔案。