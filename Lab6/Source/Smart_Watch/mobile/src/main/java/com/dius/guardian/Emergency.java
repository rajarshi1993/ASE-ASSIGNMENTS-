package com.dius.guardian;

import java.io.Serializable;

public class Emergency implements Serializable {

    public String danger;

    @Override
    public String toString() {
        return "Danger " + danger;
    }
}
