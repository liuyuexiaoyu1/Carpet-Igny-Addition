package com.liuyue.igny.client;

import com.liuyue.igny.client.renderer.world.HighlightBlocksRenderer;
import com.liuyue.igny.manager.BaseDataManager;
import com.liuyue.igny.manager.EasterEggDataManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class IGNYClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IGNYClientRegister.register();
        EasterEggDataManager.INSTANCE.load();
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> HighlightBlocksRenderer.INSTANCE.clear());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, sender) -> BaseDataManager.clearAll());
    }
}
