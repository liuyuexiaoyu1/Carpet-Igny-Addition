package com.liuyue.igny.mixins.features.rule.compat.fapi;


import net.fabricmc.fabric.impl.networking.AbstractNetworkAddon;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AbstractNetworkAddon.class, priority = 999, remap = false)
public abstract class AbstractNetworkAddonMixin {
}
