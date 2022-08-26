# Regenerate Structure Plugin Konzept
## Idee:
Alle Kisten die einen sog. Loottable enthalten wie z.b. End City Kisten, 
sowie die Itemframes in End Cities werden nach einem bestimmten Zeitintervall 
(default 1 Woche) zurückgesetzt. Das passiert für alle Dimensionen.\
Gespeichert werden alle Daten für jede Dimension in einer sqllite Datenbank.
Das Zurücksetzten passiert beim Neustart des Servers oder bei Ausführung 
des commands.\
Nach der generierung werden alle Daten aus der datenbank gelöscht. 
Somit werden nur kisten welche auch wirklich neu gefunden wurden beim 
nächsten mal neu generiert.

## Commands:
- Um alle Blöcke/Entitäten zu regenerieren: `/structure regen all`
- Um alle Blöcke/Entitäten in einer bestimmten Welt zu regenerieren: `/structure regen <world>`
- Setzen des Regenerierungsintervalls: `/structure intervall <world/all> <value>` (format: 1w, 1t, 1h, 30m, 1w2t usw.)
- Informationen über die jeweiligen Intervalle anzeigen: `/structure info`

## Configs
- **Main Config `config.yml`:**
  - Prefix
  - Intervall Länge
  - Regenerieren wenn kiste nicht vorhanden
  - Welten welche von der regenerierung ausgenommen sind. 
- **Language config `messages.yml`**
- **Letzte Regenrierungs Config `last_regen.yml`:**
  - Speichert für jede welt die letzte Regenerierung als Systemzeit.

## Database per world:
- **Chests Table:**
  - varchar(`11+1+3+1+11=`27): location PRIMARY 
  - varchar(32): loottable
  

- **Itemframe Table (only the_end):**
  - varchar(`11+1+3+1+11=`27): location PRIMARY
  - varchar(`len(WAXED_WEATHERED_CUT_COPPER_STAIRS)+7=`40): material
  - varchar(`len(NORTH_NORTH_EAST)=`16): blockFace
  - varchar(`len(ce0a1d1e-89e0-44ff-8275-2b08ee46d752)=`36): entityUUID

## Speichern von Daten
- **Chests:**
  - [LootGenerateEvent](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/world/LootGenerateEvent.html)


- **ItemFrames:**
  - [ChunkPopulateEvent#getChunk#getEntities](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/world/ChunkPopulateEvent.html) funktioniert nur, wenn die chunks das erste Mal generiert werden
  - [EntityDamageByEntityEvent](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageByEntityEvent.html) item wird aus dem ItemFrame genommen

## Exploitable?
### Chests:
Nein, da das [LootGenerateEvent](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/world/LootGenerateEvent.html) 
nur beim ersten Öffnen der Kiste aufgerufen wird. Da man auch anderweitig,
ohne Commands oder Plugins, keine Kisten mit einem Loottable erstellen kann,
sind alle Kisten, bei denen dieses Event aufgerufen wird, von Minecraft generiert.


### Itemframes:
Nein, da man während des [ChunkPopulateEvent's](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/world/ChunkPopulateEvent.html) 
allen generierten Itemframes einen bestimmten PersistentDataContainer wert gibt, 
welcher dieses Itemframe identifiziert. Sobald das Item aus dem Frame genommen wurde, 
wird dieser Tag gelöscht. Das bedeutet, alle Itemsframes die von Minecraft generiert 
werden, werden noch während der Generierung gekennzeichnet. Sobald dann ein Spieler
ein Item aus diesem Frame nimmt wird diese Markierung weggenommen 
und das Frame ist ein ganz normales wie jedes andere auch. Dadurch ist gewährleistet 
das nur von Minecraft generierte Itemframes in die datenbank mitaufgenommen werden. 
Wenn das Plugin jetzt die Frames regeneriert, werden die alten itemframes gelöscht, 
und neue mit demselben PersistentDataContainer generiert. Damit wird dieses neue Itemframe 
als ein von Minecraft generiertes Itemsframe definiert.

## Probleme:
### Lag
Dieses Problem könnte evtl auftreten, da in relativ kurzer Zeit sehr 
viele Chunks in allen Welten geladen werden müssen. (worst case 3 Chunks
pro endcity) usw. Dies ist der Fall, da nur in geladenen Chunks Blöcke 
geändert/Entities gespawnt werden können. 
 - **Lösung:** Evtl. nur alle 3 Sekunden eine Kiste/Entität neugenerieren
   - **Vorteil:** Weniger Lag
   - **Nachteil:** Regenerierungsprozess kann in einer Welt mit 20 Endcities (a 8 Kisten und einer Elytra) bis zu 540+ Sekunden dauern (9 Minuten) 
