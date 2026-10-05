package com.liuyue.igny.helper.itemFlowTracker.core;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface TrackedStack {
    @Nullable
    List<TrackMark> igny$getMarks();

    void igny$setMarks(@Nullable List<TrackMark> marks);
}
