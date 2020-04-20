package com.informationcollector.fragments;

import com.informationcollector.utils.info.HardwareInfo;
import com.informationcollector.utils.type.Tuple;

import java.util.ArrayList;
import java.util.Objects;

public final class DisplayFragment extends SharedFragment {
    @Override
    protected ArrayList<Tuple> getData() {
        return HardwareInfo.getDisplayInfo(Objects.requireNonNull(getContext()));
    }
}

