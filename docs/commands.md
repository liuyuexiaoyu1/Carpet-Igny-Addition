## 命令

## 音符盒音高设置 (`/fixnotepitch`)

### 语法
`/fixnotepitch <pos1> <pos2> [<pitch>]`
- 参数`<pitch>`是可选的，取值范围为0~24，默认值为0

### 效果
- 将`<pos1>`和`<pos2>`组成的立方体区域的音符盒音高设置为`<pitch>`

## 玩家动作 (`/playerOperate`)

### 语法
- `/playerOperate ..`
    - `...<player>`
        - `...task`
            - `...vault` `MC>=1.20.3`
            - `...pressUse`
        - `...stop`
        - `..pause`
        - `...resume`
    - `...list` 
    - `...stopAll`
    - `...pauseAll` 
    - `...resumeAll`

### 效果
- `/playerOperate ..`
    - `...<player>` 假玩家。
        - `...task`
            - `...vault [<maxCycles>]` 使假玩家执行开宝库的任务 `MC>=1.20.3`
                 - 使`<player>`长按右键100游戏刻后下线，并在游戏刻21刻后召唤一个`<player>_1`假人，视角和坐标不变，`<player>_1`假人继续长按右键100刻后下线，21刻后召唤`<player>_2`，一直循环到`<player>_[<maxCycles>]`，`[<maxCycles>]`默认为130。
            - `...pressUse <interval> <duration> [<cycles>]` 使假玩家间隔`<interval>`tick长按右键`<duration>`tick，重复`[<cycles>]`次，`[<cycles>]`默认为Infinite，当`[<cycles>]`为1时，`<interval>`值无用。
        - `...stop` 停止该玩家的任务。
        - `...pause` 暂停该玩家的任务
        - `...resume` 继续该玩家的任务（已暂停）
    - `...list` 查看所有任务列表。
    - `...stopAll` 停止所有任务。
    - `...pauseAll` 暂停所有任务。
    - `...resumeAll` 继续所有任务。

## 清空光照队列 (`/clearlightqueue`)

### 语法
- `/clearlightqueue`

### 效果
- 直接清空待处理的光照队列。

## 自定义玩家拾取物品 (`/customPlayerPickupItem`)

### 语法
- `/customPlayerPickupItem`
    - `...<player>`
        - `...get`
        - `...mode`
            - `disable`
            - `whitelist`
            - `blacklist`
        - `...items`
            - `add <item>`
            - `remove <item>`
            - `clear`

### 效果
- `/customPlayerPickupItem` 设置自定义的物品拾取过滤规则。
    - `...<player>` 目标玩家。
        - `...get` 查看目标玩家当前的拾取自定义物品。
        - `...mode` 设置拾取模式。
            - `disable`: 禁用过滤，玩家可以拾取所有物品。
            - `whitelist`: 白名单模式，玩家只能拾取列表中的物品。
            - `blacklist`: 黑名单模式，玩家不能拾取列表中的物品。
        - `...items` 物品列表管理。
            - `add <item>` 向列表中添加一个物品。
            - `remove <item>` 从列表中移除一个物品。
            - `clear` 清空当前玩家的所有过滤物品设置。

## 自定义物品堆叠上限 (`/customItemMaxStackSize`)

### 语法
- `/customItemMaxStackSize`
    - `...set`
        - `<item>`
        - `<count>`
    - `...remove`
        - `<item>` 
    - `...clear`
    - `...list`

### 效果
- `/customItemMaxStackSize` 修改指定物品在游戏中的最大堆叠数量。
    - `...set` 设置指定物品的最大堆叠上限。
        - `<item>`: 目标物品 ID。
        - `<count>`: 设置的堆叠值，取值范围为 1~99。
    - `...remove` 移除指定物品的自定义堆叠设置。
        - `<item>`: 目标物品 ID。
    - `...clear` 清空所有已设置的自定义物品堆叠规则。
    - `...list` 列出当前所有已生效的自定义堆叠设置。
        - 鼠标指针悬浮在物品名称上可查看其完整物品ID。