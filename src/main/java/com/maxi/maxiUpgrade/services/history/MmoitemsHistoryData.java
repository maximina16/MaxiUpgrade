// services/history/MmoitemsHistoryData.java
package com.maxi.maxiUpgrade.services.history;

public final class MmoitemsHistoryData {
    public final double ogValue;   // original/base (MMOItems StatHistory originalData)
    public final double gemValue;  // gemstones + external

    public MmoitemsHistoryData(double ogValue, double gemValue) {
        this.ogValue = ogValue;
        this.gemValue = gemValue;
    }
}
