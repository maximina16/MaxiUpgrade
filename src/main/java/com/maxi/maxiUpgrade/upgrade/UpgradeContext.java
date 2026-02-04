package com.maxi.maxiUpgrade.upgrade;

/**
 * GUI render sırasında (upgrade butonu lore/isim) için gereken anlık state.
 * UpgradeButtonRenderer sadece bu getter'ları kullanır.
 */
public final class UpgradeContext {

    private final boolean stoneSystem;

    private final boolean hasTargetItem;
    private final int currentLevel;
    private final int targetLevel;
    private final boolean maxLevel;

    private final boolean hasUpgradeMaterial; // stone/parchment var mı
    private final double chancePercent;

    // Lore placeholder satırları (messages.yml / items.yml içinde kullanılıyor)
    private final String orbStatusLine;
    private final String dustStatusLine;
    private final String luckBoosterStatusLine;

    private UpgradeContext(Builder b) {
        this.stoneSystem = b.stoneSystem;
        this.hasTargetItem = b.hasTargetItem;
        this.currentLevel = b.currentLevel;
        this.targetLevel = b.targetLevel;
        this.maxLevel = b.maxLevel;
        this.hasUpgradeMaterial = b.hasUpgradeMaterial;
        this.chancePercent = b.chancePercent;
        this.orbStatusLine = b.orbStatusLine;
        this.dustStatusLine = b.dustStatusLine;
        this.luckBoosterStatusLine = b.luckBoosterStatusLine;
    }

    public boolean isStoneSystem() {
        return stoneSystem;
    }

    public boolean hasTargetItem() {
        return hasTargetItem;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public boolean isMaxLevel() {
        return maxLevel;
    }

    public boolean hasUpgradeMaterial() {
        return hasUpgradeMaterial;
    }

    public double getChancePercent() {
        return chancePercent;
    }

    public String getOrbStatusLine() {
        return orbStatusLine;
    }

    public String getDustStatusLine() {
        return dustStatusLine;
    }

    public String getLuckBoosterStatusLine() {
        return luckBoosterStatusLine;
    }

    public static Builder builder(boolean stoneSystem) {
        return new Builder(stoneSystem);
    }

    public static final class Builder {
        private final boolean stoneSystem;

        private boolean hasTargetItem;
        private int currentLevel;
        private int targetLevel;
        private boolean maxLevel;

        private boolean hasUpgradeMaterial;
        private double chancePercent;

        private String orbStatusLine = "";
        private String dustStatusLine = "";
        private String luckBoosterStatusLine = "";

        private Builder(boolean stoneSystem) {
            this.stoneSystem = stoneSystem;
        }

        public Builder hasTargetItem(boolean v) {
            this.hasTargetItem = v;
            return this;
        }

        public Builder currentLevel(int v) {
            this.currentLevel = v;
            return this;
        }

        public Builder targetLevel(int v) {
            this.targetLevel = v;
            return this;
        }

        public Builder maxLevel(boolean v) {
            this.maxLevel = v;
            return this;
        }

        public Builder hasUpgradeMaterial(boolean v) {
            this.hasUpgradeMaterial = v;
            return this;
        }

        public Builder chancePercent(double v) {
            this.chancePercent = v;
            return this;
        }

        public Builder orbStatusLine(String v) {
            this.orbStatusLine = v == null ? "" : v;
            return this;
        }

        public Builder dustStatusLine(String v) {
            this.dustStatusLine = v == null ? "" : v;
            return this;
        }

        public Builder luckBoosterStatusLine(String v) {
            this.luckBoosterStatusLine = v == null ? "" : v;
            return this;
        }

        public UpgradeContext build() {
            return new UpgradeContext(this);
        }
    }
}
