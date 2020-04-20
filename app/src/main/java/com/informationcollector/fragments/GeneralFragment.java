package com.informationcollector.fragments;

import com.informationcollector.utils.info.HardwareInfo;
import com.informationcollector.utils.type.Tuple;

import java.util.ArrayList;

public final class GeneralFragment extends SharedFragment {
    @Override
    protected ArrayList<Tuple> getData() {
        return HardwareInfo.getGeneralInfo();
    }
}
