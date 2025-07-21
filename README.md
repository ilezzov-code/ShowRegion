<div align="center">
    <img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/logo/BannerRoundedEn.png" alt="">
    <h4>Supporting version: Paper: 1.18.2 — 1.21.x</h4>
    <h1>Showing regions in Action and Boss Bar</h1>
</div>

<div align="center" content="">
    <a href="https://modrinth.com/plugin/showregion">
        <img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/social/modrinth.svg" width="">
    </a>
</div>

### <a href="https://github.com/ilezzov-code/ShowRegion/blob/main/README_RU.md"><img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/flags/ru.svg" width=15px> Выбрать русский README.md</a>

##  <a>Table of contents</a>

- [About](#about)
- [Features](#features)
- [Config.yml](#config)
- [Commands](#commands)
- [Permissions](#permissions)
- [Links](#links)
- [Buy me coffee](#donate)
- [Report a bug](https://github.com/ilezzov-code/showregion/issues)


## <a id="about">About</a>

**ShowRegion** is a unique plugin that allows players to see which region they are in. The uniqueness lies in the fact that you can customize your message for each region!

## <a id="features">Features</a>

* [🔥] **Custom messages for specific regions** → [more details](#custom-messages)
* Supporting two languages: ru-RU (Russian), en-US (English)
* Displaying a free regions, Someone Else's region, and your own → [more details](#region-show)
* Displaying the name of the region and owners → [more details](#region-name-show)
* Enable / Disable Action Bar and Boss Bar via the command → [more details](#toggle-command)
* Setting the refresh rate
* Enable / Disable Action Bar and Boss Bar in config.yml

## <a id="config">Config.yml</a>

<details>
    <summary>Look config.yml</summary>

```yaml
#  ░██████╗██╗░░██╗░█████╗░░██╗░░░░░░░██╗██████╗░███████╗░██████╗░██╗░█████╗░███╗░░██╗
#  ██╔════╝██║░░██║██╔══██╗░██║░░██╗░░██║██╔══██╗██╔════╝██╔════╝░██║██╔══██╗████╗░██║
#  ╚█████╗░███████║██║░░██║░╚██╗████╗██╔╝██████╔╝█████╗░░██║░░██╗░██║██║░░██║██╔██╗██║
#  ░╚═══██╗██╔══██║██║░░██║░░████╔═████║░██╔══██╗██╔══╝░░██║░░╚██╗██║██║░░██║██║╚████║
#  ██████╔╝██║░░██║╚█████╔╝░░╚██╔╝░╚██╔╝░██║░░██║███████╗╚██████╔╝██║╚█████╔╝██║░╚███║
#  ╚═════╝░╚═╝░░╚═╝░╚════╝░░░░╚═╝░░░╚═╝░░╚═╝░░╚═╝╚══════╝░╚═════╝░╚═╝░╚════╝░╚═╝░░╚══╝

# Developer / Разработчик: ILeZzoV

# Socials / Ссылки:
# • Contact with me / Связаться: https://t.me/ilezovofficial
# • Telegram Channel / Телеграм канал: RUS: https://t.me/ilezzov
# • GitHub: https://github.com/ilezzov-code

# By me coffee / Поддержать разработчика:
# • DA: https://www.donationalerts.com/r/ilezov
# • YooMoney: https://yoomoney.ru/to/4100118180919675
# • Telegram Gift: https://t.me/ilezovofficial
# • TON: UQCInXoHOJAlMpZ-8GIHqv1k0dg2E4pglKAIxOf3ia5xHmKV
# • BTC: 1KCM1QN9TNYRevvQD63UF81oBRSK67vCon
# • Card: 5536914188326494

# Supporting messages languages / Доступные языки сообщений:
# en-US, ru-RU
language: "ru-RU"

# Check the plugin for updates
# Проверять плагин на наличие обновлений
check_updates: true

showing_settings:
  # Should I enable the display of the name of the regions to all new players at the entrance
  # Включить ли отображение имени регионов всем новым игрокам при входе
  default_enable: true
  # Enable Boss Bar
  # Включить Bossbar
  enable_boss_bar: true
  # Enable Action Bar
  # Включить ActionBar
  enable_action_bar: true
  # How many region owners display in placeholder {REGION_NAME}
  # Количество владельцев в плейсхолдере {REGION_OWNER}
  owner_count: 3
  # Tick rate tp update in ticks
  # Частота обновления в тиках
  tick_rate: 1

# Don't edit this / Не редактируйте это
config_version: 1.0
```

</details>

## <a id = "region-show">Displaying a free regions, foreign region, and your own</a>

The plugin displays each type of region: The Free Region, Your region, and Someone Else's region. All messages are configured in the file [`region_settings.yml`](src/main/resources/region_settings.yml)

Examples:

<img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/gifs/your_region.gif" width="400">
<img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/gifs/foreign_region.gif" width="400">

## <a id = "region-name-show">Displaying the name of the region and owners</a>

The plugin supports its own placeholders, which you can add to each message.

* {REGION_NAME} — Name of region
* {REGION_OWNER} — Name of region's owner

And here's what it will look like:

<img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/gifs/foreign_region_and_name.gif" width="400">

## <a id = "custom-messages">[🔥] Custom messages for specific regions</a>

You can set custom messages for specific regions in file [`region_settings.yml`](src/main/resources/region_settings.yml)

Examples:

<details>

<summary>region_settings.yml</summary>

```yml
# Custom regions list
# Список кастомных регионов
custom_regions:
  # Region name (in WorldGuard)
  # Название региона (в WorldGuard)
  spawn:
    # Message for Action Bar
    # Сообщение для Action Bar
    action_bar: "&7Вы находитесь на &6спавне"
    # Boss Bar settings
    # Настройка BossBar
    boss_bar:
      # Displaying text
      # Отображаемый текст
      text: "&7Вы находитесь на &6спавне"
      # Progress [0.0 ; 1.0]
      # Прогресс [0.0 ; 1.0]
      progress: 1.0
      # Boss Bar color. Supporting colors:
      # Цвет полоски. Доступные цвета:
      # PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
      color: YELLOW
      # Overlay. Supporting overlays:
      # Оверлей. Доступные оверлеи
      # PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20
      overlay: PROGRESS

  pvp_arena:
    action_bar: "&7Вы находитесь на &cPVP-арене"
    boss_bar:
      text: "&7Вы находитесь на &cPVP-арене"
      progress: 1.0
      color: RED
      overlay: PROGRESS
```
</details>

Result:

<img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/gifs/custom_names_2.gif" width="400">
<img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/gifs/custom_names_1.gif" width="400">

## <a id = "toggle-command">Enable / Disable Action Bar and Boss Bar via the command</a>

Each player can customize the display of regions individually for themselves: Enable / Disable Action Bar or Boss Bar

Enable / Disable all elements (command `/sr toggle`)

<img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/gifs/toggle_cmd.gif" width="400">

Enable / Disable Action Bar (command `/sr toggle actionbar`)

<img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/gifs/toggle_actonbar_cmd.gif" width="400">

Enable / Disable Boss Bar (command `/sr toggle bossbar`)

<img src="https://raw.githubusercontent.com/ilezzov-code/ShowRegion/refs/heads/main/img/gifs/toggle_bossbar_cmd.gif" width="400">


## <a id="commands">Command (/command → /args1, /args2, ... ※ `permission`)</a>

### /showregion reload → /sr reload ※ `showregion.reload`

* Reload the plugin configuration

### /showregion version → /sr version ※ ``

* Check for updates

### /showregion toggle → /sr toggle ※ `showregion.toggle`

* Enable / Disable Boss Bar and Action Bar

### /showregion toggle actionbar → /sr toggle actionbar ※ `showregion.toggle.actionbar`

* Enable / Disable Action Bar

### /showregion toggle bossbar → /sr toggle bossbar ※ `showregion.toggle.bossbar`

* Enable / Disable Boss Bar

## <a id="permissions">All plugin permissions</a>

| Permission                  | About                                          |
|-----------------------------|------------------------------------------------|
| showregion.*                | Access to all plug-in features                 |
| showregion.reload           | Access to reload the plugin /sr reload         |
| showregion.access.showing   | Access to the display of regions               |
| showregion.toggle           | Access to command /sr toggle                   |
| showregion.toggle.actionbar | Access to command /sr toggle actionbar         |
| showregion.toggle.bossbar   | Access to command /sr toggle bossbar           |
| showregion.toggle.*         | Access to command /sr toggle and its arguments |


## <a id="links">Links</a>

* Contact: https://t.me/ilezovofficial
* Telegram Channel: https://t.me/ilezzov
* Modrinth: https://modrinth.com/plugin/showregion

## <a id="donate">Buy me coffee</a>

* DA: https://www.donationalerts.com/r/ilezov
* YooMoney: https://yoomoney.ru/to/4100118180919675
* Telegram Gift: https://t.me/ilezovofficial
* TON: UQCInXoHOJAlMpZ-8GIHqv1k0dg2E4pglKAIxOf3ia5xHmKV
* BTC: 1KCM1QN9TNYRevvQD63UF81oBRSK67vCon
* Card: 5536914188326494

## Found an issue or have a question? Create a new issue — https://github.com/ilezzov-code/ShowRegion/issues/new

## <a id="license">License</a>

This project is distributed under the `GPL-3.0 License'. For more details, see the [LICENSE](LICENSE) file.
