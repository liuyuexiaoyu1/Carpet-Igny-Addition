package com.liuyue.igny.mixins.rule.projectileDuplicationReintroduced;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityInvoker {

    @Invoker("applyGravity")
    void invokeApplyGravity();

    //#if MC>=12105
    //$$ @Invoker("applyEffectsFromBlocks")
    //$$ void invokeApplyEffectsFromBlocks();
    //#endif

}
