## 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

### 监守者永不钻地 (wardenNeverDig)

监守者没有听到声音将不会使它钻地。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 潜影贝攻击玩家没有漂浮效果 (playerLevitationFreeShulkerBullet)

当玩家被潜影贝的子弹击中时不会拥有漂浮效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 守卫者攻击玩家没有挖掘疲劳 (playerMiningFatigueFreeGuardian)

当玩家在被守卫者或者远古守卫者锁定的时候不会拥有挖掘疲劳效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 显示规则变更历史 (showRuleChangeHistory)

在规则的值变更的时候，会记录并且在规则详情中显示操作者、操作时间、原始值。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

### 假玩家可以被推动 (fakePlayerCanPush)

假玩家受到其他玩家的碰撞时，不会移动（被推动）。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 湿海绵吸收岩浆 (wetSpongeCanAbsorbLava)

使湿海绵碰到岩浆可以吸收岩浆（会变为海绵）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 丢弃玩家末影箱物品指令权限 (commandPlayerEnderChestDrop)

控制玩家丢弃末影箱物品的权限等级
用法: /player <玩家名> drop all - 丢弃背包+末影箱物品(如有权限)，用法: /player <玩家名> drop inventory 仅丢弃背包物品;  /player <玩家名> drop enderchest - 仅丢弃末影箱物品，无权限时: 'all' 仅丢弃背包物品，否则: 'all' 同时丢弃背包和末影箱物品，真人玩家: 只有OP可以丢弃其末影箱物品，假玩家: 遵循上述权限设置。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

### 玩家在监守者附近不会被给予黑暗效果 (noWardenDarkness)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 悬空冰破坏产生水 (floatingIceWater)

当冰下没有方块时，不使用精准采集破坏冰也能生成水。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 僵尸猪灵不会自然生成在下界传送门 (noZombifiedPiglinNetherPortalSpawn)

当主世界的下界传送门接收到随机刻时，僵尸猪灵将不会自然生成在主世界的下界传送门中。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 重新引入投掷物复制 (projectileDuplicationReintroduced) `MC>=1.21.2`

重新引入1.21.2以下的投掷物残留刻复制行为（可视作恢复药水、鸡蛋、雪球等复制行为）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### 重新引入骷髅捡剑 (skeletonsPickupSwordsReintroduced) `MC>=1.21.4`

重新引入1.21.4以下的骷髅捡剑行为。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### 重新引入矿车跨纬度传递动量 (minecartMotionFix) `MC>=1.21.2`

重新引入1.21-1.21.1版本中矿车携带乘客跨纬度会在传送tick给予乘客矿车的动量到1.21.2以上版本。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### TNT矿车空伤害来源修复 (tntMinecartEmptyDamageSourceFix) `MC<1.21.9`

修复TNT矿车引爆时传入的伤害来源为null，导致TNT矿车无法继承伤害来源的属性（可视作重新引入TNT矿车掠夺）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### 假玩家乘船不纠正偏航角修复 (fakePlayerBoatYawFix)

假玩家在骑乘船时不会纠正偏航角。 [#2100](https://github.com/gnembon/fabric-carpet/issues/2100)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`, `BUGFIX`

### 移除假玩家时移除载具 (killFakePlayerRemoveVehicle)

移除假玩家时移除其乘坐的载具。
cantrade：当载具上没有村民或者流浪商人时移除载具，其他实体保持默认。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `cantrade`, `true`
- 分类: `IGNY`, `FEATURE`

### 蜡烛可放在不完整方块上 (candlePlaceOnIncompleteBlock)

蜡烛可直接放在上表面不完整方块上。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### 音符盒音高指令权限 (commandfixnotepitch)

音符盒音高指令权限。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `CREATIVE`,`FEATURE`

### Fixnotepitch指令产生方块更新 (fixnotepitchUpdateBlock)

控制`commandFixnotepitech`规则是否产生方块更新。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `COMMAND`, `CREATIVE`, `FEATURE`

### 快乐恶魂无碰撞 (happyGhastNoClip) `MC>1.21.6`

快乐恶魂有玩家骑乘时无视方块碰撞。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `CLIENT`, `FEATURE`

### 只有玩家实体创建下界传送门 (onlyPlayerCreateNetherPortal)

只有玩家实体可以创建下界传送门。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### 没有凋零效果 (noWitherEffect)

凋零、凋零骷髅、凋零玫瑰不能给予玩家凋零效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### 设置默认参数权限 (setDefaultArgument)

在`/carpet <rule> <value> <setDefault>`指令中，控制`setDefault`参数的指令权限，用于让规则直接设为默认。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

### 重新引入僵尸猪灵在愤怒时掉落战利品 (zombifiedPiglinDropLootIfAngryReintroduced) `MC>=1.21.5`

开启后，僵尸猪灵在愤怒、设置玩家为攻击目标的100gt内死亡，将可掉落如被玩家击杀一样的掉落物和经验球。

它将相关表现还原回了 MC < 25w02a（1.21.5 的快照）的表现，撤销了 MC-56653 的修复。

此功能来自Carpet Tis Addition，将游戏版本1.21.9+的zombifiedPiglinDropLootIfAngryReintroduced规则向下移植到游戏版本1.21.5+。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### 定位栏无假玩家 (locatorBarNoFakePlayer) `MC>=1.21.6`

定位栏不显示假玩家。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### 假玩家登录登出无聊天信息 (fakePlayerLoginLogoutNoChatInfo)

假玩家登录登出不在聊天栏显示登录登出的提示。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

### 玩家动作指令 (commandPlayerOperate)

使用/playerManager命令来控制玩家动作。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

### 清除光照队列指令 (commandClearLightQueue)

使用/clearlightqueue命令来清除光照队列。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

### 假玩家无挖掘冷却 (fakePlayerNoBreakingCoolDown) `🐛Beta`

假玩家长按破坏无挖掘冷却。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`
