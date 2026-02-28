// MIT License
//
// Copyright (c) 2025 Melationin
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.liuyue.igny.mixins.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.optimizations.IEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity {

    @Shadow public int tickCount;

    @Shadow public abstract EntityType<?> getType();

    @Shadow public abstract AABB getBoundingBox();

    @Shadow private Level level;

    @Shadow public abstract int getId();

    @Unique
    private int carpet_Igny_Addition$crammingCount = 0;

    @Override
    public int carpet_Igny_Addition$getCrammingCount() {
        return this.carpet_Igny_Addition$crammingCount;
    }

    @Override
    public void carpet_Igny_Addition$setCrammingCount(int count) {
        this.carpet_Igny_Addition$crammingCount = count;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.level.isClientSide()) return;
        ResourceLocation resourceLocation = EntityType.getKey(this.getType());
        String entityTypeName = resourceLocation.toString();
        if (IGNYSettings.optimizedEntityList.equals("#none") || !IGNYSettings.CRAMMING_ENTITIES.contains(entityTypeName)) {
            this.carpet_Igny_Addition$setCrammingCount(0);
            return;
        }
        if ((this.tickCount + this.getId()) % 100 == 0) {
            this.carpet_Igny_Addition$setCrammingCount(this.level.getEntities(this.getType(), this.getBoundingBox(), entity -> true).size());
        }
    }

    @Inject(method = "move", at = @At(value = "HEAD"), cancellable = true)
    private void move(MoverType moverType, Vec3 vec3, CallbackInfo ci){
        if (this.carpet_Igny_Addition$crammingCount >= IGNYSettings.optimizedEntityLimit){
            ci.cancel();
        }
    }
}