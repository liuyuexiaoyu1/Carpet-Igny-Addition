// MIT License
//
// Copyright (c) 2026 R-Matrix
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

package com.liuyue.igny.helper.tridentMultipleDamage;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class PiercingCollisionHelper {
    public static float getToleranceMargin(Entity entity) {
        return Math.max(0.0F, Math.min(0.3F, (entity.tickCount - 2) / 20.0F));
    }

    public static Collection<EntityHitResult> collect(Level level, Entity entity, Vec3 from, Vec3 to, AABB box, Predicate<Entity> hitPredicate) {
        return collect(level, entity, from, to, box, hitPredicate, getToleranceMargin(entity));
    }

    public static Collection<EntityHitResult> collect(Level level, Entity entity, Vec3 from, Vec3 to, AABB box, Predicate<Entity> hitPredicate, float hitboxMargin) {
        List<EntityHitResult> list = new ArrayList<>();

        for (Entity target : level.getEntities(entity, box, hitPredicate)) {
            AABB targetBox = target.getBoundingBox().inflate(hitboxMargin);
            if (targetBox.contains(from)) {
                list.add(new EntityHitResult(target, from));
            } else {
                Optional<Vec3> hitPos = targetBox.clip(from, to);
                hitPos.ifPresent(pos -> list.add(new EntityHitResult(target, pos)));
            }
        }

        return list;
    }
}
