## Rules

Tip: You can use `Ctrl+F` to find rule(s)

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

When a player is locked on by a Guardian or Elder Guardian, they will not suffer from mining effect.

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

When the Nether portal in the Overworld receives a random tick, zombified piglin will not naturally spawn in the Nether portal in the Overworld

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

### SkeletonsPickupSwordsReintroduced `MC>=1.21.4`

Reintroducing the skeleton pickup swords behavior from Minecraft versions below 1.21.4

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

### MinecartMotionFix `MC>=1.21.2`

Ports the feature from versions 1.21-1.21.1 where minecars carrying passengers transfer their momentum to passengers during the teleport tick when crossing dimensions to version 1.21.2 and above.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

### TntMinecartEmptyDamageSourceFix `MC<1.21.9`

Fixed the source of empty damage in TNT minecarts.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

### FakePlayerBoatYawFix

Fake players will not correct yaw when riding boats. [#2100](https://github.com/gnembon/fabric-carpet/issues/2100)

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`, `BUGFIX`

### KillFakePlayerRemoveVehicle

When removing a fake player, remove the vehicle they are riding in.
cantrade: Remove the vehicle when there are no villagers or wandering merchants on it, and keep other options consistent with the false option.

- Type: `boolean`
- Default Value: `true`
- Suggested Options: `true`, `cantrade`, `false`
- Categories: `IGNY`, `FEATURE`

### CandlePlaceOnIncompleteBlock

Candle can place on incomplete block

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- ategories: `IGNY`, `FEATURE`