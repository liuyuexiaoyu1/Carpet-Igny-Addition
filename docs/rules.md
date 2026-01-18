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

在规则的值变更的时候，会记录并且在规则详情中显示操作者、操作时间、原始值。

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
- 分类: `IGNY`, `FEATURE`

## 重新引入骷髅捡剑 (skeletonsPickupSwordsReintroduced) `MC>=1.21.4`

重新引入1.21.4以下的骷髅捡剑行为。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

## 重新引入矿车传送动量继承 (teleportInheritMinecartsMotionReintroduced) `MC>=1.21.2`

重新引入1.21-1.21.1版本中矿车携带乘客跨纬度会在传送tick给予乘客矿车的动量到1.21.2以上版本。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

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

优化堆叠的生物实体，每个实体以`,`（英文逗号）分割，设为'#none'则不启用。

它关闭了生物实体大部分移动和挤压的计算，使其性能更好。

- 类型: `string`
- 默认值: `#none`
- 参考选项: `#none`, `minecraft:warden`, `minecraft:piglin`, `minecraft:warden,minecraft:piglin`
- 分类: `IGNY`, `OPTIMIZATION`, `FEATURE`

## 实体优化限制 (optimizedEntityLimit) 

当堆叠的实体个数达到规则设置的值时启用优化，optimizedEntityList规则需包含这个实体。

- 类型: `int`
- 默认值: `100`
- 分类: `IGNY`, `OPTIMIZATION`, `FEATURE`

## optimizedTNT错误作用域修复 (optimizedTNTErrorScopeFix)

修复optimizedTNT规则启用时，错误优化风弹等实体爆炸的问题。 [#1928](https://github.com/gnembon/fabric-carpet/issues/1928)

### 可能会与一些多线程优化模组冲突！！

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `BUGFIX`

## 熔炉含不可燃烧物高亮 (furnaceHasIncombustibleHighlight) 

当熔炉的原料槽中含有不可燃烧的物品时，自身颜色变为淡红色，可透过方块显示。

高亮未生效需重新加载熔炉所在区块。

需要客户端同时安装有此MOD。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `CLIENT`, `SURVIVAL`, `FEATURE`

## 自定义玩家拾取物品指令权限 (commandCustomPlayerPickupItem) 

使用/customPlayerPickupItem来自定义玩家能拾取的物品。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`
  
## 自定义物品最大堆叠数量指令权限 (commandCustomItemMaxStackSize) 

使用/customItemMaxStackSize来自定义物品的最大堆叠数量。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

## 玩家饥饿值不减少 (playerHungryValueNoDecrease) `🐛Beta`

玩家饥饿值不会减少。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

## 玩家饥饿值低可疾跑 (playerLowHungryValueCanSprint) `🐛Beta`

当玩家饥饿值低于7点时，可以疾跑。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `CLIENT`, `FEATURE`
