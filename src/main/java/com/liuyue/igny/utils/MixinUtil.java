package com.liuyue.igny.utils;

import carpet.utils.Messenger;
import com.liuyue.igny.IGNYServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class MixinUtil {
    public static boolean audit(@Nullable CommandSourceStack source) {
        boolean ok;

        Component response;
        try {
            MixinEnvironment.getCurrentEnvironment().audit();
            response = Messenger.s("Mixin environment audited successfully");
            ok = true;
        } catch (Exception e) {
            IGNYServer.LOGGER.error("Error when auditing mixin", e);
            response = Messenger.s(String.format("Mixin environment auditing failed, check console for more information (%s)", e));
            ok = false;
        }
        if (source != null) {
            Messenger.m(source, response);
        }
        return ok;
    }
}