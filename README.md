# Carrot Rabbit Mod (مود أرنب الجزر)

Minecraft **1.21.1** · **NeoForge** · requires **Extended Crafting** (7.0.x)

Five carrot staffs, crafted only on the **Ultimate 9x9 table** (81 items, shapeless).
Right click a staff to summon your rideable Super Rabbit (right click again while riding to send it away).
Hold jump to charge the jump bar, like a horse. Cooldown: 20 seconds. The staff is not consumed.

| Staff | Recipe (81x) | Speed (blocks/s) | Jump (full charge) | Health | Extras |
|---|---|---|---|---|---|
| Carrot | Carrot | 7 | 2.5 | 10 hearts | |
| Golden Carrot | Carrot Staff | 8.5 | 3.5 | 12 hearts | slow regeneration |
| Diamond Carrot | Golden Carrot Staff | 10 | 5 | 15 hearts | no fall damage |
| Meteor Carrot | Diamond Carrot Staff | 11.5 | 6 | 18 hearts | floats on water, fire/lava immune (rider too) |
| Legendary Carrot | Meteor Carrot Staff | 13 | 8 | 20 hearts | slow glide when falling, some armor |

## Build
Push to GitHub: the workflow in `.github/workflows/build.yml` builds the jar (Actions > build > Artifacts).
Locally: Gradle 8.10+ and Java 21, then `gradle build`; the jar is in `build/libs/`.

## Textures
Replace the PNGs (keep the file names):
- staffs: `src/main/resources/assets/carrotmod/textures/item/` (16x16)
- rabbit: `src/main/resources/assets/carrotmod/textures/entity/super_rabbit_1..5.png` (128x64, one per tier)

## Edit stats
`src/main/java/com/carrotmod/SuperRabbit.java` (SPEED_BPS, JUMP, HEALTH, ARMOR arrays).
