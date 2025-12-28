package com.liuyue.igny.client.renderer.substitute;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class WorldRenderEvents {
    public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, listeners -> () -> {
        for (Start start : listeners) {
            start.onStart();
        }
    });

    public static final Event<BeforeDebugRender> BEFORE_DEBUG_RENDER = EventFactory.createArrayBacked(BeforeDebugRender.class, listeners -> context -> {
        for (BeforeDebugRender render : listeners) {
            render.render(context);
        }
    });

    public static final Event<AfterTranslucent> AFTER_TRANSLUCENT = EventFactory.createArrayBacked(AfterTranslucent.class, listeners -> context -> {
        for (AfterTranslucent after : listeners) {
            after.render(context);
        }
    });

    public interface BeforeDebugRender {
        void render(WorldRenderContext context);
    }

    public interface AfterTranslucent {
        void render(WorldRenderContext context);
    }

    public interface Start {
        void onStart();
    }
}
