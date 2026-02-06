# 记录器

## 活塞 (piston)

`/log piston`

记录活塞与黏性活塞的以下行为：

- 推出
- 拉回
- 收回
- 推出失败
- 拉回失败

属性:

- 默认选项: N/A
- 参考选项: N/A

![logger_piston](./img/loggers/logger_piston.png "logger_piston")

## 熔炉 (allFurnace) `[迁移]` `来自furnaceHasIncombustibleHighlight规则`

`/log allFurnace`

- 当熔炉的原料槽中含有不可燃烧的物品时，自身颜色变为淡红色，可透过方块显示。
- **需要客户端同时安装有此MOD。**

属性:

- 默认选项: N/A
- 参考选项: N/A

## 信标 (beacon)

`/log beacon`

实时渲染信标的范围框。

- 当信标范围发生变动、展开或收回时，渲染框会伴随平滑的过渡动画。
- 大范围渲染可能在低端设备上产生轻微掉帧。
- **需要客户端同时安装有此MOD。**

属性:

- 默认选项: N/A
- 参考选项: N/A