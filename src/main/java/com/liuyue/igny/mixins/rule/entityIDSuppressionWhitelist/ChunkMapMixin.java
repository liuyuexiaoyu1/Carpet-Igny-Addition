package com.liuyue.igny.mixins.rule.entityIDSuppressionWhitelist;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChunkMap.class, priority = 1001)
public class ChunkMapMixin {
    @WrapOperation(method = "addEntity", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;containsKey(I)Z"))
    private boolean containsKey(Int2ObjectMap<?> instance, int i, Operation<Boolean> original, @Local(argsOnly = true) Entity entity) {
        ResourceLocation resourceLocation = EntityType.getKey(entity.getType());
        String entityTypeName = resourceLocation.toString();
        if (IGNYSettings.ENTITY_ID_SUPPRESSION_WHITELIST.value().equals("#all")) {
            return original.call(instance, i);
        }
        if (IGNYSettings.ENTITY_ID_SUPPRESSION_WHITELIST.value().equals("#none") || !IGNYSettings.EIDWhitelist.contains(entityTypeName)) {
            return false;
        }
        return original.call(instance, i);
    }
}
