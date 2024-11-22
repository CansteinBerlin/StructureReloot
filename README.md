# Structure Reloot

*This plugin is tested for minecraft versions 1.19.x, 1.20.x, 1.21.x*

A plugin for automatically resettings loot blocks like chests/dispensers/suspicious gravel/sand as well as lootentities
like chest minecars/elytra item frames. Intended for the usage on a server with a fixed worldboarder that relies on
these blocks/entities to be present.

## Commands

To execute any of the commands you need the base permission `structurereloot.command.reloot`

| Command                                                  | Description                                                                                                        | Permission                              |
|----------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| /reloot info (world)                                     | Displays an ingame and clickable view of the settings for that specific world.                                     | `structurereloot.command.info`          |
| /reloot listLootables <block/entity> (world) (page)      | Lists all Relootable blocks/entities in the world                                                                  | `structurereloot.command.listLootables` |
| /reloot regen <block/entity> <amount> (world)            | Regenerates a certain amount of blocks/entities in the specific world                                              | `structurereloot.command.regen`         |
| /reloot reloadConfig <configName> (override: true/false) | Reloads the specified config. If override is true the config will be completely overridden with the default config | `structurereloot.command.reloadConfig`  |
| /reloot reset <block/entity> (world)                     | Resets all the blocks/entities in the specific world                                                               | `structurereloot.command.reset`         |
| /reloot internal <subcommand>                            | This command in normaly hidden, as it is used for all the clickable commands.                                      | `structurereloot.command.internal`      |

#### Settings

![image](https://github.com/CansteinBerlin/StructureReloot/assets/75828222/5dc693ff-981e-40d9-a9dd-929127c40d02)

#### Lootable List

![image](https://github.com/CansteinBerlin/StructureReloot/assets/75828222/8d896bc3-620f-4955-b2eb-100b0154b1de)

## Why use this plugin and not the `auto-replenish` option provided by paper?
This plugin offers many practical functions for regenerating chests and entities that are not possible with the method provided in the paper configuration. 
This includes the regeneration of entities in general, as well as the ability to regenerate blocks at any time with just one command. 
Another advantage of this plugin is the ability to view/teleport/remove looted blocks/entities at any time.
