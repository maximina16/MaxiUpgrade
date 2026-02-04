// src/main/java/com/maxi/maxiUpgrade/upgrade/UpgradeResult.java
package com.maxi.maxiUpgrade.upgrade;

public record UpgradeResult(
        int oldLevel,
        int newLevel,
        double chance,
        UpgradeOutcome outcome,
        boolean usedProtectionOrb,
        boolean usedBlessedParchment,
        boolean usedDowngradeParchment,
        int dustAmount,
        double dustBonus,
        boolean usedLuckBooster,
        double boosterMult
) {
    public int getNewLevel() { return newLevel; }
    public UpgradeOutcome getOutcome() { return outcome; }
}
