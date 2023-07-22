<p align="center">
  <img src="https://cdn.discordapp.com/attachments/889200718886608966/1080592685473218621/image.png" alt="banner">
</p>

# Cartoland Bot
#### [English](https://github.com/AlexCai2019/Cartoland/blob/master/README.md) / [台灣正體](https://github.com/AlexCai2019/Cartoland/blob/master/README_tw.md) / [台語文字](https://github.com/AlexCai2019/Cartoland/blob/master/README_ta.md) / [粵語漢字](https://github.com/AlexCai2019/Cartoland/blob/master/README_hk.md) / 简体中文

<img src="https://discord.com/api/guilds/886936474723950603/widget.png">

## 简介
属于名为创世联邦的Discord服务器的Cartoland Bot的源代码。通过此连结加入创世联邦：https://discord.gg/UMYxwHyRNE

## 必备的文件夹和文件
为了节省效能，本机器人并没有检查必要的文件夹和文件是否存在。因此，你必须准备好以下的文件夹和文件，才能让机器人正常运作：
- 📁`lang/`，以及本项目的 `lang/` 文件夹内的所有 `.json` 文件。
- 📁`logs/`
- 📁`serialize/`，以及下列文件：
  - 📄`all_members.ser`，从一个键为`Long`的`HashSet`序列化而来。
  - 📄`birthday_array.ser`，从一个键为`Long`的`ArrayList[]`序列化而来。
  - 📄`birthday_map.ser`，从一个键为`Long`、值为`Short`的`HashMap`序列化而来。
  - 📄`has_start_message.ser`，从一个键为`Long`的`HashSet`序列化而来。
  - 📄`idled_questions.ser`，从一个键为`Long`的`HashSet`序列化而来。
  - 📄`introduction.ser`，从一个键为`Long`、值为`String`的`HashMap`序列化而来。
  - 📄`lottery_data.ser`，从一个键为`Long`、值为`cartoland.utilities.CommandBlocksHandle.LotteryData`的`HashMap`序列化而来。
  - 📄`temp_ban_list.ser`，从一个值为`long[]`的`HashSet`序列化而来。
  - 📄`users.ser`，从一个键为`Long`、值为`String`的`HashMap`序列化而来。

## 激活
通过在终端输入以下的命令激活机器人：
```
java -jar Cartoland.jar <令牌>
```
用你自己的机器人的令牌取代 `<令牌>` 参数。务必确保你有必备的文件夹和文件。