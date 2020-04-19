package com.informationcollector.utils;

import android.content.Context;

abstract class BaseInfo {
    Context context;

    BaseInfo(Context context) {
        this.context = context;
    }

    public abstract void output();
}
