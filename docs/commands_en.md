## Commands

### Fixnotepitch (`fixnotepitch`)

### Syntax
`/fixnotepitch <pos1> <pos2> [<pitch>]`
- The parameter `<pitch>` is optional, with a valid range of 0–24. The default value is 0.

### Effect
- Sets the note pitch of all note blocks within the cuboid region defined by `<pos1>` and `<pos2>` to `<pitch>`.

## Player Operation (`/playerOperate`)
### Syntax
- `/playerOperate ..`
    - `...<player>`
        - `...task`
            - `...vault [<maxCycles>]`  `MC>=1.20.3`
            - `...pressUse <interval> <duration> [<cycles>]`
        - `...stop`
        - `...pause`
        - `...resume`
    - `...list`
    - `...stopAll`
    - `...pauseAll`
    - `...resumeAll`

### Effects
- `/playerOperate ..`
    - `...<player>`: Fake player.
        - `...task`
            - `...vault [<maxCycles>]`: Makes the fake player perform a vault-opening task.
                - The `<player>` holds right-click for 100 game ticks, then disconnects. After 21 game ticks, a new fake player named `<player>_1` is spawned at the same position and orientation, continuing to hold right-click for another 100 ticks before disconnecting. This cycle repeats, spawning `<player>_2`, `<player>_3`, etc., up to `<player>_[<maxCycles>]`. The default value of `[<maxCycles>]` is 130.
            - `...pressUse <interval> <duration> [<cycles>]`: Makes the fake player repeatedly hold right-click for `<duration>` ticks every `<interval>` ticks, repeating for `[<cycles>]` times. If `[<cycles>]` is omitted, it defaults to infinite repetition. When `[<cycles>]` is 1, the `<interval>` value is ignored.
        - `...stop`: Stops all tasks for this player.
        - `...pause`: Pauses the player's current task.
        - `...resume`: Resumes the player's paused task.
    - `...list`: Lists all active tasks.
    - `...stopAll`: Stops all tasks.
    - `...pauseAll`: Pauses all running tasks.
    - `...resumeAll`: Resumes all paused tasks.

## ClearLightQueue

### Syntax
- `/clearlightqueue`

### Effects
- Clear the pending lighting queue directly.

## Custom Player Item Pickup (`/customPlayerPickupItem`)

### Syntax
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

### Effects
- `/customPlayerPickupItem` Configures custom item pickup filtering rules.
    - `...<player>` The target player.
        - `...get` View the target player's current pickup configuration.
        - `...mode` Set the pickup filtering mode.
            - `disable`: Disables filtering; the player can pick up all items.
            - `whitelist`: Whitelist mode; the player can only pick up items in the list.
            - `blacklist`: Blacklist mode; the player cannot pick up items in the list.
        - `...items` Manage the filtered item list.
            - `add <item>` Add an item to the list.
            - `remove <item>` Remove an item from the list.
            - `clear` Clear all filtered items for the current player.

## Custom Item Max Stack Size (`/customItemMaxStackSize`)

### Syntax
- `/customItemMaxStackSize`
    - `...set`
        - `<item>`
        - `<count>`
    - `...remove`
        - `<item>`
    - `...clear`
    - `...list`

### Effect
- `/customItemMaxStackSize` Modifies the maximum stack size for specific items.
    - `...set` Sets the custom max stack size for a specified item.
        - `<item>`: The target item ID.
        - `<count>`: The stack size to set, ranging from 1 to 99.
    - `...remove` Removes the custom stack size setting for a specified item.
        - `<item>`: The target item ID.
    - `...clear` Clears all configured custom stack size rules.
    - `...list` Lists all currently active custom stack size settings.
        - Cursor hover over the item name to view its full ResourceLocation (Namespace ID).