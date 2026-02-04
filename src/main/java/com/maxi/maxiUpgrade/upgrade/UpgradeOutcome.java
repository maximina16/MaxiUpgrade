// src/main/java/com/maxi/maxiUpgrade/upgrade/UpgradeOutcome.java
package com.maxi.maxiUpgrade.upgrade;

public enum UpgradeOutcome {
    SUCCESS,
    FAIL,            // level aynı kaldı
    FAIL_DOWNGRADE,  // -1
    FAIL_PROTECTED,  // protection orb ile korundu (item kırılmadı)
    FAIL_DESTROY     // item kırıldı
}
