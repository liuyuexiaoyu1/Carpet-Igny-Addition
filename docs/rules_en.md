# Rules

Tip: You can use `Ctrl+F` to find rule(s)

## wardenNeverDig

Wardens will not burrow underground unless they hear sounds.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## playerLevitationFreeShulkerBullet

Players will not receive levitation effect when hit by shulker bullets.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## playerMiningFatigueFreeGuardian

When a player is locked on by a Guardian or Elder Guardian, they will not suffer from mining effect.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## showRuleChangeHistory

Records and displays rule change history including operator, timestamp, and original value when rules are modified.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `COMMAND`, `FEATURE`

## fakePlayerCanPush

Fake players can be pushed by other players' collisions.

- Type: `boolean`
- Default value: `true`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## wetSpongeCanAbsorbLava

Wet sponges can absorb lava and turn into regular sponges when touching lava.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## commandPlayerEnderChestDrop

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

## noWardenDarkness

Players near wardens will not receive darkness effect.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## floatingIceWater

When ice is broken without silk touch and there are no blocks beneath it, water will be generated (even if the ice is floating).

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## projectileDuplicationReintroduced `MC>=1.21.2`

Reintroduces the projectile residual tick duplication behavior from versions below 1.21.2 (can be considered as restoring duplication behaviors for potions, eggs, snowballs, etc.).

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## skeletonsPickupSwordsReintroduced `MC>=1.21.4`

Reintroducing the skeleton pickup swords behavior from Minecraft versions below 1.21.4

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## teleportInheritMinecartsMotionReintroduced `MC>=1.21.2`

Ports the feature from versions 1.21-1.21.1 where minecarts carrying passengers transfer their motion to passengers during the teleport tick when crossing dimensions to version 1.21.2 and above.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## tntMinecartEmptyDamageSourceFix `MC<1.21.9`

Fixed the source of empty damage in TNT minecarts. [MC-279548](https://bugs.mojang.com/browse/MC-279548)

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## fakePlayerBoatYawFix `MC<1.21.11`

Fake players will not correct yaw when riding boats. [#2100](https://github.com/gnembon/fabric-carpet/issues/2100)

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`, `BUGFIX`

## killFakePlayerRemoveVehicle

When removing a fake player, remove the vehicle they are riding in.

canBoatTrade: Remove the vehicle when there are villagers or wandering merchants on it, other entities are not removed.

- Type: `boolean`
- Default Value: `true`
- Suggested Options: `false`, `canBoatTrade`, `true`
- Categories: `IGNY`, `FEATURE`

## candlePlaceOnIncompleteBlock

Candle can place on incomplete block.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## commandfixnotepitch

`/fixnotepitch` permission.

- Type: `string`
- Default Value: `ops`
- Suggested Options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

## fixnotepitchUpdateBlock

Control `commandFixnotepitech` to sending block update.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

## happyGhastNoClip `MC>1.21.6`

Happy Ghast has players who ride while ignoring block collisions and can pass through blocks.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `CLIENT`, `FEATURE`

## noWitherEffect

Wither, Wither Skeleton, and Wither Rose cannot grant players the Wither effect.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## locatorBarNoFakePlayer `MC>=1.21.6`

The locator bar does not show fake players.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## fakePlayerLoginLogoutNoChatInfo

When a fake player logs in and logs out, the prompt of login and logout will not be displayed in the chat historys

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `COMMAND`, `FEATURE`

## commandPlayerOperate

Use the /playerOperate command to manage player actions.

- Type: `string`
- Default Value: `ops`
- Suggested Options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `IGNY`, `COMMAND`, `FEATURE`

## commandClearLightQueue

Clears the light queue of the world.

- Type: `string`
- Default Value: `ops`
- Suggested Options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `IGNY`, `COMMAND`, `FEATURE`

## fakePlayerNoBreakingCoolDown

Fake players keep breaking without a cooldown for breaking.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## creativeDestroyWaterloggedBlockNoWater

Creative players can destroy waterlogged blocks without generating water.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## trialSpawnerCoolDown `MC>=1.20.5`

Customize the monster spawning cooldown after the trial spawner generates rewards.

- Type: `int`
- Default Value: `36000`
- Categories: `IGNY`, `FEATURE`

## playerOperationLimiter

Enables per-tick operation rate limiting for both real and fake players. This master switch controls whether the following four limit rules take effect.

From [Plusls Carpet Addition](https://github.com/Nyan-Work/plusls-carpet-addition)

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## realPlayerBreakLimitPerTick

Maximum number of blocks a real player can break per game tick. Set to 0 to disable. Requires `playerOperationLimiter` to be enabled.

- Type: `int`
- Default value: `0`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## realPlayerPlaceLimitPerTick

Maximum number of blocks a real player can place per game tick. Set to 0 to disable. Requires `playerOperationLimiter` to be enabled.

- Type: `int`
- Default value: `0`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## fakePlayerBreakLimitPerTick

Maximum number of blocks a fake player can break per game tick. Set to 0 to disable. Requires `playerOperationLimiter` to be enabled.

- Type: `int`
- Default value: `0`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## fakePlayerPlaceLimitPerTick

Maximum number of blocks a fake player can place per game tick. Set to 0 to disable. Requires `playerOperationLimiter` to be enabled.

- Type: `int`
- Default value: `0`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## generateNetherPortal

In the Overworld and Nether dimensions, using flint and steel or fire charges on obsidian and nether portal blocks allows you to place a nether portal perpendicular to the clicked block face.

Use the vanilla logic when sneaking and clicking.

If right-clicking the top or bottom face of an obsidian block, the generated nether portal will face the player's direction.

If right-clicking a nether portal block, placement is only allowed on its four side faces.

If the player is in creative mode, this rule can also be used in the End dimension.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## placeComposterCompost

hen placing the composter, the compost level should be set to a value within the specified range, with a minimum of 0 and a maximum of 8,Triggered when the sneak button is pressed.

- Type: `int`
- Default value: `0`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## enderDragonDeathRiseLimit

Limits the maximum Y-coordinate the Ender Dragon can rise to after death. Set to -1145 to disable the limit.

- Type: `int`
- Default value: `-1145`
- Categories: `IGNY`, `FEATURE`

## enderDragonDeathDropExp

Sets the amount of experience dropped when the Ender Dragon dies. Set to -1 to use vanilla behavior.  

The first dragon kill grants 12,000 experience (set rule value to 12000).

Subsequent dragon kills grant 500 experience (set rule value to 500).

- Type: `int`
- Default value: `-1`
- Categories: `IGNY`, `FEATURE`

## instantSpawnEnderDragon

Skips the respawn animation and instantly spawns the Ender Dragon when end crystals are placed to revive it.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## maxEndPortalSize

Sets the maximum size of the End portal (minimum is 3). Set to -1 to use vanilla behavior.

- Type: `int`
- Default value: `-1`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## allowRectangularEndPortal

Allows rectangular End portals (each side must be at least 3 blocks long). Only effective when maxEndPortalSize is not -1.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## instantVaultSpawnLoot `MC>=1.20.5`

When generating loot from the vault, skip the animation and directly generate all loot.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## trialSpawnerLootMultiplier `MC>=1.20.5`

The loot multiplier after the trial spawner generates rewards.

- Type: `int`
- Default value: `1`
- Categories: `IGNY`, `FEATURE`

## trialSpawnerDropKeyProbability `MC>=1.20.5`

The probability of a Trial Key dropping as a loot from a Trial Spawner, Set to -1 to use vanilla behavior.

- Type: `int`
- Default value: `-1`
- Categories: `IGNY`, `FEATURE`

## instantTrialSpawnerSpawnLoot `MC>=1.20.5`

Skip the animation when the trial spawner generates loot, and directly spawn all the spoils.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## simpleSoundSuppression `MC>=1.20.5`

Create an update suppressor based on 'IllegalArgumentException' by naming a Calibrated Sculk Sensor with a specified name.

When the rule value is false, the rule is inactive.

When the rule value is true, Calibrated Sculk Sensors named '声音抑制器' or 'soundSuppression' can be used to create sound suppressors.

When the rule value is any other string, Calibrated Sculk Sensors named that string can be used to create sound suppressors.

- Type: `string`
- Default value: `false`
- Categories: `IGNY`, `FEATURE`

## safeSoundSuppression `MC>=1.20`

When the sound suppressor has no redstone signal input, it has no effect.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## twoChangedRuleValueSetDefault

When changing a rule value with the /carpet command, if the new value is the same as the previous one, reset it to the default.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `COMMAND`, `FEATURE`

## optimizedEntityList

Optimizes stacked living entities; separate each entity with a comma `,`, set it to '#none' to disable it.

It disables most movement and squeezing calculations for these entities, improving performance.

- Type: `string`
- Default value: `#none`
- Suggested options: `#none`, `minecraft:warden`, `minecraft:piglin`, `minecraft:warden,minecraft:piglin`
- Categories: `IGNY`, `OPTIMIZATION`, `FEATURE`

## optimizedEntityLimit

Enables optimization when the number of stacked entities reaches the value set by this rule. The entity must be included in the optimizedEntityList rule.

- Type: `int`
- Default value: `100`
- Categories: `IGNY`, `OPTIMIZATION`, `FEATURE`

## optimizedTNTErrorScopeFix

Fixes an issue where entities like Wind Charges have their explosions incorrectly optimized when optimizedTNT Rule is enabled. [#1928](https://github.com/gnembon/fabric-carpet/issues/1928)

### Potential conflicts with certain multi-threading optimization mods!!

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `BUGFIX`

## furnaceHasIncombustibleHighlight

When the input slot of a furnace contains non-smeltable items, the furnace itself turns light red and becomes visible through blocks (X-ray highlight).

Highlighting is not effective. The chunk where the furnace is located needs to be reloaded.

Requires this mod to be installed on the client side as well.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CLIENT`, `SURVIVAL`, `FEATURE`

## commandCustomPlayerPickupItem

Use /customPlayerPickupItem to control player custom pickup items.

- Type: `string`
- Default Value: `ops`
- Suggested Options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

## commandCustomItemMaxStackSize

Use /customItemMaxStackSize to control the maximum stack size of the item.

- Type: `string`
- Default Value: `ops`
- Suggested Options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

## playerHungryValueNoDecrease

The player's hunger value will not decrease.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## playerLowHungryValueCanSprint

When the player's hunger level is below 7 points, they can sprint.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `CLIENT`, `FEATURE`


