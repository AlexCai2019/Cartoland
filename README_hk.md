# Cartoland Bot
#### [English](https://github.com/AlexCai2019/Cartoland/blob/master/README.md) / [台灣正體](https://github.com/AlexCai2019/Cartoland/blob/master/README_tw.md) / [台語文字](https://github.com/AlexCai2019/Cartoland/blob/master/README_ta.md) / 粵語漢字 / [简体中文](https://github.com/AlexCai2019/Cartoland/blob/master/README_cn.md)

## 簡介
屬於名為創世聯邦的Discord伺服器的Cartoland Bot的原始碼。透過此連結加入創世聯邦：https://discord.gg/UMYxwHyRNE

## 必備的資料夾和檔案
為咗節省效能，本機器人並唔會特別檢查你嘅資料夾同檔案是否符合條件。因此，你必須準備好以下嘅資料夾同檔案，先可以令機器人正常運作：
- 📁`lang/`，以及本專案的 `lang/` 資料夾內的所有 `.json` 檔案。
- 📁`logs/`
- 📄`users.json`，以及一對大括號作為內容。
- 📄`command_blocks.json`，以及一對大括號作為內容。

## 啟動
透過在終端機輸入以下的指令啟動機器人：
```
java -jar Cartoland.jar <權杖>
```
用你自己的機器人的權杖取代 `<權杖>` 引數。務必確保你有必備的資料夾和檔案。