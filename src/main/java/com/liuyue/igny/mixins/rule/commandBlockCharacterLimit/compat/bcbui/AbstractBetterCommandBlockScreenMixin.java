package com.liuyue.igny.mixins.rule.commandBlockCharacterLimit.compat.bcbui;

import com.liuyue.igny.IGNYSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Restriction(conflict = @Condition("bettercommandblockui"))
@Mixin(targets = "bettercommandblockui/main/ui/screen/AbstractBetterCommandBlockScreen")
public class AbstractBetterCommandBlockScreenMixin {
    @ModifyConstant(method = "init", constant = @Constant(intValue = 32500))
    private int init(int value) {
        return IGNYSettings.COMMAND_BLOCK_CHARACTER_LIMIT.value() == 32500 ? value : IGNYSettings.COMMAND_BLOCK_CHARACTER_LIMIT.value();
    }
}