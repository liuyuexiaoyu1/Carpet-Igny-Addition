package com.liuyue.igny.client;

import com.liuyue.igny.client.renderer.highlightBlocks.HighlightBlocksRenderer;
import net.fabricmc.api.ClientModInitializer;

public class IGNYClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IGNYClientRegister.register();
        HighlightBlocksRenderer.init();
    }
}
