![Banner](Banner.jpg)

# Cartoland Bot
#### English / [台灣正體](https://github.com/AlexCai2019/Cartoland/blob/master/readme/README_tw.md) / [台語文字](https://github.com/AlexCai2019/Cartoland/blob/master/readme/README_ta.md) / [粵語漢字](https://github.com/AlexCai2019/Cartoland/blob/master/readme/README_hk.md) / [简体中文](https://github.com/AlexCai2019/Cartoland/blob/master/readme/README_cn.md)

![Online Members](https://discord.com/api/guilds/886936474723950603/widget.png)

## Description
The source code of Cartoland Bot, a discord bot developed for a server named Cartoland. Join Cartoland here: https://discord.gg/UMYxwHyRNE

## Required folders and files
For performance reasons, this bot won't verify if all the necessary paths and files exist. If you want to run the bot yourself, you must have the following folders and files:
- 📁`dms/`
- 📁`lang/` with all `.json` files found in the `lang/` folder of this repository.
- 📁`logs/`
- 📁`serialize/` with these following files:
  - 📄`all_members.ser` serialized from a `HashSet` which use `Long` as value.
  - 📄`birthday_map.ser` serialized from a `HashMap` which use `Long` as key and `cartoland.utilities.TimerHandle.Birthday` as value.
  - 📄`introduction.ser` serialized from a `HashMap` which use `Long` as key and `String` as value.
  - 📄`lottery_data.ser` serialized from a `HashMap` which use `Long` as key and `cartoland.utilities.CommandBlocksHandle.LotteryData` as value.
  - 📄`private_to_underground.ser` serialized from a `HashMap` which use `Long` as key and value.
  - 📄`scheduled_events.ser` serialized from a `HashMap` which use `String` as key and `cartoland.utilities.TimerHandle.TimerEvent` as value.
  - 📄`temp_ban_list.ser` serialized from a `HashSet` which use `cartoland.commands.AdminCommand.BanData` as value.
  - 📄`unresolved_questions.ser` serialized from a `HashSet` which use `Long` as value.
  - 📄`users.ser` serialized from a `HashMap` which use `Long` as key and `String` as value.

## Launching
Start the bot by running the following command in your terminal:
```
java -jar Cartoland.jar <token>
```
Replace the `<token>` argument with the token of your own bot. Make sure you have all the required folders and files before launching.