# Carpet-Igny-Addition

[中文](README.md) | **English**

## Required Mods

| Name | Type | Links                                                                                                                                                                       | Notes |
|------|------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------|
| Carpet | Required | [MC百科](https://www.mcmod.cn/class/2361.html) &#124; [Modrinth](https://modrinth.com/mod/carpet) &#124; [CurseForge](https://www.curseforge.com/minecraft/mc-mods/carpet) | |
| MixinExtras | Built-in | [MC百科](https://www.mcmod.cn/class/12750.html)                                                                                                                           | |
| Fabric API | Required | [MC百科](https://www.mcmod.cn/class/3124.html) &#124; [Official](https://fabricmc.net/)                                                                                         | &gt;=1.21 |

## Version Support

| Game Version | Development Status | Last Supported Version |
|--------------|-------------------|------------------------|
| 1.21(.1) (Main) | Maintained | - |
| 1.21.2 ~ Latest Release | Maintained | - |

## Rules

### WardenNeverDig

Wardens will not burrow underground unless they hear sounds.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

### PlayerLevitationFreeShulkerBullet

Players will not receive levitation effect when hit by shulker bullets.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

### PlayerMiningFatigueFreeGuardian

Players will not receive mining fatigue effect when attacked by guardians or elder guardians.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

### ShowRuleChangeHistory

Records and displays rule change history including operator, timestamp, and original value when rules are modified.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

### FakePlayerCanPush

Fake players can be pushed by other players' collisions.

- Type: `boolean`
- Default value: `true`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

### WetSpongeCanAbsorbLava

Wet sponges can absorb lava and turn into regular sponges when touching lava.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

### CommandPlayerEnderChestDrop

Controls permission level for players to drop ender chest items.  
Usage: `/player <player> drop all` - drops inventory + ender chest items (if permitted);  
`/player <player> drop inventory` - drops only inventory items;  
`/player <player> drop enderchest` - drops only ender chest items.  
Without permission: 'all' drops only inventory items; otherwise: 'all' drops both inventory and ender chest items.  
Real players: only OPs can drop their ender chest items.  
Fake players: follows above permission settings.

- Type: `string`
- Default value: `false`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `IGNY`, `COMMAND`, `FEATURE`

### NoWardenDarkness

Players near wardens will not receive darkness effect.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

### FloatingIceWater

When ice is broken without silk touch and there are no blocks beneath it, water will be generated (even if the ice is floating).

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

### NoZombifiedPiglinNetherPortalSpawn

Zombified piglins will not naturally spawn in nether portals when difficulty is not peaceful.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

### ProjectileDuplicationReintroduced `MC>=1.21.2`

Reintroduces the projectile residual tick duplication behavior from versions below 1.21.2 (can be considered as restoring duplication behaviors for potions, eggs, snowballs, etc.).

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`
