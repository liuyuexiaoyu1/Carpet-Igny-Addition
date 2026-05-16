# 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

## 监守者永不钻地 (wardenNeverDig)

监守者没有听到声音将不会使它钻地。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 潜影贝攻击玩家没有漂浮效果 (playerLevitationFreeShulkerBullet)

当玩家被潜影贝的子弹击中时不会拥有漂浮效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 守卫者攻击玩家没有挖掘疲劳 (playerMiningFatigueFreeGuardian)

当玩家在被守卫者或者远古守卫者锁定的时候不会拥有挖掘疲劳效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 显示规则变更历史 (showRuleChangeHistory)

在规则的值变更的时候，会记录并且在规则详情中显示操作者、操作时间、原始值和用户输入的值。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

## 假玩家可以被推动 (fakePlayerCanPush)

假玩家受到其他玩家的碰撞时，不会移动（被推动）。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 湿海绵吸收岩浆 (wetSpongeCanAbsorbLava)

使湿海绵碰到岩浆可以吸收岩浆（会变为海绵）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 丢弃玩家末影箱物品指令权限 (commandPlayerEnderChestDrop)

控制玩家丢弃末影箱物品的权限等级  
用法: /player <玩家名> drop all - 丢弃背包+末影箱物品(如有权限)，  
语法:  
/player <玩家名> drop inventory 仅丢弃背包物品;  
/player <玩家名> drop enderchest - 仅丢弃末影箱物品，无权限时: 'all' 仅丢弃背包物品，否则: 'all' 同时丢弃背包和末影箱物品，真人玩家: 只有OP可以丢弃其末影箱物品，假玩家: 遵循上述权限设置。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

## 玩家在监守者附近不会被给予黑暗效果 (noWardenDarkness)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 悬空冰破坏产生水 (floatingIceWater)

当冰下没有方块时，不使用精准采集破坏冰也能生成水。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 重新引入投掷物复制 (projectileDuplicationReintroduced) `MC>=1.21.2`

重新引入1.21.2以下的投掷物残留刻复制行为（可视作恢复药水、鸡蛋、雪球等复制行为）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`, `PORTING`

## 重新引入骷髅捡剑 (skeletonsPickupSwordsReintroduced) `MC>=1.21.4`

重新引入1.21.4以下的骷髅捡剑行为。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`, `PORTING`

## 重新引入矿车传送动量继承 (teleportInheritMinecartsMotionReintroduced) `MC>=1.21.2`

重新引入Minecraft 1.21-1.21.1版本中矿车携带乘客跨纬度会在传送tick给予乘客矿车的动量到Minecraft 1.21.2以上版本。  
`Infinity` (`Double类型的最大值`) 动量 (`Motion属性`) 的矿车实体在Minecraft `1.21.11`及以上版本失效，需在Minecraft `1.21.11`及以上版本启用`allowInvalidMotion`规则

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`, `PORTING`

## TNT矿车空伤害来源修复 (tntMinecartEmptyDamageSourceFix) `MC<1.21.9`

修复TNT矿车引爆时传入的伤害来源为null，导致TNT矿车无法继承伤害来源的属性（可视作重新引入TNT矿车掠夺）。 [MC-279548](https://bugs.mojang.com/browse/MC-279548)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 假玩家乘船不纠正偏航角修复 (fakePlayerBoatYawFix) `MC<1.21.11`

假玩家在骑乘船时不会纠正偏航角。 [#2100](https://github.com/gnembon/fabric-carpet/issues/2100)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`, `BUGFIX`

## 移除假玩家时移除载具 (killFakePlayerRemoveVehicle)

移除假玩家时移除其乘坐的载具。  
canBoatTrade：当载具上有村民或者流浪商人时移除载具，其他实体不移除。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `canBoatTrade`, `true`
- 分类: `IGNY`, `FEATURE`

## 蜡烛可放在不完整方块上 (candlePlaceOnIncompleteBlock)

蜡烛可直接放在上表面不完整方块上。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 音符盒音高指令权限 (commandfixnotepitch)

音符盒音高指令权限。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

## Fixnotepitch指令产生方块更新 (fixnotepitchUpdateBlock)

控制`commandFixnotepitech`规则是否产生方块更新。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

## 快乐恶魂无碰撞 (happyGhastNoClip) `MC>1.21.6`

快乐恶魂有玩家骑乘时无视方块碰撞。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `CLIENT`, `FEATURE`

## 没有凋零效果 (noWitherEffect)

凋零、凋零骷髅、凋零玫瑰不能给予玩家凋零效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 定位栏无假玩家 (locatorBarNoFakePlayer) `MC>=1.21.6`

定位栏不显示假玩家。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 假玩家登录登出无聊天信息 (fakePlayerLoginLogoutNoChatInfo)

假玩家登录登出不在聊天栏显示登录登出的提示。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

## 玩家动作指令 (commandPlayerOperate)

使用/PlayerOperate命令来控制玩家动作。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

## 清除光照队列指令 (commandClearLightQueue)

使用/clearlightqueue命令来清除光照队列。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

## 假玩家无挖掘冷却 (fakePlayerNoBreakingCoolDown)

假玩家长按破坏无挖掘冷却。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 创造破坏含水方块无水 (creativeDestroyWaterloggedBlockNoWater)

创造玩家破坏含水方块时不会产生水。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 试炼刷怪笼刷怪冷却 (trialSpawnerCoolDown) `MC>=1.20.5`

自定义试炼刷怪笼生成奖励之后的刷怪冷却。

- 类型: `int`
- 默认值: `36000`
- 分类: `IGNY`, `FEATURE`
- 
## 玩家操作限制器 (playerOperationLimiter)

为真实玩家和假玩家启用每游戏刻（tick）的操作频率限制功能。此开关控制以下四项限制规则是否生效。

移植自[Plusls Carpet Addition](https://github.com/Nyan-Work/plusls-carpet-addition)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 真玩家每游戏刻可破坏方块数量 (realPlayerBreakLimitPerTick)

真实玩家每游戏刻最多可破坏的方块数量。设为 0 表示不限制。需开启 `playerOperationLimiter` 规则。

- 类型: `int`
- 默认值: `0`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 真玩家每游戏刻可放置方块数量 (realPlayerPlaceLimitPerTick)

真实玩家每游戏刻最多可放置的方块数量。设为 0 表示不限制。需开启 `playerOperationLimiter` 规则。

- 类型: `int`
- 默认值: `0`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 假玩家每游戏刻可破坏方块数量 (fakePlayerBreakLimitPerTick)

假玩家每游戏刻最多可破坏的方块数量。设为 0 表示不限制。需开启 `playerOperationLimiter` 规则。

- 类型: `int`
- 默认值: `0`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 假玩家每游戏刻可放置方块数量 (fakePlayerPlaceLimitPerTick)

假玩家每游戏刻最多可放置的方块数量。设为 0 表示不限制。需开启 `playerOperationLimiter` 规则。

- 类型: `int`
- 默认值: `0`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 生成下界传送门 (generateNetherPortal)

在主世界和下界维度，对准黑曜石方块和下界传送门方块使用打火石和火焰弹，可以在右键的方块面上放置一个垂直于右键方块的下界传送门方块。  
潜行点击时使用原版逻辑。  
若右键黑曜石方块的上或下方块面，那么生成的下界传送门方块面向玩家。  
若右键下界传送门方块，则只能在它的4个边放置下界传送门方块。  
若玩家为创造模式，那么末地维度可用该规则。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 放置堆肥桶堆肥 (placeComposterCompost)

放置堆肥桶时堆肥到规则设置的值，范围为0-8，按着潜行键时触发。

- 类型: `int`
- 默认值: `0`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 末影龙死亡上升限制 (enderDragonDeathRiseLimit)

限制末影龙死亡后上升到的最大y坐标，设置为-1145表示不限制。

- 类型: `int`
- 默认值: `-1145`
- 分类: `IGNY`, `FEATURE`

## 末影龙死亡掉落经验 (enderDragonDeathDropExp)

末影龙死亡掉落的经验值，设置为-1表示遵循原版逻辑。  
首次击杀龙掉落的经验为12000（填写规则值12000）。  
重复击杀击杀龙掉落的经验为500（填写规则值500）。

- 类型: `int`
- 默认值: `-1`
- 分类: `IGNY`, `FEATURE`

## 瞬时生成末影龙 (instantSpawnEnderDragon)

放置末影水晶重生末影龙时跳过重生动画，直接召唤末影龙。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 最大末地传送门大小 (maxEndPortalSize)

设置末地传送门的最大大小（3以上），设置为-1表示遵循原版逻辑。

- 类型: `int`
- 默认值: `-1`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 允许长方形末地传送门 (allowRectangularEndPortal)

允许生成长方形的末地传送门（传送门边最小为3），maxEndPortalSize不为-1时有效。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 瞬时宝库生成战利品 (instantVaultSpawnLoot) `MC>=1.20.5`

宝库生成战利品时跳过动画，直接生成所有战利品。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 试炼刷怪笼战利品倍率 (trialSpawnerLootMultiplier) `MC>=1.20.5`

试炼刷怪笼的战利品倍率。

- 类型: `int`
- 默认值: `1`
- 分类: `IGNY`, `FEATURE`

## 试炼刷怪笼掉落钥匙概率 (trialSpawnerDropKeyProbability) `MC>=1.20.5`

试炼刷怪笼的战利品掉落试炼钥匙的概率，设置为-1表示遵循原版逻辑。

- 类型: `int`
- 默认值: `-1`
- 分类: `IGNY`, `FEATURE`

## 瞬时试炼刷怪笼生成战利品 (instantTrialSpawnerSpawnLoot) `MC>=1.20.5`

试炼刷怪笼生成奖励时跳过动画，直接生成所有战利品。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 简易声音抑制器 (simpleSoundSuppression) `MC>=1.20.5`

通过将校频幽匿感测体命名为指定名称来制作基于“IllegalArgumentException”的更新抑制器。  
规则值为false时，规则不生效。  
规则值为true时，命名为“声音抑制器”或“soundSuppression”的校频幽匿感测体可以用来制作声音抑制器。  
规则值为其它字符串时，命名为该字符串的校频幽匿感测体可以用来制作声音抑制器。

- 类型: `string`
- 默认值: `false`
- 分类: `IGNY`, `FEATURE`

## 安全声音抑制器 (safeSoundSuppression) `MC>=1.20`

当声音抑制器没有红石信号输入时，没有任何效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 两次更改规则值设置默认 (twoChangedRuleValueSetDefault)

当使用/carpet指令更改规则值时，与更改前的值相同就设为默认。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

## 实体优化列表 (optimizedEntityList) 

优化堆叠的生物实体，每个实体以`,`（英文逗号）分割，设为`#none`则不启用。  
它关闭了生物实体大部分移动和挤压的计算，使其性能更好。

- 类型: `string`
- 默认值: `#none`
- 参考选项: `#none`, `minecraft:warden`, `minecraft:piglin`, `minecraft:warden,minecraft:piglin`
- 分类: `IGNY`, `OPTIMIZATION`, `FEATURE`

## 实体优化限制 (optimizedEntityLimit) 

当堆叠的实体个数达到规则设置的值时启用优化，`optimizedEntityList`规则需包含这个实体。

- 类型: `int`
- 默认值: `100`
- 分类: `IGNY`, `OPTIMIZATION`, `FEATURE`

## optimizedTNT错误作用域修复 (optimizedTNTErrorScopeFix)

修复`optimizedTNT`规则启用时，错误优化风弹等实体爆炸的问题。 [#1928](https://github.com/gnembon/fabric-carpet/issues/1928)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `BUGFIX`

## 自定义玩家拾取物品指令权限 (commandCustomPlayerPickupItem)

使用/customPlayerPickupItem来自定义玩家能拾取的物品。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`
  
## 自定义物品最大堆叠数量指令权限 (commandCustomItemMaxStackSize) `MC>=1.20.5`

使用/customItemMaxStackSize来自定义物品的最大堆叠数量。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

## 玩家饥饿值不减少 (playerHungryValueNoDecrease)

玩家饥饿值不会减少。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 玩家饥饿值低可疾跑 (playerLowHungryValueCanSprint)

当玩家饥饿值低于7点时，可以疾跑。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `CLIENT`, `FEATURE`

## 瞬时青蛙捕食 (instantFrogEat)

使青蛙能够每游戏刻扫描并瞬间捕食周围的可食用的实体。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 允许非法动量 (allowInvalidMotion) `MC>=1.21.11`

允许实体的动量 (`Motion属性`) 可以为`Infinity` (`Double类型的最大值`), 回退了Minecraft `25w45a`的更改。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 喂食加速幼年村民生长 (accelerateBabyVillagerGrowth)

玩家手持面包、胡萝卜、马铃薯、小麦、小麦种子、甜菜根、甜菜种子、火把花种子和瓶子草荚果对幼年村民使用，以加速幼年村民的生长.  
一次可以减少约剩余成长时间的10%，每次喂食最少减少剩余成长时间100tick，当剩余成长时间达到200tick时喂食将不再减少.  
每次喂食会产生粒子。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 无火闪电束 (lightningBoltNoFire)

闪电束不会生成火方块与点燃实体。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 发射器交易 (dispenserTrade)

发射器可以与村民进行交易。  
将发射器命名为希望交易的村民目标交易选项(`数字1-10`)，然后放置发射器面向村民。  
发射器任意槽位或多个槽位满足交易货币需求即可交易。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 发射器交易失败不喷出物品 (dispenserTrade)

发射器交易失败时(交易选项缺货等)，不喷出货币，而是使发射器喷出失败。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 可再生方解石 (renewableCalcite)

使用滴水石锥接受随机刻转化石英块为方解石。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 纯净潜影盒发射 (pureShulkerBoxDispense)

发射器放置染色或者命名过的潜影盒会使其变回普通的未命名的潜影盒，同时保留其内容物。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 结构方块无方块更新 (structureBlockNoBlockUpdate)

使用结构方块加载结构时，结构中放置的方块不会产生方块更新。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 生成上限无视区块重叠 (spawnMaxCountIgnoresChunkOverlap)

将玩家之间的重叠的可生成区块计入生成上限，即按在线玩家数计算生成上限。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 优先飞行使用物品 (prioritizeFlyingUseItem)

鞘翅飞行或激流冲锋时，优先使用物品而非与方块交互。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 优化生成 (optimizedSpawning)

优化刷怪性能，当维度任意生成类型达到上限时停止该类型的生成计算。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `OPTIMIZATION`

## 染色青蛙 (dyedFrog)

使用染料喂食蝌蚪，当蝌蚪成年后根据喂食的对应染料变为对应种类的青蛙，喂食粘液球使用原版逻辑。  
绿色染料 -> 寒带青蛙  
橙色染料 -> 温带青蛙  
淡灰色染料 -> 热带青蛙

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 更好的忠诚三叉戟 (betterLoyaltyTrident)

附魔忠诚的三叉戟会在超过玩家的模拟距离和超过世界最低最高建筑高度时触发忠诚。  
玩家超过世界最低最高建筑高度时，三叉戟超过世界最低最高建筑高度不会触发忠诚。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 删除共享原理图权限 (removeSyncmaticaPermission)

在[Syncmatica](https://github.com/sakura-ryoko/syncmatica)中删除原理图时，需要当前规则值设置的权限。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`

## 禁用看门狗 (disableWatchDog)

禁用Minecraft看门狗，防止服务器因卡顿而停止运行。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`

## 灰化土扩散 (podzolSpread)

灰化土会像草方块与菌丝体一样向四周的泥土扩散。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 无主TNT抢夺三 (noOwnerTntLootingIII)

当TNT爆炸时，如果点燃的TNT实体的owner为空，那么因为爆炸死亡的实体则应用抢夺附魔等级3的效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 全局阳光探测器 (globalDaylightDetector)

阳光探测器不受维度限制，如果没有天空光照，阳光探测器会以主世界的时间计算亮度。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 音符盒自检 (noteBlockSelfCheck)

音符盒在放置时，会像活塞一样检查一次自己的方块状态。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 创造破坏附着方块无掉落物品 (noCreativeDestroyAttachmentDrops)

创造玩家破坏方块时，附着在上面的方块不会因为破坏而掉落物品。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 显示规则来源 (showRuleSource)

在规则详情中显示注册当前规则的Mod的名称。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`

## 末地可创建下界传送门 (theEndCanCreateNetherPortal)

允许在末地创建下界传送门。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 可再生末地折跃门 (renewableEndGatewayPortal)

使用龙蛋在同一高度上围住空气，并在中间点火，可生成一个末地折跃门。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 末地折跃门无冷却 (endGatewayPortalNoCooldown)

取消末地折跃门有实体进入后或是间隔2400gt触发的40gt冷却。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 流体源可挖掘 (liquidSourceCanDestroy)

允许选中流体源并且破坏。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 更好地加速游戏刻 (betterSprintGameTick)

当不满足条件的时候，tick sprint(warp)会暂停，tick rate变为20。  
`true`: 条件：服务器不存在真人玩家时。  
`playerJoin`: 条件：服务器加入新的真人玩家时，直到他退出服务器。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `playerJoin`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 重新引入绊线钩复制 (tripwireHookDupeReintroduced) `MC>=26.1`

重新引入绊线钩被破坏时，不检查方块状态而触发的绊线钩复制，回退了26.1-pre-2的更改。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`, `PORTING`

## 显示类Mixin列表 (showClassMixinList)

在崩溃报告的堆栈跟踪中显示堆栈中所有被注入的类的Mixin类。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`

## 猪灵中立行为 (piglinNeutralBehavior)

在玩家不攻击猪灵时，猪灵不会攻击玩家。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 假玩家内存泄露修复 (fakePlayerMemoryLeakFix) `MC<1.21.11`

修复假玩家内存泄露问题，防止假玩家在服务器运行时发包并添加队列导致内存泄露。 [#2169](https://github.com/gnembon/fabric-carpet/issues/2169)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `BUGFIX`

## 流体永不传播 (liquidNeverSpread)

使流体不会向四周和下方传播。  
liquid_source: 只有流体源不会传播。  
true: 流体源和流体都不会传播。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `liquid_source`
- 分类: `IGNY`, `FEATURE`

## 超级效果等级 (superEffectLevel)

让使用/effect指令给予的效果的最高等级为2147483647。  
设为默认后重新加载存档后生效。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 简易实体ID抑制器 (simpleEntityIDSuppression)

通过召唤一个指定名称的假玩家来制作基于创建实体时抛出“IllegalStateException”的更新抑制器。  
规则值为false时，规则不生效。  
规则值为true时，召唤名为“eIDSuppression”的假玩家可以用来制作实体ID抑制器。  
规则值为其它字符串时，召唤名为为该字符串的假玩家可以用来制作实体ID抑制器。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 实体ID抑制器白名单 (entityIDSuppressionWhitelist)

不在列表内的实体创建不会触发实体ID抑制。  
#all: 任意实体  
#none: 没有实体  
每个实体以`,`（英文逗号）分割

- 类型: `string`
- 默认值: `#all`
- 参考选项: `#all`, `#none`, `minecraft:item`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 潜影盒中潜影盒 (shulkerBoxInShulkerBox)

使潜影盒中能塞入潜影盒。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 重新引入比较器复制 (comparatorDupeReintroduced) `MC>=26.2`

让比较器可以在改变模式的时候复制自身，回退了26.2-snapshot-2的更改。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`, `PORTING`

## 钻头铁砧 (drillAnvil)

当下落的铁砧掉落在无法挖掘破坏且爆炸抗性 <= 1200的方块上时，会将其清除并继续下落。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 透明梦魇方块 (transparentNightmarishBlock)

让试炼刷怪笼、宝库、紫水晶母岩和紫水晶芽及簇像幽灵方块一样，能被非玩家实体穿过，且活塞等方式移动方块时能穿过紫水晶母岩，无法被炸毁和挖掘，无头活塞收回也无法将其破坏。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 幽灵末影珍珠修复 (ghostEnderPearlFix) `MC>=1.21.2`

修复了线程竟态导致的末影珍珠双重持久化。 [MC-306936](https://bugs.mojang.com/browse/MC-306936)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `BUGFIX`

## 稳定的TNT爆炸 (stableTNTExplosion)

控制TNT爆炸在计算可破坏方块时使用的射线强度模式。

`false`: 使用原版随机值。  
`minimum`: 强制随机因子为 `0.0`。  
`average`: 强制随机因子为 `0.5`。  
`maximum`: 强制随机因子为 `1.0`。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `minimum`, `average`, `maximum`
- 分类: `IGNY`, `FEATURE`

## 更好的仙人掌扳手 (betterFlippinCactus)

使仙人掌可以扳动任何带有方向的方块，需开启flippinCactus规则。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 可阻挡的紫水晶 (blockableAmethyst)

紫水晶母岩生长出小型紫水晶芽时非水源的水方块可以阻挡其生成。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 恒速的漏斗矿车 (constantSpeedHopperMinecart)

漏斗矿车不会因为其内容物而减速，始终为无内容物时的速度。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CREATIVE`, `FEATURE`

## 快速铁砧破坏 (fastAnvilBreaking)

当铁砧落沙实体被非完整方块摧毁时，会把从它自身的y坐标到最高建造高度的铁砧落沙实体及铁砧方块清除并掉落。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 烟花拓展 (fireworksExtra)

让烟花的最高等级变为8级，可通过1张纸和1-8个火药合成不同等级的烟花。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 安全的玩家死亡掉落 (safePlayerDeathDrop) `🐛Beta`

当玩家死亡时掉落的物品不会向四处飞溅。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 烟花堆叠 (fireworksStacking) `🐛Beta`

使飞行时可以叠加多个烟花的加速效果，使玩家拥有更快的飞行速度。  
规则值为起作用的烟花数量上限，当等于-1时则无限制。

- 类型: `int`
- 默认值: `1`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 粘性树脂块 (stickyResinBlock) `🐛Beta` `MC>=1.21.4`

使树脂块像黏液块和蜂蜜块一样可粘连其他可推动方块，不与黏液块或蜂蜜块互相粘连。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 可再生细雪 (renewablePowderSnow) `🐛Beta`

滴水石锥(钟乳石)可像滴落水或熔岩一样滴落细雪，可被炼药锅收集。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 可链接的末影箱 (linkableEnderChest) `🐛Beta`

控制拥有相同自定义名称的末影箱是否共用一个存储空间(频道)。  
`false`: 保持原版行为。  
`onlyLink`: 拥有相同自定义名称的末影箱会共用一个存储空间，不再区分打开它的玩家是谁。  
`true`: 包含 `onlyLink` 行为，并允许比较器和漏斗等方块交互。  
不带自定义名称的末影箱保持原版逻辑，玩家打开末影箱可以操作自己的末影箱物品。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `onlyLink`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`
