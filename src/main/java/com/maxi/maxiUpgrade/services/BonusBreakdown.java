package com.maxi.maxiUpgrade.services;

public final class BonusBreakdown {

    public final double base;
    public final double current;
    public final double gemstone;
    public final double external;
    public final double upgradeBonus;

    public BonusBreakdown(double base, double current, double gemstone, double external) {
        this.base = base;
        this.current = current;
        this.gemstone = gemstone;
        this.external = external;
        this.upgradeBonus = current - base - gemstone - external;
    }
}
