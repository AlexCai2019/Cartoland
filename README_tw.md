<p align="center">
  <img src="https://cdn.discordapp.com/attachments/889200718886608966/1080592685473218621/image.png" alt="banner">
</p>

# Cartoland Bot
#### [English](https://github.com/AlexCai2019/Cartoland/blob/master/README.md) / 台灣正體 / [台語文字](https://github.com/AlexCai2019/Cartoland/blob/master/README_ta.md) / [粵語漢字](https://github.com/AlexCai2019/Cartoland/blob/master/README_hk.md) / [简体中文](https://github.com/AlexCai2019/Cartoland/blob/master/README_cn.md)

![線上成員](https://discord.com/api/guilds/886936474723950603/widget.png)

## 簡介
屬於名為創世聯邦的Discord伺服器的Cartoland Bot的原始碼。透過此連結加入創世聯邦：https://discord.gg/UMYxwHyRNE

## 必備的資料夾和檔案
為了節省效能，本機器人並沒有檢查必要的資料夾和檔案是否存在。因此，你必須準備好以下的資料夾和檔案，才能讓機器人正常運作：
- 📁`lang/`，以及本專案的 `lang/` 資料夾內的所有 `.json` 檔案。
- 📁`logs/`
- 📁`serialize/`，以及下列檔案：
  - 📄`all_members.ser`，從一個值為`Long`的`HashSet`串聯化而來。
  - 📄`birthday_array.ser`，從一個值為`Long`的`ArrayList[]`串聯化而來。
  - 📄`birthday_map.ser`，從一個索引為`Long`、值為`Short`的`HashMap`串聯化而來。
  - 📄`has_start_message.ser`，從一個值為`Long`的`HashSet`串聯化而來。
  - 📄`idled_questions.ser`，從一個值為`Long`的`HashSet`串聯化而來。
  - 📄`introduction.ser`，從一個索引為`Long`、值為`String`的`HashMap`串聯化而來。
  - 📄`lottery_data.ser`，從一個索引為`Long`、值為`cartoland.utilties.CommandBlocksHandle.LotteryData`的`HashMap`串聯化而來。
  - 📄`temp_ban_list.ser`，從一個值為`long[]`的`HashSet`串聯化而來。
  - 📄`users.ser`，從一個索引為`Long`、值為`String`的`HashMap`串聯化而來。

## 啟動
透過在終端機輸入以下的指令啟動機器人：
```
java -jar Cartoland.jar <權杖>
```
用你自己的機器人的權杖取代 `<權杖>` 引數。務必確保你有必備的資料夾和檔案。