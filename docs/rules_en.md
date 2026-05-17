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

Records and displays rule change history including operator, timestamp, newValue, and original value when rules are modified.

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
- Categories: `IGNY`, `FEATURE`, `PORTING`

## skeletonsPickupSwordsReintroduced `MC>=1.21.4`

Reintroducing the skeleton pickup swords behavior from Minecraft versions below 1.21.4

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`, `PORTING`

## teleportInheritMinecartsMotionReintroduced `MC>=1.21.2`

Ports the feature from versions Minecraft 1.21-1.21.1 where minecarts carrying passengers transfer their motion to passengers during the teleport tick when crossing dimensions to version Minecraft 1.21.2 and above.

Minecart entities with `Infinity` (`Double.MAX_VALUE`) motion (Motion attribute) no longer work in Minecraft `1.21.11` and above; the `allowInvalidMotion` rule must be enabled for these versions.

- Type: `boolean`
- Default Value: `false`
- Suggested Options: `false`, `true`
- Categories: `IGNY`, `FEATURE`, `PORTING`

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

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `BUGFIX`

## commandCustomPlayerPickupItem

Use /customPlayerPickupItem to control player custom pickup items.

- Type: `string`
- Default Value: `ops`
- Suggested Options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

## commandCustomItemMaxStackSize `MC>=1.20.5`

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

## instantFrogEat

Allows frogs to scan and instantly consume nearby edible entities every game tick.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## allowInvalidMotion

Allows entity motion (`Motion attribute`) to be `Infinity` (`Double.MAX_VALUE`), reverting the changes made in Minecraft `25w45a`.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## accelerateBabyVillagerGrowth

Players can feed baby villagers bread, carrots, potatoes, wheat, wheat seeds, beetroots, beetroot seeds, torchflower seeds, or pitcher pods to accelerate their growth.  
Each feeding reduces the remaining growth time by approximately 10%. A minimum of 100 ticks is reduced per feeding, and feeding has no effect once the remaining growth time reaches 200 ticks.  
Particles are displayed upon successful feeding.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## lightningBoltNoFire

Lightning Bolt will not generate fire blocks or ignite entities.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## dispenserTrade

Dispensers can trade with villagers.  
Name the dispenser after the target villager's trade slot (number 1-10), then place it facing the villager.  
Any single slot or combination of slots in the dispenser that fulfills the trade's currency requirement will trigger the trade.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## dispenserTradeFailDisperseItem

When a dispenser trade fails, do not eject the currency items; instead, cause the dispenser to fail its ejection attempt.

- Type: `boolean`
- Default value: `true`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## renewableCalcite

Using pointed dripstone to convert quartz blocks into calcite via random ticks.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## pureShulkerBoxDispense

When a dispenser places a dyed or renamed shulker box, it will be reverted to a regular, unnamed shulker box while preserving its contents.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## structureBlockNoBlockUpdate

When loading a structure with structure blocks, the placed blocks do not trigger block updates.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## spawnMaxCountIgnoresChunkOverlap

Count overlapping spawnable chunks between players towards the spawn cap, i.e. calculate the spawn cap based on online player count.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## prioritizeFlyingUseItem

Prioritize using items over interacting with blocks while flying or riptiding.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## optimizedSpawning

Optimizes performance by canceling spawn cycles for any category that has reached its mobcap.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `OPTIMIZATION`

## dyedFrog

Feed tadpoles with dye to determine their frog variant upon maturing. Feeding slimeballs will result in vanilla behavior.  
Green Dye -> Cold Frog (Green)  
Orange Dye -> Temperate Frog (Brown)  
Light Gray Dye -> Warm Frog (White)

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## betterLoyaltyTrident

Tridents with the Loyalty enchantment will trigger their return if they exceed the player's simulation distance or go beyond the world's minimum/maximum build height.  
Loyalty will not trigger if both the player and the trident are outside the world's build height limits.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## removeSyncmaticaPermission

Sets the permission level required to remove schematics in [Syncmatica](https://github.com/sakura-ryoko/syncmatica).

- Type: `boolean`
- Default value: `true`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `IGNY`

## disableWatchDog

Disables the Minecraft watchdog, preventing the server from stopping due to lag.

- Type: `boolean`
- Default value: `true`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `IGNY`

## podzolSpread

Podzol will spread like grass and mycelium to nearby dirt blocks.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## noOwnerTntLootingIII

If the owner of the primed TNT entity is null when it explodes, then any entities killed by the explosion should apply the Looting III effect.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## globalDaylightDetector

Daylight detectors are no longer restricted by dimensions. In the absence of skylight, they will calculate brightness based on the Overworld time.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## noteBlockSelfCheck

When placed, a note block checks its block state once, just like a piston.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## noCreativeDestroyAttachmentDrops

When a player in Creative Mode breaks a block, any blocks attached to it (support-dependent blocks) will not drop as items when they are destroyed.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## showRuleSource

Display the Name of the Mod that registered the current rule in the rule details.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`

## theEndCanCreateNetherPortal

Allows Nether portals to be created and activated in the End dimension.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## renewableEndGatewayPortal

Allows players to create an End Gateway portal by surrounding an air block with Dragon Eggs at the same height and lighting a fire in the center.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## endGatewayPortalNoCooldown

Removes the 40gt cooldown for End Gateways, whether triggered by an entity entry or the periodic 2400gt cooldown.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## liquidSourceCanDestroy

Allows fluid sources to be targeted and destroyed.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## betterSprintGameTick

When conditions are not met, tick sprint (warp) will pause and the tick rate will revert to 20.  
`true`: Pauses when no real player is present on the server.  
`playerJoin`: Pauses when a new real player joins, until they log out.

- Type: `string`
- Default value: `false`
- Suggested options: `false`, `true`, `playerJoin`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## tripwireHookDupeReintroduced `MC>=26.1`

Reintroduces tripwire hook duplication by skipping block state checks upon destruction, reverting changes made in 26.1-pre-2.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`, `PORTING`

## showClassMixinList

Displays the Mixin classes of all injected classes in the stack trace of a crash report.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`

## piglinNeutralBehavior

Piglins remain neutral towards players unless they are attacked first.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## fakePlayerMemoryLeakFix `MC<26.1`

Resolve fake player memory leak by disabling packet sending and preventing queue buildup. [#2169](https://github.com/gnembon/fabric-carpet/issues/2169)

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `BUGFIX`

## liquidNeverSpread

Prevent the fluid from spreading horizontally or downwards.  
`liquid_source`: Only liquid sources will not spread.  
`true`: Neither liquid sources nor flowing liquids will spread.

- Type: `string`
- Default value: `false`
- Suggested options: `false`, `true`, `liquid_source`
- Categories: `IGNY`, `FEATURE`

## superEffectLevel

Allows the maximum level of effects given via the /effect command to reach 2147483647.

Takes effect after reloading the world/save if set to default.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## simpleEntityIDSuppression

Create an update suppressor based on an 'IllegalStateException' thrown during entity creation by spawning a fake player with a specific name. 
When the rule value is `false`, the rule is disabled.  
When the rule value is `true`, spawning a fake player named 'eIDSuppression' can be used to create an Entity ID Suppressor.  
When the rule value is any other string, spawning a fake player with that specific string as their name can be used to create an Entity ID Suppressor.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## entityIDSuppressionWhitelist

Creation of entities not in this list will not trigger Entity ID Suppression.  
#all: Any entity  
#none: No entities  
Separate each entity with a `,`

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## shulkerBoxInShulkerBox

Allows shulker boxes to be placed inside other shulker boxes.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## comparatorDupeReintroduced `MC>=26.2`

Allow comparators to clone themselves when changing modes; reverted changes from 26.2-snapshot-2.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`, `PORTING`

## drillAnvil

When a falling anvil lands on an unbreakable block with an explosion resistance of 1200 or less, it will destroy the block and continue to fall.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## transparentNightmarishBlock

Make Trial Spawner, Vault, Budding Amethyst, Amethyst Buds, and Amethyst Clusters act like 'ghost' blocks: they can be phased through by non-player entities, and blocks moved by pistons can pass through them. These blocks are immune to explosions and mining, and are protected against destruction by headless piston retraction or other illegal block updates.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## ghostEnderPearlFix `MC>=1.21.2`

Fixed double persistence of ender pearls caused by a thread race condition. [MC-306936](https://bugs.mojang.com/browse/MC-306936)

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `BUGFIX`

## stableTNTExplosion

Controls the TNT explosion ray strength mode used during block destruction calculation.

`false`: Use vanilla random value.  
`minimum`: Force random factor to `0.0`.  
`average`: Force random factor to `0.5`.  
`maximum`: Force random factor to `1.0`.

- Type: `string`
- Default value: `false`
- Suggested options: `false`, `minimum`, `average`, `maximum`
- Categories: `IGNY`, `FEATURE`

## betterFlippinCactus

Allows cacti to rotate any block with directional properties. Requires the 'flippinCactus' rule to be enabled.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `FEATURE`

## blockableAmethyst

Non-source water blocks can prevent small amethyst buds from growing on budding amethyst.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## constantSpeedHopperMinecart

Hopper minecarts will no longer slow down based on their contents, maintaining the same speed as an empty minecart.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `CREATIVE`, `FEATURE`

## fastAnvilBreaking

When a falling anvil entity is destroyed by a non-full block, it clears and drops all anvil entities and blocks from its Y-level up to the build height limit.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## fireworksExtra

Set the maximum firework level to 8, allowing players to craft rockets of different levels using 1 piece of paper and 1 to 8 pieces of gunpowder.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## safePlayerDeathDrop `🐛Beta`

Items dropped upon player death will not scatter in all directions.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## fireworksStacking `🐛Beta`

Allows the acceleration effects of multiple fireworks to stack while flying, enabling players to reach higher flight speeds.  
The rule value represents the maximum number of fireworks that can stack; -1 means no limit

- Type: `int`
- Default value: `1`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## stickyResinBlock `🐛Beta` `MC>=1.21.4`

Makes Resin Blocks stick to other movable blocks like Slime and Honey Blocks, but they will not stick to Slime or Honey Blocks themselves.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## convenientRegeneratePowderSnow `🐛Beta`

Pointed dripstone (stalactites) can drip powder snow like water or lava, and the snow dripping from pointed dripstone can be collected by cauldrons.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## linkableEnderChest `🐛Beta`

Controls whether custom-named ender chests with the same name share a common inventory (channel).  
`false`: Keep vanilla behavior.  
`onlyLink`: Named ender chests with the same custom name share inventory, regardless of which player opens them.  
`true`: Includes `onlyLink` behavior and allows interaction with blocks such as comparators and hoppers.  
Ender chests without a custom name retain vanilla behavior, showing the player's personal ender chest inventory.

- Type: `string`
- Default value: `false`
- Suggested options: `false`, `onlyLink`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## invisibleItemFrames `🐛Beta`

Makes the item inside an item frame invisible when the frame has the specified name.  
false: Rule is disabled.  
true: Item frames named 'invisible' can become invisible.  
Any other string: Item frames named with that string can become invisible

- Type: `string`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`

## bonemealableAmethyst `🐛Beta`

Using bonemeal on budding amethyst can grow amethyst buds in any direction.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `IGNY`, `SURVIVAL`, `FEATURE`
