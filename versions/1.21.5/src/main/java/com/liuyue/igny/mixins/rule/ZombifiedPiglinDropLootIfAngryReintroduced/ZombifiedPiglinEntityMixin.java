package com.liuyue.igny.mixins.rule.ZombifiedPiglinDropLootIfAngryReintroduced;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombifiedPiglin.class)
public class ZombifiedPiglinEntityMixin extends Zombie
{
    public ZombifiedPiglinEntityMixin(EntityType<? extends Zombie> entityType, Level world)
    {
        super(entityType, world);
    }

    @Unique
    private static final int ZPDIAR_PLAYER_HURT_EXPERIENCE_TIME = 100;

    @Inject(
            method = "customServerAiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Zombie;customServerAiStep(Lnet/minecraft/server/level/ServerLevel;)V"
            )
    )
    private void zombifiedPiglinDropLootIfAngryReintroduced_updatePlayerHitTimer(CallbackInfo ci)
    {
        if (IGNYSettings.ZombifiedPiglinDropLootIfAngryReintroduced)
        {
            this.lastHurtByPlayerMemoryTime = ZPDIAR_PLAYER_HURT_EXPERIENCE_TIME;
        }
    }

    @Inject(
            method = "setTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Zombie;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"
            )
    )
    private void zombifiedPiglinDropLootIfAngryReintroduced_setAttackingIfTargetIsPlayer(LivingEntity target, CallbackInfo ci)
    {
        if (IGNYSettings.ZombifiedPiglinDropLootIfAngryReintroduced)
        {
            if (target instanceof Player player)
            {
                this.setLastHurtByPlayer(player, ZPDIAR_PLAYER_HURT_EXPERIENCE_TIME);
            }
        }
    }
}