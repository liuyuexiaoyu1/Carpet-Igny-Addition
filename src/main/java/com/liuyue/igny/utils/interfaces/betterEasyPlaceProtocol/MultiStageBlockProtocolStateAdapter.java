package com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol;

import net.minecraft.world.level.block.state.BlockState;

public interface MultiStageBlockProtocolStateAdapter {
    void igny$setLoopCount(LoopContext ctx);

    int igny$toProtocolValueLoop(LoopContext ctx);

    class LoopContext {
        public BlockState stateSchematic = null;
        public BlockState stateClient = null;
        public int loopCount = 0;
        public int loopIndex = 0;
        public Object data;
    }
}