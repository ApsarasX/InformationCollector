package com.informationcollector.utils.info;

import android.content.Context;

abstract class BaseInfo {
    Context context;

    BaseInfo(Context context) {
        this.context = context;
    }

    public abstract void output();
}
