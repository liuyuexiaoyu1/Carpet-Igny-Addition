package com.liuyue.igny.mixins.rule.fakePlayerNoCollision;

import com.liuyue.igny.utils.interfaces.fakePlayerNoCollision.NoCollisionFlag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin implements NoCollisionFlag {
    @Shadow
    @Final
    protected static EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION;

    @Unique
    private static final byte IGNY_NO_COLLISION_MASK = (byte) 0x80;

    @Override
    public boolean igny$isNoCollision() {
        return (this.igny$getCustomisation() & IGNY_NO_COLLISION_MASK) != 0;
    }

    @Override
    public void igny$setNoCollision(boolean value) {
        byte current = this.igny$getCustomisation();
        byte wanted = value ? (byte) (current | IGNY_NO_COLLISION_MASK) : (byte) (current & ~IGNY_NO_COLLISION_MASK);
        if (current != wanted) {
            ((Player) (Object) this).getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, wanted);
        }
    }

    @Unique
    private byte igny$getCustomisation() {
        return ((Player) (Object) this).getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION);
    }
}
