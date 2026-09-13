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
            - `...vault [<maxCycles>] [<onlineDuration>] [<waitingDuration>]` `MC>=1.20.3`
            - `...pressUse <interval> <duration> [<cycles>]`
            - `...rotation <interval> <angle>`
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
                - Makes `<player>` hold right-click for `<onlineDuration>` game ticks then log out, and spawns a `<player>_1` fake player after `<waitingDuration>` game ticks with the same position and rotation. `<player>_1` continues holding right-click for `<onlineDuration>` ticks then logs out, spawns `<player>_2` after `<waitingDuration>` ticks, looping until `<player>_[<maxCycles>]`.
                - `[<maxCycles>]` defaults to 130, `[<onlineDuration>]` defaults to 100, `[<waitingDuration>]` defaults to 21.
            - `...pressUse <interval> <duration> [<cycles>]`: Makes the fake player repeatedly hold right-click for `<duration>` ticks every `<interval>` ticks, repeating for `[<cycles>]` times. If `[<cycles>]` is omitted, it defaults to infinite repetition. When `[<cycles>]` is 1, the `<interval>` value is ignored.
            - `...rotation <interval> <angle>` Makes the fake player rotate `<angle>` degrees every `<interval>` ticks, rotating clockwise.
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
        - `<itemStack>`: The target item.
        - `<count>`: The stack size to set, ranging from 1 to 99.
    - `...remove` Removes the custom stack size setting for a specified item.
        - `<itemStack>`: The target item.
    - `...clear` Clears all configured custom stack size rules.
    - `...list` Lists all currently active custom stack size settings.
        - Cursor hover over the item name to view its full ResourceLocation (Namespace ID).

## Insane Behaviors (`/insanebehaviors`)

Requires the `insaneBehaviors` rule to be enabled (not `off`); access is controlled by the `commandInsaneBehaviors` rule.

### Syntax
- `/insanebehaviors`
    - `...reset`
    - `...getstate`
    - `...setstate <resolution> <counter>`

### Effects
- `/insanebehaviors` reads or writes the global iteration state of the `insaneBehaviors` rule.
    - `...reset` resets the resolution and counter to their defaults (resolution `2`, counter `0`).
    - `...getstate` prints the current resolution and counter.
    - `...setstate <resolution> <counter>` writes the resolution and counter directly.
        - `<resolution>` is an integer of at least `2`, i.e. the refinement level of the current "grid".
        - `<counter>` is an integer of at least `0`, i.e. the position already reached at that resolution.
- Note that `insaneBehaviors` works on a **global** iterator: any triggering event steps through an iteration for all insaneBehaviors events at once, so writing the state by hand affects every related event.
- This rule family is ported from JoaCarpet.

## Item Flow Tracker (`/itemflowtracker`)

Requires the `itemFlowTracker` rule to be enabled.

### Syntax
- `/itemflowtracker`
    - `...mark`
        - `...<colour>`
            - `[<track_path>]`
            - `...<targets>`
                - `[<track_path>]`
        - `...hex <rrggbb>`
            - `[<track_path>]`
            - `...<targets>`
                - `[<track_path>]`
    - `...status`
    - `...clear`

### Effects
- `/itemflowtracker` manages the item flow tracking sessions.
    - `...mark` starts a tracking session with the given colour.
        - `...<colour>` is one of the dye names: `white`, `orange`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `light_gray`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`.
        - `...<targets>` marks the specified dropped item entities in the world; when omitted the item in your main hand is marked.
        - `...hex <rrggbb>` uses a custom colour; `<rrggbb>` may be `ff8800` or `0xff8800`, and `#` has to be quoted.
        - `[<track_path>]` is the trail sampling interval in ticks; it defaults to 5, and 0 turns the trail off.
    - `...status` lists every live tracking session and its remaining budget.
    - `...clear` stops every tracking session and removes all highlights.
