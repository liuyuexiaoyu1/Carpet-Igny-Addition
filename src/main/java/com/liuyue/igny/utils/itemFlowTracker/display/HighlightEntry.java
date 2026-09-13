package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.display.VirtualDisplay;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HighlightEntry {
    @Nullable
    public TrackMark mark;

    public final List<VirtualDisplay> displays = new ArrayList<>();

    public void sync() {
        for (VirtualDisplay display : this.displays) {
            display.sync();
        }
    }

    public void dispose() {
        for (VirtualDisplay display : this.displays) {
            display.remove();
        }

        this.displays.clear();
    }
}
