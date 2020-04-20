package com.informationcollector.fragments;

import com.informationcollector.utils.info.HardwareInfo;
import com.informationcollector.utils.type.Tuple;

import java.util.ArrayList;
import java.util.Objects;


public final class BatteryFragment extends SharedFragment {
    @Override
    protected ArrayList<Tuple> getData() {
        return HardwareInfo.getBatteryInformation(Objects.requireNonNull(getContext()));
    }
}