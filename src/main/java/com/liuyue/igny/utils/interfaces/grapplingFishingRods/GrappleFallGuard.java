package com.liuyue.igny.utils.interfaces.grapplingFishingRods;

import net.minecraft.world.phys.Vec3;

public interface GrappleFallGuard {
    void igny$startGrappleImpulse(Vec3 impactPos);
    boolean igny$isGrappleImpulseActive();
    Vec3 igny$getGrappleImpactPos();
    void igny$tickGrappleImpulse();
}