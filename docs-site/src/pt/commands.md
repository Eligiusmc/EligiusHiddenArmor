# Commands & Permissions

## Commands & Permissions Table

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/eha toggle` | Toggles your entire armor visibility. | `eligiushiddenarmor.toggle` |
| `/eha toggle <piece>` | Toggles a specific armor piece (helmet, chestplate, leggings, boots). | `eligiushiddenarmor.toggle` |
| `/eha toggle <piece> <player>` | Toggles armor visibility for another player. | `eligiushiddenarmor.toggle.other` |
| `/eha hide [player]` | Explicitly hides the armor. | `eligiushiddenarmor.toggle` / `.other` |
| `/eha show [player]` | Explicitly shows the armor. | `eligiushiddenarmor.toggle` / `.other` |
| `/eha reload` | Reloads the configuration files. | `eligiushiddenarmor.reload` |

## Additional Permission Details

By default, the `config.yml` has a `default-permissions` section that automatically grants the `toggle` permission to all players without needing a permissions manager like LuckPerms. You can change this behavior in the config.
