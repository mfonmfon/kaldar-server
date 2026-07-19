package com.kaldar.kaldar.shared.domain.constants;

public enum BusinessType {
    SOLE_PROPRIETORSHIP("Sole Proprietorship"),
    LIMITED_LIABILITY_COMPANY("Limited Liability Company"),
    PARTNERSHIP("Partnership"),
    CORPORATION("Corporation");

    private final String displayName;

    BusinessType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
