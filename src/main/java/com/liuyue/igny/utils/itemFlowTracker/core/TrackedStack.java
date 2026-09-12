package com.liuyue.igny.utils.itemFlowTracker.core;

import org.jetbrains.annotations.Nullable;

public interface TrackedStack {
    @Nullable
    TrackMark igny$getMark();

    void igny$setMark(@Nullable TrackMark mark);
}
