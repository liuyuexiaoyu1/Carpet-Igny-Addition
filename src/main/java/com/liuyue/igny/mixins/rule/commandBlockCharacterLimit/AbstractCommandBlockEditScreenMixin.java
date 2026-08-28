package com.liuyue.igny.mixins.rule.commandBlockCharacterLimit;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractCommandBlockEditScreen.class)
public class AbstractCommandBlockEditScreenMixin {
    @ModifyConstant(method = "init", constant = @Constant(intValue = 32500))
    private int init(int value) {
        return IGNYSettings.COMMAND_BLOCK_CHARACTER_LIMIT.value() == 32500 ? value : IGNYSettings.COMMAND_BLOCK_CHARACTER_LIMIT.value();
    }
}
