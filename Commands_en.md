## Commands

### Fixnotepitch

### Syntax
`/fixnotepitch <pos1> <pos2> [<pitch>]`
- The parameter `<pitch>` is optional, with a valid range of 0–24. The default value is 0.

### Effect
- Sets the note pitch of all note blocks within the cuboid region defined by `<pos1>` and `<pos2>` to `<pitch>`.

## PlayerOperate

### Syntax
- `/playerOperate <player>/list ..`
    - `...vault`
    - `...stop`

### Effects
- `/playerOperate <player>/list ..`
    - `...vault [<maxCycles>]` Make the fake player perform the task of opening the vault
        - Causes `<player>` to hold down the right mouse button for 100 game ticks, then log off. After 21 game ticks, a dummy named `<player>_1` is summoned at the same position and orientation as `<player>`. This dummy continues holding the right mouse button for another 100 ticks before logging off, and after another 21 ticks, summons `<player>_2`. This cycle repeats until reaching `<player>_[<maxCycles>]`. The default value of `[<maxCycles>]` is 130.
    - `...pressUse <interval> <duration> [<cycles>]` `🐛Beta` makes the fake player right-click and hold for `<duration>` ticks, then wait `<interval>` ticks before repeating. This cycle runs `[<cycles>]` times. If `[<cycles>]` is omitted, it defaults to Infinite. When `[<cycles>]` is 1, the `<interval>` value is ignored.
    - `...stop`
        - Cancels all ongoing tasks for the specified player.

## ClearLightQueue

### Syntax
- `/clearlightqueue`

### Effects
- Clear the pending lighting queue directly.