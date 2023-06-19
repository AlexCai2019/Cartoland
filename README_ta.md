<p align="center">
  <img src="https://cdn.discordapp.com/attachments/889200718886608966/1080592685473218621/image.png" alt="banner">
</p>

# Cartoland Bot
#### [English](https://github.com/AlexCai2019/Cartoland/blob/master/README.md) / [台灣正體](https://github.com/AlexCai2019/Cartoland/blob/master/README_tw.md) / 台語文字 / [粵語漢字](https://github.com/AlexCai2019/Cartoland/blob/master/README_hk.md) / [简体中文](https://github.com/AlexCai2019/Cartoland/blob/master/README_cn.md)

<img src="https://discord.com/api/guilds/886936474723950603/widget.png">

## 簡介
屬於名為創世聯邦的Discord伺服器的Cartoland Bot的原始碼。透過此連結加入創世聯邦：https://discord.gg/UMYxwHyRNE

## 必備的資料匣仔佮檔案
為著抾拾效能，本機器人並無檢查必要的資料匣仔佮檔案是無存在。就按呢，你定著著備辦以下的資料匣仔佮檔案，才會使予機器人正常運作：
- 📁`lang/`，以及本專案的 `lang/` 資料匣仔內底的所有 `.json` 檔案。
- 📁`logs/`
- 📁`serialize/`，以及下列檔案：
  - 📄`users.ser`，從一个索引是`Long`、值是`String`的`HashMap`串聯化而來。
  - 📄`name_and_blocks.ser`，從一个索引是`Long`、值是`cartoland.utilties.CommandBlocksHandle.LotteryData`的`HashMap`串聯化而來。
  - 📄`idled_questions.ser`，從一个值是`Long`的`HashSet`串聯化而來。
  - 📄`has_start_message.ser`，從一个值是`Long`的`HashSet`串聯化而來。
  - 📄`introduction.ser`，從一个索引是`Long`、值是`String`的`HashMap`串聯化而來。
  - 📄`all_members.ser`，從一个值是`Long`的`HashSet`串聯化而來。

## 啟動
透過佇終端機輸入以下的指令啟動機器人：
```
java -jar Cartoland.jar <權杖>
```
用你家己的機器人的權杖取代 `<權杖>` 引數。千萬確保你有必備的資料匣仔佮檔案。