# Carpet-Igny-Addition

[![License](https://img.shields.io/github/license/liuyuexiaoyu1/Carpet-Igny-Addition)](https://choosealicense.com/licenses/mit/)

**中文** | [English](README_en.md)

## 前置模组

| 名称          | 类型 | 链接                                                                                                                                                                       |
|-------------|----|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Carpet      | 必须 | [MC百科](https://www.mcmod.cn/class/2361.html) &#124; [Modrinth](https://modrinth.com/mod/carpet) &#124; [CurseForge](https://www.curseforge.com/minecraft/mc-mods/carpet) |
| MixinExtras | 内置 | [MC百科](https://www.mcmod.cn/class/12750.html)                                                                                                                            |
| Fabric API  | 必须 | [MC百科](https://www.mcmod.cn/class/3124.html) &#124; [官方](https://fabricmc.net/)                                                                                          |

## 版本支持

| 游戏版本           | 开发状态 | 最后支持版本                                                                                                 |  
|----------------|------|--------------------------------------------------------------------------------------------------------|
| 1.21(.1)（主版本）  | 维护中  | -                                                                                                      |
| 1.21.2 ~ 最新正式版 | 维护中  | -                                                                                                      |

## 规则

### 监守者永不钻地 (WardenNeverDig)

监守者没有听到声音将不会使它钻地。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 潜影贝攻击玩家没有漂浮效果 (PlayerLevitationFreeShulkerBullet)

当玩家被潜影贝的子弹击中时不会拥有漂浮效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 守卫者攻击玩家没有挖掘疲劳 (PlayerMiningFatigueFreeGuardian)

当玩家在被守卫者或者远古守卫者攻击的时候不会拥有挖掘疲劳效果。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 显示规则变更历史 (ShowRuleChangeHistory)

在规则的值变更的时候，会记录并且在规则详情中显示操作者、操作时间、原始值。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`

### 假玩家可以被推动 (FakePlayerCanPush)

假玩家受到其他玩家的碰撞时，不会移动（被推动）。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 湿海绵吸收岩浆 (WetSpongeCanAbsorbLava)

使湿海绵碰到岩浆可以吸收岩浆（会变为海绵）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 丢弃玩家末影箱物品指令权限 (CommandPlayerEnderChestDrop)

控制玩家丢弃末影箱物品的权限等级
用法: /player <玩家名> drop all - 丢弃背包+末影箱物品(如有权限)，用法: /player <玩家名> drop inventory 仅丢弃背包物品;  /player <玩家名> drop enderchest - 仅丢弃末影箱物品，无权限时: 'all' 仅丢弃背包物品，否则: 'all' 同时丢弃背包和末影箱物品，真人玩家: 只有OP可以丢弃其末影箱物品，假玩家: 遵循上述权限设置。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `IGNY`, `COMMAND`, `FEATURE`

### 玩家在监守者附近不会被给予黑暗效果 (NoWardenDarkness)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 悬空冰破坏产生水 (FloatingIceWater)

当冰下没有方块时，不使用精准采集破坏冰也能生成水。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 僵尸猪灵不会自然生成在下界传送门 (NoZombifiedPiglinNetherPortalSpawn)

当难度不为和平时，僵尸猪灵将不能自然生成在下界传送门中。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `SURVIVAL`, `FEATURE`

### 重新引入投掷物复制 (ProjectileDuplicationReintroduced) `MC>=1.21.2`

重新引入1.21.2以下的投掷物残留刻复制行为（可视作恢复药水、鸡蛋、雪球等复制行为）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `IGNY`, `FEATURE`