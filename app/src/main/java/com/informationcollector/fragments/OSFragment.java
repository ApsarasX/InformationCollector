package com.informationcollector.fragments;

import com.informationcollector.utils.info.SystemInfo;
import com.informationcollector.utils.type.Tuple;

import java.util.ArrayList;

public final class OSFragment extends SharedFragment {
    @Override
    protected ArrayList<Tuple> getData() {
        return SystemInfo.getOSInfo();
    }
}

