package com.informationcollector.fragments;

import com.informationcollector.utils.info.NetworkInfo;
import com.informationcollector.utils.type.Tuple;

import java.util.ArrayList;
import java.util.Objects;

public final class NetworkFragment extends SharedFragment {
    @Override
    protected ArrayList<Tuple> getData() {
        return NetworkInfo.getGeneralNetworkInformation(Objects.requireNonNull(getContext()));
    }
}
