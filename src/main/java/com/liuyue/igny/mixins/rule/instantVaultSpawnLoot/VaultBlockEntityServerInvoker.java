package com.liuyue.igny.mixins.rule.instantVaultSpawnLoot;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VaultBlockEntity.Server.class)
public interface VaultBlockEntityServerInvoker {
    @Invoker("setVaultState")
    static void invokeSetVaultState(
            ServerLevel level,
            BlockPos pos,
            BlockState oldState,
            BlockState newState,
            VaultConfig config,
            VaultSharedData sharedData
    ) {
        throw new UnsupportedOperationException("Mixin method not implemented");
    }
}
