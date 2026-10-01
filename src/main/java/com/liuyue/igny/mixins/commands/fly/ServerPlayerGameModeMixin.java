package com.liuyue.igny.mixins.commands.fly;

import com.liuyue.igny.utils.interfaces.flyCommand.FlyState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;

    @Redirect(
            method = "setGameModeForPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameType;updatePlayerAbilities(Lnet/minecraft/world/entity/player/Abilities;)V"
            )
    )
    private void igny$keepFlyAbility(GameType gameType, Abilities abilities) {
        boolean wasFlying = abilities.flying;
        gameType.updatePlayerAbilities(abilities);
        if (gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) {
            return;
        }
        if (((FlyState) this.player).igny$isFlyCommandOn()) {
            abilities.mayfly = true;
            abilities.flying = wasFlying;
        }
    }
}
