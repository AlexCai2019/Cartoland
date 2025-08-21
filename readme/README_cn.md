![Banner](Banner.jpg)

# Cartoland Bot
#### [English](https://github.com/AlexCai2019/Cartoland/blob/master/readme/README.md) / [台灣正體](https://github.com/AlexCai2019/Cartoland/blob/master/readme/README_tw.md) / [台語文字](https://github.com/AlexCai2019/Cartoland/blob/master/readme/README_ta.md) / [粵語漢字](https://github.com/AlexCai2019/Cartoland/blob/master/readme/README_hk.md) / 简体中文

![在线成员](https://discord.com/api/guilds/886936474723950603/widget.png)

## 简介
属于名为创世联邦的Discord服务器的Cartoland Bot的源代码。通过此连结加入创世联邦：https://discord.gg/UMYxwHyRNE

## 必备的文件夹和文件
为了节省效能，本机器人并没有检查必要的文件夹和文件是否存在。因此，你必须准备好以下的文件夹和文件，才能让机器人正常运作：
- 📄`config.properties` with properties of
  - `token`
  - `db.url`
  - `db.users`
  - `db.password`
- 📁`lang/`，以及本项目的 `lang/` 文件夹内的所有 `.json` 文件。
- 📁`logs/`

## 激活
通过在终端输入以下的命令激活机器人：
```
java -jar Cartoland.jar <令牌>
```
用你自己的机器人的令牌取代 `<令牌>` 参数。务必确保你有必备的文件夹和文件。