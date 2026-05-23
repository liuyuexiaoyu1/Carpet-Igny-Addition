package com.liuyue.igny.mixins.rule.simpleEntityIDSuppression;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.RuleUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @WrapOperation(method = "addEntity", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;containsKey(I)Z"))
    private boolean containsKey(Int2ObjectMap<?> instance, int i, Operation<Boolean> original, @Local(argsOnly = true) Entity entity) {
        if (!"false".equalsIgnoreCase(IGNYSettings.SIMPLE_ENTITY_ID_SUPPRESSION.value())) {
            MinecraftServer server = entity.level().getServer();
            if (server == null) {
                return original.call(instance, i);
            }
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player instanceof EntityPlayerMPFake) {
                    if (player == entity) {
                        break;
                    }
                    if (RuleUtil.canEntityIDSuppression(player)) {
                        return true;
                    }
                }
            }
        }
        return original.call(instance, i);
    }
}
