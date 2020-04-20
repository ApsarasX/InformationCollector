package com.informationcollector.utils.type;

import androidx.annotation.NonNull;

public class Tuple {
    private final String first;

    private final String second;

    public Tuple(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return this.first;
    }

    public String getSecond() {
        return this.second;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + this.first + ", " + this.second + ")";
    }
}
