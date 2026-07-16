package com.liuyue.igny.mixins.rule.emptyChatScreenCanCompletePlayerName;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionsMixin {
    @WrapOperation(method = "updateCommandInfo", at = @At(value = "INVOKE", target = "Ljava/lang/String;isBlank()Z"))
    private boolean isBlank(String instance, Operation<Boolean> original) {
        if (IGNYSettings.EMPTY_CHAT_SCREEN_CAN_COMPLETE_PLAYER_NAME.value()) {
            return false;
        }
        return original.call(instance);
    }

    @Inject(method = "updateCommandInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;messagesAllowed:Z", opcode = Opcodes.GETFIELD), cancellable = true)
    private void updateCommandInfo(CallbackInfo ci, @Local(name = "command") String command) {
        if (command.isBlank()) {
            ci.cancel();
        }
    }
}
