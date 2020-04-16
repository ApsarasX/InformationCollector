package com.informationcollector;

import android.content.Context;

abstract class BaseInformation {
    Context context;

    BaseInformation(Context context) {
        this.context = context;
    }

    abstract void output();
}
