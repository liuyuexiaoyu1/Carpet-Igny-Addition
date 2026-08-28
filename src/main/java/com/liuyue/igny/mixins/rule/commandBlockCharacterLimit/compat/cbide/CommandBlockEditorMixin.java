package com.liuyue.igny.mixins.rule.commandBlockCharacterLimit.compat.cbide;

import com.liuyue.igny.IGNYSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Restriction(conflict = @Condition("command-block-ide"))
@Mixin(targets = "arm32x/minecraft/commandblockide/client/gui/editor/CommandBlockEditor")
public class CommandBlockEditorMixin {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 32500))
    private int onInit(int value) {
        return IGNYSettings.COMMAND_BLOCK_CHARACTER_LIMIT.value() == 32500 ? value : IGNYSettings.COMMAND_BLOCK_CHARACTER_LIMIT.value();
    }
}