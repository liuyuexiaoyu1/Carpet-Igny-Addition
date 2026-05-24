package com.liuyue.igny.mixins.rule.invisibleItemFrames;

import com.liuyue.igny.IGNYSettings;
//#if MC > 12006
import net.minecraft.core.component.DataComponents;
//#endif
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;
import java.util.Optional;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    private final Entity self = (Entity) (Object) this;

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void invisible(CallbackInfoReturnable<Boolean> cir) {
        if (this.self instanceof ItemFrame itemFrame) {
            if (IGNYSettings.ITEM_FRAME_SAVE_NBT.get()) {
                return;
            }
            ItemStack itemStack = itemFrame.getItem();
            if (itemStack.isEmpty()) {
                return;
            }
            String ruleValue = IGNYSettings.INVISIBLE_ITEM_FRAMES.value();
            switch (ruleValue.toLowerCase(Locale.ROOT)) {
                case "false" -> {}
                case "true" -> cir.setReturnValue(true);
                default -> Optional.ofNullable(
                        //#if MC > 12006
                        itemStack.get(DataComponents.CUSTOM_NAME)
                        //#else
                        //$$ itemStack.getDisplayName()
                        //#endif
                        )
                        .map(Component::getString)
                        .ifPresent(name -> {
                            if (name.equalsIgnoreCase(ruleValue)) {
                                cir.setReturnValue(true);
                            }
                        });
            }
        }
    }
}
