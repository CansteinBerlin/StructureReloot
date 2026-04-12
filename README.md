# StructureReloot

*Tested on Minecraft 1.19.x, 1.20.x, 1.21.x*

StructureReloot is a Paper plugin that automatically tracks and regenerates loot blocks and loot entities across all worlds on your server. When a player opens a chest, breaks a suspicious block, or kills an elytra item frame, the plugin records that event and later restores the block or entity — complete with its original loot table, block type, and facing direction — on a fully configurable per-world schedule.

It is designed for servers with a fixed world border that rely on structure loot remaining available to players over time.

---

## Why use this instead of Paper's `auto-replenish`?

Paper's built-in option only replenishes chest inventories. StructureReloot goes further:

- Regenerates **entities** (chest minecarts, elytra item frames) in addition to blocks
- Supports **brushable blocks** (Suspicious Sand, Suspicious Gravel) and **decorated pots**
- Handles blocks destroyed by **explosions**
- Provides an **in-game clickable UI** to inspect, teleport to, manually regenerate, or remove individual lootables
- Lets you **manually trigger regeneration** at any time with a single command
- Gives you **per-world control** over the cycle duration, maximum regeneration count, and startup behaviour
- Keeps a **persistent activity log** of every add and remove operation

---

## How It Works

### Tracking

The plugin listens to game events to detect when a loot block or entity has been interacted with and records it to an SQLite database.

**Blocks** are recorded when:
- A player **opens** a loot container for the first time (`LootGenerateEvent`)
- A player **breaks** a loot container (`BlockBreakEvent`)
- An **explosion** destroys a loot container (`EntityExplodeEvent`)
- A player finishes **brushing** a Suspicious Sand/Gravel block (`BlockDropItemEvent`)
- A Suspicious block **falls** because the block below it was removed (`EntityChangeBlockEvent`)

**Entities** are recorded when:
- A player **opens** a chest minecart for the first time (`LootGenerateEvent`)
- An **elytra item frame** in the End is broken by a player (`EntityDamageByEntityEvent`) or by the environment (`HangingBreakEvent`)

> Item frames in the End are automatically **marked on chunk generation** so the plugin can distinguish structure item frames from player-placed ones.

### Regeneration

A background task runs every 60 seconds and checks each world's configured schedule. When it is time to regenerate, the plugin:

1. Picks a random subset of stored loot values up to the configured `maxRelootAmount`
2. Queues them for physical regeneration through the `LootValueProcessor`
3. Removes them from the database so they can be re-tracked when looted again

The `LootValueProcessor` spreads the physical world changes over multiple ticks (controlled by `changesPerTick` in `config.yml`) to avoid lag spikes on large regeneration cycles.

**Block regeneration** places the block back at its original location with its original material, facing direction, and loot table.

**Entity regeneration** spawns the entity at its original location. For item frames a support block is placed below if needed, and the elytra item is set automatically.

---

## Supported Lootables

### Blocks
Any block that implements Bukkit's `Lootable` interface is supported. This includes:

| Block                 | Notes                                         |
|-----------------------|-----------------------------------------------|
| Chest / Trapped Chest | Directional — facing is preserved             |
| Barrel                | Directional — facing is preserved             |
| Dispenser / Dropper   | Directional — facing is preserved             |
| Suspicious Sand       | Brushable block; loot table saved via NBT tag |
| Suspicious Gravel     | Brushable block; loot table saved via NBT tag |
| Decorated Pot         | Non-directional                               |

### Entities

| Entity            | Notes                                                                       |
|-------------------|-----------------------------------------------------------------------------|
| Chest Minecart    | Tracked when loot is first generated                                        |
| Elytra Item Frame | Only tracked in The End; must contain an elytra and be marked by the plugin |

---

### `blockUpdateSettings.yml` and `entityUpdateSettings.yml`

These files store the regeneration schedule for each world, one section per world name. Settings are created automatically for every loaded world and can be edited in-game via `/reloot info` or directly in the file.

```yaml
world:
  relootOnStartup: true       # Trigger a reloot cycle when the server starts
  maxRelootAmount: -1         # Max entries to regenerate per cycle. -1 = unlimited
  duration: 1h                # Time between automatic regeneration cycles
  nextReloot: "01.01.2025, 12:00:00"  # Managed automatically; do not edit manually
```

**Duration pattern format:** Combine any of `Xd` (days), `Xh` (hours), `Xm` (minutes), `Xs` (seconds). Examples: `1d`, `2h30m`, `90m`, `1d12h`.

---

## Commands and Permissions

All commands require the base permission `structurereloot.command.reloot`.

| Command                                                | Description                                                                                                                                                                     | Permission                              |
|--------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| `/reloot info (world)`                                 | Opens the interactive settings panel for a world. Clicking any value lets you change it in chat.                                                                                | `structurereloot.command.info`          |
| `/reloot listLootables <block\|entity> (world) (page)` | Paginated list of all tracked lootables in a world. Each entry has clickable **Teleport**, **Reloot**, and **Remove** buttons.                                                  | `structurereloot.command.listLootables` |
| `/reloot regen <block\|entity> <amount> (world)`       | Immediately regenerates the given number of random entries. Use `-1` to regenerate all.                                                                                         | `structurereloot.command.regen`         |
| `/reloot reset <block\|entity> (world)`                | Wipes all tracked blocks or entities from the database for that world without regenerating them.                                                                                | `structurereloot.command.reset`         |
| `/reloot remove <block\|entity> <radius>`              | Removes all tracked blocks or entities within `radius` blocks of your current position from the database.                                                                       | `structurereloot.command.remove`        |
| `/reloot reloadConfig <name> (override)`               | Reloads a config file. Pass `override: true` to delete the current file and restore the default. Config names: `config`, `lang`, `blockupdatesettings`, `entityupdatesettings`. | `structurereloot.command.reloadConfig`  |
| `/reloot internal ...`                                 | Hidden command used internally by the clickable chat UI. Not intended for direct use.                                                                                           | `structurereloot.command.internal`      |

### Permission Summary

| Permission                              | Description                                                  |
|-----------------------------------------|--------------------------------------------------------------|
| `structurereloot.command.reloot`        | Required to use any `/reloot` command                        |
| `structurereloot.command.info`          | View and edit per-world settings                             |
| `structurereloot.command.listLootables` | Browse tracked lootables with teleport/reloot/remove actions |
| `structurereloot.command.regen`         | Manually trigger regeneration                                |
| `structurereloot.command.reset`         | Clear the entire database for a world                        |
| `structurereloot.command.remove`        | Remove entries within a radius                               |
| `structurereloot.command.reloadConfig`  | Reload or reset config files                                 |
| `structurereloot.command.internal`      | Used internally; grant only to operators                     |

---

## In-Game UI

### `/reloot info`

Displays an interactive panel showing the current block and entity settings for a world. Every setting is clickable:

- **Reloot on Startup** — toggle between `true` and `false`
- **Max Reloot Amount** — click to type a new value into chat
- **Time Between Reloot** — click to type a new duration pattern into chat

Changes take effect immediately and are saved to the update settings file.

![Settings panel](https://github.com/CansteinBerlin/StructureReloot/assets/75828222/5dc693ff-981e-40d9-a9dd-929127c40d02)

### `/reloot listLootables`

Shows a paginated, scrollable list of all tracked loot entries. Each entry displays the block/entity type, its coordinates, and three action buttons:

- **Coordinates** — click to teleport (requires `teleportPermission`)
- **[Reloot]** — immediately regenerates the entry and removes it from tracking
- **[-]** — removes the entry from the database without regenerating it

![Lootable list](https://github.com/CansteinBerlin/StructureReloot/assets/75828222/8d896bc3-620f-4955-b2eb-100b0154b1de)

---

## Activity Logging

The plugin writes a detailed activity log to `plugins/StructureReloot/logs/<date>.txt`. Every tracked add, remove, and reloot operation is recorded with a timestamp. In `debugMode`, all log entries are also printed to the server console.

---

## Database

Each world has its own SQLite database file stored at `plugins/StructureReloot/data/<worldname>.db`. The database contains two tables:

- **`blocks`** — location, loot table key, block material, and facing direction
- **`entities`** — location, loot table key, entity type, and UUID

The database is updated synchronously on the main thread. Large batch operations (adding or removing more than 1000 entries) are automatically split into multiple transactions.