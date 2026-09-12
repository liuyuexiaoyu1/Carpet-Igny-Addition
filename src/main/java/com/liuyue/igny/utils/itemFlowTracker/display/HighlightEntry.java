package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import org.jetbrains.annotations.Nullable;

public class HighlightEntry {
    @Nullable
    public TrackMark mark;

    @Nullable
    public VirtualDisplay display;

    public void dispose() {
        if (this.display != null) {
            this.display.remove();
            this.display = null;
        }
    }
}
