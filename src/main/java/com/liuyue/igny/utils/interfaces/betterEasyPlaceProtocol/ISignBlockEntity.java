package com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol;

import org.spongepowered.asm.mixin.Unique;

public interface ISignBlockEntity {
    @Unique
    boolean igny$isPendingWaxed();

    @Unique
    void igny$setPendingWaxed(boolean pending);
}
